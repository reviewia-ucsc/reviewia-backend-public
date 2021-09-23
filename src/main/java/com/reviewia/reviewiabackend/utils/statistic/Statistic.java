package com.reviewia.reviewiabackend.utils.statistic;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Statistic {
    private Long users;
    private Long activeUsers;
    private Long posts;
    private Long blockedPosts;
    private Long reviews;
    private Long blockedReviews;
    private Long categories;
    private Long subCategories;
    private Long chatGroups;
    private Long messages;
}
