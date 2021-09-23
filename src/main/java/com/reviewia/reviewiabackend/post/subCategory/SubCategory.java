package com.reviewia.reviewiabackend.post.subCategory;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.reviewia.reviewiabackend.post.Post;
import com.reviewia.reviewiabackend.post.brand.Brand;
import com.reviewia.reviewiabackend.post.category.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class SubCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long subCategoryId;
    private String subCategoryName;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(nullable = false, name = "category_fk_id")
    private Category category;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany
    @JoinColumn(name = "fk_sub_cat")
    private List<Post> posts;

    private int postCount;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany
    private List<Brand> brandList = new ArrayList<>();

    public void addNewBrand(Brand brand) {
        this.brandList.add(brand);
    }

    public SubCategory(String subCategoryName, Category category) {
        this.subCategoryName = subCategoryName;
        this.category = category;
    }

    public SubCategory(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }
}
