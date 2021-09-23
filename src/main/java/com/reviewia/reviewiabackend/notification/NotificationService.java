package com.reviewia.reviewiabackend.notification;

import com.reviewia.reviewiabackend.report.ReportType;
import com.reviewia.reviewiabackend.user.User;
import com.reviewia.reviewiabackend.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@AllArgsConstructor
public class NotificationService {
    private NotificationRepository notificationRepository;
    private UserService userService;

    @Transactional
    public void create(String email, String type, Notification notification) {
        User user = userService.getUser(email);

        try {
            if(type.equalsIgnoreCase("p"))
                notification.setType(ReportType.POST);
            else if(type.equalsIgnoreCase("r"))
                notification.setType(ReportType.REVIEW);
            else if(type.equalsIgnoreCase("u")) {
                notification.setType(ReportType.USER);
                notification.setTargetId(user.getId());
            }

            user.setNotification(notification);
            notification.setCreatedFor(email);

            notificationRepository.save(notification);
            userService.saveUser(user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "There is a problem with creating notification");
        }
    }

    public void markAsRead(Long id) {
        try {
            notificationRepository.markAsRead(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public void markAsReadAll(String email) {
        try {
            notificationRepository.markAsReadAll(email);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public List<Notification> getAllByUser(String email) {
        try {
            return notificationRepository.getAllByCreatedForOrderByCreatedAtDesc(email);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, e.getMessage());
        }
    }

    public int getCount(String email) {
        try {
            return notificationRepository.countNotificationByCreatedForAndMarkAsRead(email, false);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, e.getMessage());
        }
    }
}
