package com.reviewia.reviewiabackend.post;

import com.reviewia.reviewiabackend.post.brand.Brand;
import com.reviewia.reviewiabackend.post.brand.BrandService;
import com.reviewia.reviewiabackend.post.category.Category;
import com.reviewia.reviewiabackend.post.category.CategoryService;
import com.reviewia.reviewiabackend.post.category.CategoryView;
import com.reviewia.reviewiabackend.post.subCategory.SubCategory;
import com.reviewia.reviewiabackend.post.subCategory.SubCategoryService;
import com.reviewia.reviewiabackend.post.subCategory.SubCategoryView;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api")
public class CategoryController {

    private final CategoryService categoryService;
    private final SubCategoryService subCategoryService;
    private final BrandService brandService;

    // create new brand by giving sub category id
    @PostMapping("/user/brand")
    public ResponseEntity<Brand> createBrand(@RequestParam Long id, @RequestBody Brand brand) {
        return ResponseEntity.ok(brandService.create(id, brand));
    }

    /*
            PUBLIC END POINTS
     */

    // get all sub+main categories
    @GetMapping("/public/category/all")
    public ResponseEntity<List<Category>> getAll() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    // get category names
    @GetMapping("/public/category/names")
    public ResponseEntity<List<CategoryView>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    // get category by category id
    @GetMapping("/public/category")
    public ResponseEntity<Category> getCategoryById(@RequestParam("id") Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    // get subcategory names
    @GetMapping("/public/subcategory/names")
    public ResponseEntity<List<SubCategoryView>> getAllSubCategories() {
        return ResponseEntity.ok(subCategoryService.getAllSubCategories());
    }

    // get subcategory by category id
    @GetMapping("/public/category/{id}/subcategory")
    public ResponseEntity<Category> getAllSubCategoriesByCategoryId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(categoryService.getCategoryAndSubCategoryById(id));
    }

    // get sub category by subcategory id
    @GetMapping("/public/subcategory")
    public ResponseEntity<SubCategory> getSubCategoryById(@RequestParam("id") Long id) {
        return ResponseEntity.ok(subCategoryService.getSubCategoryById(id));
    }

    // get brand list by sub-category id
    @GetMapping("/public/subcategory/{id}/brands")
    public ResponseEntity<List<Brand>> getBrandsBySubCategoryById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(subCategoryService.getBrandsBySubCategory(id));
    }

    @GetMapping("/public/category/subcategory/names")
    public ResponseEntity<List<Category>> getAllCategoryAndSubCategories() {
        return ResponseEntity.ok(categoryService.getCategoriesAndSubCategories());
    }

    /*
        Admin endpoints
     */

    // create new category
    @PostMapping("/admin/category")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        return ResponseEntity.ok(categoryService.saveCategory(category));
    }

    // create new subCategory by giving category id
    @PostMapping("/admin/subcategory")
    public ResponseEntity<SubCategory> createSubCategory(@RequestParam Long id, @RequestBody SubCategory subCategory) {
        return ResponseEntity.ok(subCategoryService.create(id, subCategory));
    }

    // update category name by giving category id
    @PatchMapping("/admin/category/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable("id") Long id, @RequestBody Category category) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.updateCategoryNameById(id, category));
    }

    // update sub category name by id
    @PutMapping("/admin/subcategory/{id}")
    public ResponseEntity<SubCategory> updateSubCategoryById(@PathVariable("id") Long id, @RequestBody SubCategory subCategory) {
        return ResponseEntity.ok(subCategoryService.updateSubCategoryNameById(id, subCategory));
    }

    //  delete category by id
    @DeleteMapping("/admin/category/{id}")
    public ResponseEntity<?> deleteCategoryById(@PathVariable("id") Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    //    delete sub category by id
    @DeleteMapping("/admin/subcategory/{id}")
    public ResponseEntity<?> deleteSubCategoryById(@PathVariable("id") Long id) {
        subCategoryService.deleteSubCategory(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
