package com.reviewia.reviewiabackend.notification;

import com.reviewia.reviewiabackend.report.ReportType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String content;
    private Long targetId;
    private ReportType type;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean markAsRead;

//    @ManyToOne
//    @JoinColumn(nullable = false, name = "user_notifi_id")
    @Column(nullable = false)
    private String createdFor;

    public Notification(String content) {
        this.content = content;
        this.markAsRead = false;
    }

    public Notification(String content, Long targetId) {
        this.content = content;
        this.targetId = targetId;
        this.markAsRead = false;
    }

    public Notification(String content, Long targetId, ReportType type, String createdFor) {
        this.content = content;
        this.targetId = targetId;
        this.type = type;
        this.markAsRead = false;
        this.createdFor = createdFor;
    }
}
