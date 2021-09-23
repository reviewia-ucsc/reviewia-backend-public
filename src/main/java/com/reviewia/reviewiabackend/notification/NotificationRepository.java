package com.reviewia.reviewiabackend.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> getAllByCreatedForOrderByCreatedAtDesc(String email);

    @Transactional
    @Modifying
    @Query("UPDATE Notification a SET a.markAsRead = TRUE WHERE a.id = ?1")
    void markAsRead(Long id);

    @Transactional
    @Modifying
    @Query("UPDATE Notification a SET a.markAsRead = TRUE WHERE a.createdFor = ?1")
    void markAsReadAll(String email);

    int countNotificationByCreatedForAndMarkAsRead(String email, boolean isRead);
}
