package com.reviewia.reviewiabackend.post.subCategory;

import com.reviewia.reviewiabackend.post.brand.Brand;
import com.reviewia.reviewiabackend.post.category.Category;
import com.reviewia.reviewiabackend.post.category.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Service
public class SubCategoryService {
    private final SubCategoryRepository subCategoryRepository;
    private final CategoryService categoryService;

    public SubCategoryService(SubCategoryRepository subCategoryRepository, CategoryService categoryService) {
        this.subCategoryRepository = subCategoryRepository;
        this.categoryService = categoryService;
    }

    public SubCategory create(Long categoryId, SubCategory subCategory) {
        Category category = categoryService.getCategoryById(categoryId);
        category.getSubCategoryList().forEach(subCategory1 -> {
            if(Objects.equals(subCategory1.getSubCategoryName(), subCategory.getSubCategoryName())) throw new ResponseStatusException(HttpStatus.CONFLICT, "Already exists");
        });
        subCategory.setCategory(category);
        return subCategoryRepository.save(subCategory);
    }

    public SubCategory getSubCategoryByName(String name) {
        return subCategoryRepository.findBySubCategoryName(name).orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT));
    }

    public SubCategory getSubCategoryById(Long id) {
        return subCategoryRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT));
    }

    public List<SubCategory> getAll() {
        return subCategoryRepository.findAll();
    }

    public List<SubCategoryView> getAllSubCategories() {
        return subCategoryRepository.findAllSubCategories();
    }

    public SubCategory updateSubCategoryNameById(Long id, SubCategory subCategory) {
        SubCategory subcategoryFromDb = subCategoryRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT));
        subcategoryFromDb.setSubCategoryName(subCategory.getSubCategoryName());
        return subCategoryRepository.save(subcategoryFromDb);
    }

    public List<Brand> getBrandsBySubCategory(Long id) {
        SubCategory subCategory = subCategoryRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT));
        return subCategory.getBrandList();
    }

    public void deleteSubCategory(Long id) {
        try {
            subCategoryRepository.deleteById(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
    }
}
