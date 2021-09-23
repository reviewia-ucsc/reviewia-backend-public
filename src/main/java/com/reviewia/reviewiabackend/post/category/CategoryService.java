package com.reviewia.reviewiabackend.post.category;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

@Service
@AllArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Category saveCategory(Category category) {
        try {
            return categoryRepository.save(category);
        }catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Category name exists");
        }
    }

    public Category getCategoryByName(String name) {
        return categoryRepository.findByCategoryName(name).orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT));
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT));
    }

    public Category getCategoryAndSubCategoryById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT));
        category.getSubCategoryList().forEach(subCategory -> {
            subCategory.setPosts(null);
//            subCategory.setBrandList(null);
        });
        return category;
    }

    public List<Category> getCategoriesAndSubCategories() {
        List<Category> categoryList = categoryRepository.findAll();
        categoryList.forEach(category -> {
            category.getSubCategoryList().forEach(subCategory -> {
                subCategory.setPosts(null);
//                subCategory.setBrandList(null);
            });
        });

        return categoryList;
    }

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public List<CategoryView> getAllCategories() {
        return categoryRepository.findAllWithoutPosts();
    }

    public Category updateCategoryNameById(Long id, Category category) {
        try {
            Category categoryFromDb = categoryRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT));
            categoryFromDb.setCategoryName(category.getCategoryName());
            return categoryRepository.save(categoryFromDb);
        }catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Category name exists");
        }
    }

    public void deleteCategory(Long id) {
        try {
            categoryRepository.deleteById(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
    }
}
