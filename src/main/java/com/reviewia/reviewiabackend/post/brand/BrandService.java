package com.reviewia.reviewiabackend.post.brand;

import com.reviewia.reviewiabackend.post.subCategory.SubCategory;
import com.reviewia.reviewiabackend.post.subCategory.SubCategoryRepository;
import com.reviewia.reviewiabackend.post.subCategory.SubCategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@AllArgsConstructor
public class BrandService {
    private BrandRepository brandRepository;
    private SubCategoryService subCategoryService;
    private SubCategoryRepository subCategoryRepository;

    @Transactional
    public Brand create(Long id, Brand brand) {
        SubCategory subCategory = subCategoryService.getSubCategoryById(id);
        Brand brand1 = brandRepository.findBrandByName(brand.getName());

        try {
            if(brand1 == null) {
                subCategory.addNewBrand(brand);
                brandRepository.save(brand);
                subCategoryRepository.save(subCategory);
                return brand;
            }
            else {
                if(!subCategory.getBrandList().contains(brand1)) {
                    subCategory.addNewBrand(brand1);
                    subCategoryRepository.save(subCategory);
                }
                return brand1;
            }
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, e.getMessage());
        }
    }

    public Brand getById(Long id) {
        return brandRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Brand getByName(String name) {
        return brandRepository.findBrandByName(name);
    }

//    public List<Brand> getBrandsBySubCategoryId(Long id) {
//        SubCategory subCategory = subCategoryService.getSubCategoryById(id);
//        return brandRepository.getBrandBySubCategory(subCategory);
//    }
}
