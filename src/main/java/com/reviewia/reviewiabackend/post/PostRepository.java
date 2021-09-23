package com.reviewia.reviewiabackend.post;

import com.reviewia.reviewiabackend.utils.search.GenericSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {
    Optional<Post> findByCreatedBy(String email);

    @Query("select p from Post p where p.blocked=false and p.email=:email")
    List<Post> findAllByEmail(String email);

    @Query("select p from Post p where p.blocked=false")
    List<Post> findAllPosts();

    @Query("select p from Post p where p.blocked=false")
    Page<Post> findAllPosts(Pageable paging);

    @Query("select p from Post p where p.blocked=false")
    Page<Post> findAllPosts(GenericSpecification<Post> genericSpecification, Pageable paging);

    List<Post> findAllByCategoryEquals(String category);

    Long countByCategoryAndCreatedAtBetween(String category, Timestamp start, Timestamp end);

    Long countBySubCategoryAndCreatedAtBetween(String subcategory, Timestamp start, Timestamp end);

    Long countByBlockedTrue();

    @Query(value = "select max (p.dateTime), count(p) as count from Post p where p.dateTime between :startDate and :endDate group by p.dateTime order by p.dateTime desc ")
    List<?> findChartCount(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
