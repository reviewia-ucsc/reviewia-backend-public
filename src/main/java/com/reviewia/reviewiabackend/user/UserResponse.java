package com.reviewia.reviewiabackend.user;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserResponse {
    private final Long id;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final UserRole role;
    private final boolean locked;
    private final boolean enabled;
    private final String avatar;
    private final int reportCount;
//    private final Favourite favouriteList;
//    private final List<Notification> notifications;

    public UserResponse(
            Long id,
            String firstName,
            String lastName,
            String email,
            UserRole role,
            boolean locked,
            boolean enabled,
            String avatar,
            int reportCount
//            Favourite favouriteList,
//            List<Notification> notifications
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.locked = locked;
        this.enabled = enabled;
        this.avatar = avatar;
        this.reportCount = reportCount;
//        this.favouriteList = favouriteList;
//        this.notifications = notifications;
    }
}
