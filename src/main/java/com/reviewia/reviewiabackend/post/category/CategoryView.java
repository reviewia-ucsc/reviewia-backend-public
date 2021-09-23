package com.reviewia.reviewiabackend.post.category;

public interface CategoryView {
    Long getCategoryId();
    String getCategoryName();
    String getType();
    int getPostCount();
}
