package com.reviewia.reviewiabackend.chat.group;

import com.reviewia.reviewiabackend.chat.message.Message;
import com.reviewia.reviewiabackend.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChatGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private boolean isActive;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Long postId;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToOne
    @JoinColumn(name = "fk_createdBy")
    private User createdBy;                                // uni directional one to one

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany
    private List<User> users;                              // uni directional many to many

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany
    @JoinColumn(name = "fk_group")
    private List<Message> messages = new ArrayList<>();    // uni directional one to many

    public void setMessage(Message message) {
        this.messages.add(message);
    }

    public void addUser(User user) {
        if(!users.contains(user))
        this.users.add(user);
    }

    public ChatGroup(Long postId, User createdBy,List<User> users) {
        this.postId = postId;
        this.createdBy = createdBy;
        this.isActive = true;
        this.users = users;
    }
}
