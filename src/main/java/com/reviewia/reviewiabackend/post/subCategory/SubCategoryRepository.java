package com.reviewia.reviewiabackend.post.subCategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
    Optional<SubCategory> findBySubCategoryName(String subCategoryName);

    @Query(value = "select c.subCategoryId as subCategoryId, c.subCategoryName as subCategoryName, c.postCount as postCount from SubCategory c")
    List<SubCategoryView> findAllSubCategories();
}
