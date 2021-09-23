package com.reviewia.reviewiabackend.chat.message;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @CreationTimestamp
    private LocalDateTime createdAt;

//    @OneToOne
//    @JoinColumn(name = "fk_user")
//    private User user;

    @Column(nullable = false)
    private String createdBy;

    @Column(nullable = false)
    private String avatar;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    public Message(String content) {
        this.content = content;
    }
}
