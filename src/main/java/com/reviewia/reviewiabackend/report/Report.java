package com.reviewia.reviewiabackend.report;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String reportedBy;

    @Column(columnDefinition = "text", nullable = false)
    private String reason;

    @Column(nullable = false)
    private ReportType reportType;

    @Column(nullable = false)
    private Long subjectId;

//    private String subjectEmail;

    @Column(nullable = false)
    private boolean isProcessed;

    @UpdateTimestamp
    private LocalDateTime processedAt;

    public Report(String reportedBy, String reason) {
        this.reportedBy = reportedBy;
        this.reason = reason;
        this.isProcessed = false;
    }
}
