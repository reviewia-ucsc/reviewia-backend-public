package com.reviewia.reviewiabackend.user;

public interface UserView {
    Long getId();
    String getEmail();
    String getFirstName();
    String getLastName();
    String getAvatar();
    int getReportCount();
}
