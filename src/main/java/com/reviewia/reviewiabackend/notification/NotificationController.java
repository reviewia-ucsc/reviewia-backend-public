package com.reviewia.reviewiabackend.notification;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/user/notification")
public class NotificationController {
    private NotificationService notificationService;

    @PostMapping
    public ResponseEntity<?> create(
            @RequestParam("email") String email,
            @RequestParam("type") String type,
            @RequestBody Notification notification
    ) {
        notificationService.create(email, type, notification);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/edit")
    public ResponseEntity<?> markAsRead(@RequestParam("id") Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/edit/all")
    public ResponseEntity<?> markAsReadAll(@RequestParam("email") String email) {
        notificationService.markAsReadAll(email);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications(@RequestParam("email") String email) {
        return ResponseEntity.ok(notificationService.getAllByUser(email));
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getNotificationCount(@RequestParam("email") String email) {
        return ResponseEntity.ok(notificationService.getCount(email));
    }
}
