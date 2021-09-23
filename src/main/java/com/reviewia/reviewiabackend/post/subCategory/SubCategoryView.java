package com.reviewia.reviewiabackend.post.subCategory;

import com.reviewia.reviewiabackend.post.brand.Brand;

import java.util.List;

public interface SubCategoryView {
    Long getSubCategoryId();
    String getSubCategoryName();
    int getPostCount();
}
