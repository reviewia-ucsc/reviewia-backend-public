package com.reviewia.reviewiabackend.post.category;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.reviewia.reviewiabackend.post.subCategory.SubCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long categoryId;
    @Column(nullable = false, unique = true)
    private String categoryName;
    @Column(nullable = false)
    private String type;

    private int postCount;

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER, mappedBy = "category")
//    @OneToMany
//    @JoinColumn(name = “fk_subcatego”)
    private List<SubCategory> subCategoryList;

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }

    public Category(String categoryName, String type) {
        this.categoryName = categoryName;
        this.type = type;
    }

    public Category(String categoryName, List<SubCategory> subCategoryList) {
        this.categoryName = categoryName;
        this.subCategoryList = subCategoryList;
    }
}
