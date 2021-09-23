package com.reviewia.reviewiabackend.post.brand;

import com.reviewia.reviewiabackend.post.subCategory.SubCategory;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

//    @JsonBackReference
//    @ManyToMany
//    @JoinColumn(nullable = false, name = "subcategory_fk_id")
//    private Set<SubCategory> subCategory = new HashSet<>();

    public Brand(String name) {
        this.name = name;
    }

//    public void addNewSubCategory(SubCategory subCategory) {
//        this.subCategory.add(subCategory);
//    }

    public Brand(String name, SubCategory subCategory) {
        this.name = name;
//        this.subCategory.add(subCategory);
    }

    public Brand() {

    }
}
