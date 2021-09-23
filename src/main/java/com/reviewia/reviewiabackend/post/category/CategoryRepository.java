package com.reviewia.reviewiabackend.post.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByCategoryName(String name);

    @Query(value = "select c.categoryId as categoryId, c.categoryName as categoryName, c.type as type, c.postCount as postCount from Category c")
    List<CategoryView> findAllWithoutPosts();
}
