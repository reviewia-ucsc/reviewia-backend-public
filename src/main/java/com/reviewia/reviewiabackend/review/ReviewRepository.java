package com.reviewia.reviewiabackend.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("select r from Review r where r.isBlocked=false")
    List<Review> findAllReviews();

    @Query("select r from Review r where r.isBlocked=false and r.email=:email")
    List<Review> findAllByEmail(String email);

    @Query("select r from Review r where r.isBlocked=false and r.postId=:id")
    List<Review> findAllByPostId(Long id);

    Long countByCreatedAtBetween(Timestamp start, Timestamp end);

    @Transactional
    @Modifying
    @Query("UPDATE Review a " + "SET a.isBlocked = TRUE WHERE a.reviewId = ?1")
    void blockReview(Long id);

    Long countByIsBlockedTrue();

    @Query(value = "select max (r.dateTime), count(r) as count from Review r where r.dateTime between :startDate and :endDate group by r.dateTime order by r.dateTime desc ")
    List<?> findChartCount(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
