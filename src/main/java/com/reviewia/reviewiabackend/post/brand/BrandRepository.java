package com.reviewia.reviewiabackend.post.brand;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
//    List<Brand> getBrandBySubCategory(SubCategory subCategory);
    Brand findBrandByNameContaining(String name);
    Brand findBrandByName(String name);
}
