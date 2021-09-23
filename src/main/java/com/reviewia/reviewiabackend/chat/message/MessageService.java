package com.reviewia.reviewiabackend.chat.message;

import com.reviewia.reviewiabackend.chat.group.ChatGroup;
import com.reviewia.reviewiabackend.chat.group.ChatGroupRepository;
import com.reviewia.reviewiabackend.chat.group.ChatGroupService;
import com.reviewia.reviewiabackend.notification.NotificationMessages;
import com.reviewia.reviewiabackend.notification.NotificationRepository;
import com.reviewia.reviewiabackend.report.ReportType;
import com.reviewia.reviewiabackend.user.User;
import com.reviewia.reviewiabackend.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
public class MessageService {
    private MessageRepository messageRepository;
    private ChatGroupService chatGroupService;
    private ChatGroupRepository chatGroupRepository;
    private NotificationRepository notificationRepository;
    private UserService userService;

    @Transactional
    public Message create(String email, Long grpId, Message message) {
        try {
            User u = userService.getUser(email);
            message.setCreatedBy(email);
            ChatGroup chatGroup = chatGroupService.findByGroupId(grpId);
            if (chatGroup == null) throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No chat group found");
            if(!chatGroup.isActive()) throw new ResponseStatusException(HttpStatus.EXPECTATION_FAILED, "Requested group has been deactivated");

            message.setAvatar(u.getAvatar());
            message.setFullName(u.getFirstName() + " " + u.getLastName());
            chatGroup.setMessage(message);

//        chatGroup.getCreatedBy().triggerNotification(notificationRepository, NotificationMessages.NEW_CHAT_MSG, ReportType.GROUP, chatId);
            chatGroup.getUsers().forEach(user -> {
                user.triggerNotification(notificationRepository, NotificationMessages.NEW_CHAT_MSG, ReportType.GROUP, grpId);
            });
            messageRepository.save(message);
            chatGroupRepository.save(chatGroup);
            return message;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
