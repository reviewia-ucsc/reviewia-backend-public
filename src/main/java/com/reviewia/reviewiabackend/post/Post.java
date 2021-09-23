package com.reviewia.reviewiabackend.post;

import com.reviewia.reviewiabackend.post.brand.Brand;
import com.reviewia.reviewiabackend.post.image.Image;
import com.reviewia.reviewiabackend.review.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long postId;
    @Column(precision = 18, scale = 2)
    private float totalRate = 0.0f;
    @Column(precision = 18, scale = 2)
    private float rate = 0.0f;
    private int viewCount = 0;

    @Column(nullable = false)
    private String title;

//    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_image")
    private List<Image> imgURL = new ArrayList<>();

    @Column(columnDefinition = "text")
    private String description;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;

    @CreationTimestamp
    private LocalDate dateTime;

    @NotNull
    private String type;
    private int reviewCount = 0;

    @Column(nullable = false)
    private String createdBy;

    @Column(nullable = false)
    private String email;

    private boolean blocked = false;

    @Column(nullable = false)
    private String subCategory;

    @Column(nullable = false)
    private String category;

    private String avatarUrl;

    @OneToOne
    @JoinColumn(name = "fk_brand")
    private Brand brand;

    //    *************************************************
//    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_review")
    private List<Review> reviews = new ArrayList<>();
    //    *************************************************

    public void setReview(Review review) {
        this.reviews.add(review);
    }

    public void incrementViewCount() {
        this.viewCount += 1;
    }

    public void incrementTotalRate(float rate) {
        this.totalRate += rate;
    }

    public void decrementTotalRate(float rate) {
        if(this.totalRate - rate > 0) {
            this.totalRate = this.totalRate - rate;
        }
        else this.totalRate = 0;
//        this.totalRate = this.totalRate - rate > 0 ? this.totalRate - rate : this.totalRate;

    }

    public Post(String title, String description, String createdBy, String type) {
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
        this.type = type;
    }

    public Post(String title, String description, String createdBy, String type, String subCategory) {
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
        this.type = type;
        this.subCategory = subCategory;
//        this.brand = brand;
    }
}
