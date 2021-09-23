package com.reviewia.reviewiabackend.user;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.reviewia.reviewiabackend.notification.Notification;
import com.reviewia.reviewiabackend.notification.NotificationRepository;
import com.reviewia.reviewiabackend.post.favourite.Favourite;
import com.reviewia.reviewiabackend.registration.token.ConfirmationToken;
import com.reviewia.reviewiabackend.report.ReportType;
import com.reviewia.reviewiabackend.review.Review;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Entity
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private String avatar;

    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;
    private boolean locked = false;
    private boolean enabled = false;
    private int reportCount;

    @JsonBackReference
    @OneToMany(mappedBy = "user")
    private List<ConfirmationToken> confirmationTokens;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany
    @JoinColumn(name = "fk_notification")
    private List<Notification> notifications = new ArrayList<>();

    @JsonManagedReference
    @OneToOne
    @JoinColumn(name = "fk_favouriteList")
    private Favourite favouriteList;

//    *************************************************

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany
    @JoinColumn(name = "fk_reviewList", referencedColumnName = "id")
    private List<Review> reviews = new ArrayList<>();

    public void setReview(Review review) {
        this.reviews.add(review);
    }

    public void setNotification(Notification notification) {
        this.notifications.add(notification);
    }

//    *************************************************


    public User(String firstName, String lastName, String email, String password, UserRole role, String avatarUrl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.avatar = avatarUrl;
    }

    public User(
            String firstName,
            String lastName,
            String email,
            String password,
            UserRole role,
            boolean locked,
            boolean enabled,
            Favourite favouriteList
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.locked = locked;
        this.enabled = enabled;
        this.favouriteList = favouriteList;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public int incrementReportCount() {
        this.reportCount += 1;
        return this.reportCount;
    }

    public void triggerNotification(NotificationRepository notificationRepository, String content, ReportType type, Long targetId) {
        Notification notification = new Notification(content, targetId, type, this.email);
        notificationRepository.save(notification);
        this.setNotification(notification);
    }
}
