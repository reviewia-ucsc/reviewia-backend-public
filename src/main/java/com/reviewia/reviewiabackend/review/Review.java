package com.reviewia.reviewiabackend.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long reviewId;
    @Column(nullable = false)
    private String reviewedBy;  // name
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private Long postId;
    @CreationTimestamp
    private Timestamp createdAt;
    @UpdateTimestamp
    private Timestamp updatedAt;
    @CreationTimestamp
    private LocalDate dateTime;
    @Column(nullable = false, columnDefinition = "text")
    private String description;
    @Column(nullable = false)
    private float userRate;
    private int likeCount = 0;
    private int dislikeCount = 0;
    private int reportCount = 0;
    private float sentimentRate = 0.0f;
    private float finalRate;
    private boolean isBlocked = false;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany
    private List<ReactedUser> dislikedList = new ArrayList<>();
    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany
    private List<ReactedUser> likedList = new ArrayList<>();

    public void addLikedUser(ReactedUser user) {
        if(!likedList.contains(user))
            likedList.add(user);
    }

    public void addDislikedUser(ReactedUser user) {
        if(!dislikedList.contains(user))
            dislikedList.add(user);
    }

    public void removeReaction(ReactedUser user) {
        if(likedList.remove(user)) return;
        dislikedList.remove(user);
    }

    public Review(String reviewedBy, Long postId, String description, float userRate) {
        this.reviewedBy = reviewedBy;
        this.postId = postId;
        this.description = description;
        this.userRate = userRate;
    }
}
