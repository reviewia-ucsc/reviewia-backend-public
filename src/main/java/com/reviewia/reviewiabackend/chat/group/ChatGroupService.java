package com.reviewia.reviewiabackend.chat.group;

import com.reviewia.reviewiabackend.notification.Notification;
import com.reviewia.reviewiabackend.notification.NotificationMessages;
import com.reviewia.reviewiabackend.notification.NotificationRepository;
import com.reviewia.reviewiabackend.report.ReportType;
import com.reviewia.reviewiabackend.user.User;
import com.reviewia.reviewiabackend.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
@EnableScheduling
public class ChatGroupService {
    private ChatGroupRepository chatGroupRepository;
    private UserService userService;
    private NotificationRepository notificationRepository;

    @Transactional
    public ChatGroup create(String email, Long postId, List<String> userMailList) {
        User owner = userService.getUser(email);
        ChatGroup chatGroup = chatGroupRepository.findByCreatedUser(owner);
        if (chatGroup != null && chatGroup.isActive())
            throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Previous group is currently active. Deactivate it before creating new group");
        List<User> users = new ArrayList<>();
        List<Notification> notifications = new ArrayList<>();

        userMailList.forEach(mail -> {
            mail = mail.toLowerCase();
            if (!Objects.equals(mail, email.toLowerCase()))
                users.add(userService.getUser(mail.toLowerCase()));
        });

        try {
            chatGroup = chatGroupRepository.save(new ChatGroup(postId, owner, users));
            for (User u : users) {
                notifications.add(new Notification(NotificationMessages.NEW_CHAT_GROUP, chatGroup.getId(), ReportType.GROUP, u.getEmail()));
            }
            users.add(owner);
            notificationRepository.saveAll(notifications);
            return chatGroup;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public ChatGroup findByGroupId(Long id) {
        try {
            ChatGroup chatGroup = chatGroupRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT));
            chatGroup.setCreatedBy(modify(chatGroup.getCreatedBy()));
            chatGroup.getUsers().forEach(this::modify);
            return chatGroup;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
    }

    public ChatGroup findByCreatedUser(String email) {
        try {
            ChatGroup chatGroup = chatGroupRepository.findByCreatedUser(userService.getUser(email));
            chatGroup.setCreatedBy(modify(chatGroup.getCreatedBy()));
            chatGroup.getUsers().forEach(this::modify);
            return chatGroup;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Zero chat groups available");
        }
    }

    // todo : fix the time complexity
    public List<ChatGroup> findAllByUser(String email) {
        try {
            List<ChatGroup> chatGroups = chatGroupRepository.findAllByUsersContains(userService.getUser(email));
            chatGroups.forEach(chatGroup -> {
                chatGroup.setCreatedBy(modify(chatGroup.getCreatedBy()));
                chatGroup.getUsers().forEach(this::modify);
            });
            return chatGroups;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
    }

    public void deactivate(String email, Long id) {
        if (email != null) {
            ChatGroup chatGroup = chatGroupRepository.findByCreatedUser(userService.getUser(email));
            if (chatGroup == null)
                throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Zero chat groups available");
            checkGrpStatus(id, chatGroup);
            return;
        } else if (id != null) {
            ChatGroup chatGroup = chatGroupRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT));
            checkGrpStatus(id, chatGroup);
            return;
        }
        throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "Check given parameters");
    }

    private void checkGrpStatus(Long id, ChatGroup chatGroup) {
        if (!chatGroup.isActive()) throw new ResponseStatusException(HttpStatus.CONFLICT, "Already deactivated");
        chatGroup.setActive(false);

        chatGroup.getUsers().forEach(user -> user.triggerNotification(
                notificationRepository,
                NotificationMessages.CHAT_GROUP_DEACTIVATED,
                ReportType.GROUP,
                id
        ));

        chatGroupRepository.save(chatGroup);
    }

    @Transactional
    public ChatGroup addUsersToGroup(Long id, List<String> userMailList) {
        if (!chatGroupRepository.findById(id).isPresent()) throw new ResponseStatusException(HttpStatus.NO_CONTENT);

        ChatGroup chatGroup = chatGroupRepository.findById(id).get();
        if (!chatGroup.isActive())
            throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "This group has been disabled");

        userMailList.forEach(mail -> {
            User user = userService.getUser(mail);
            if (!chatGroup.getUsers().contains(user)) {
                user.triggerNotification(notificationRepository, NotificationMessages.NEW_CHAT_GROUP, ReportType.GROUP, id);
                chatGroup.addUser(user);
            }
        });
        return chatGroupRepository.save(chatGroup);
    }

    public ChatGroup removeUsersFromGroup(Long id, List<String> userMailList) {
        if (!chatGroupRepository.findById(id).isPresent()) throw new ResponseStatusException(HttpStatus.NO_CONTENT);

        ChatGroup chatGroup = chatGroupRepository.findById(id).get();
        if (!chatGroup.isActive())
            throw new ResponseStatusException(HttpStatus.PRECONDITION_FAILED, "This group has been disabled");

        userMailList.forEach(mail -> {
            User user = userService.getUser(mail);
            chatGroup.getUsers().remove(user);
        });
        return chatGroupRepository.save(chatGroup);
    }

    public User modify(User user) {
//        user.setFavouriteList(null);
//        user.setPassword(null);
//        user.setReviews(null);
        return user;
    }

    @Scheduled(cron = "0 0 * * * ?")            // At 00:00 on everyday
    public void autoDeactivate() {
        List<ChatGroup> chatGroups = chatGroupRepository.findAll();
        if (chatGroups.isEmpty()) return;

        Date date = new Date();
        LocalDateTime timestamp = new Timestamp(date.getTime()).toLocalDateTime();

        chatGroups.forEach(chatGroup -> {
            if (ChronoUnit.DAYS.between(chatGroup.getCreatedAt(), timestamp.plusDays(5)) >= 7 && chatGroup.isActive()) {
                chatGroup.setActive(false);
                chatGroup.getUsers().forEach(user -> {
                    user.triggerNotification(notificationRepository, NotificationMessages.CHAT_GROUP_DEACTIVATED, ReportType.GROUP, chatGroup.getId());
                });
            }
        });
        chatGroupRepository.saveAll(chatGroups);
    }
}
