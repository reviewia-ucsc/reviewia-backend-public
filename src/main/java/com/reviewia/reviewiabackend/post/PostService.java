package com.reviewia.reviewiabackend.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reviewia.reviewiabackend.notification.Notification;
import com.reviewia.reviewiabackend.notification.NotificationMessages;
import com.reviewia.reviewiabackend.notification.NotificationService;
import com.reviewia.reviewiabackend.post.brand.Brand;
import com.reviewia.reviewiabackend.post.brand.BrandService;
import com.reviewia.reviewiabackend.post.category.Category;
import com.reviewia.reviewiabackend.post.category.CategoryRepository;
import com.reviewia.reviewiabackend.post.category.CategoryService;
import com.reviewia.reviewiabackend.post.image.Image;
import com.reviewia.reviewiabackend.post.subCategory.SubCategory;
import com.reviewia.reviewiabackend.post.subCategory.SubCategoryRepository;
import com.reviewia.reviewiabackend.post.subCategory.SubCategoryService;
import com.reviewia.reviewiabackend.user.User;
import com.reviewia.reviewiabackend.user.UserService;
import com.reviewia.reviewiabackend.utils.aws.AmazonClient;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
public class PostService {
    private PostRepository postRepository;
    private SubCategoryService subCategoryService;
    private SubCategoryRepository subCategoryRepository;
    private CategoryService categoryService;
    private CategoryRepository categoryRepository;
    private AmazonClient amazonClient;
    private BrandService brandService;
    private UserService userService;
    private NotificationService notificationService;

    @Transactional
    public Post create(String email, Long sub_cat, String brand, String post, List<MultipartFile> images) {
        Post postJson;
        List<Image> imageList;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            postJson = objectMapper.readValue(post, Post.class);
            if(images != null && !images.isEmpty()) {
                imageList = amazonClient.uploadFile(images);
                postJson.setImgURL(imageList);
            }
            User user = userService.getUser(email);
            SubCategory subCategory = subCategoryService.getSubCategoryById(sub_cat);
            Brand brandFromDb = brandService.getByName(brand);

            if(brandFromDb == null) {
                brandFromDb = brandService.create(sub_cat, new Brand(brand));
            }

            postJson.setAvatarUrl(user.getAvatar());
            postJson.setCreatedBy(user.getFirstName() + " " + user.getLastName());
            postJson.setEmail(email);
            postJson.setSubCategory(subCategory.getSubCategoryName().toLowerCase());
            postJson.setBrand(brandFromDb);

            subCategory.getPosts().add(postJson);
            Category category = subCategory.getCategory();
            category.setPostCount(category.getPostCount() + 1);
            subCategory.setPostCount(subCategory.getPostCount() + 1);
            postJson.setCategory(category.getCategoryName().toLowerCase());

            postRepository.save(postJson);
            subCategoryRepository.save(subCategory);
            categoryRepository.save(category);
            return postJson;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.MULTI_STATUS, e.getMessage());
        }

    }

    public Post getPostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        post.incrementViewCount();
        return postRepository.save(post);
    }

    public Post getPostByIdWithoutPostCountChange(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        post.incrementViewCount();
        return postRepository.save(post);
    }

    public List<Post> getAllByCreatedBy(String email) {
        try {
            return postRepository.findAllByEmail(email);
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public List<Post> getAllByCategoryId(Long id) {
        try{
            return postRepository.findAllByCategoryEquals(categoryService.getCategoryById(id).getCategoryName());
        }catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
    }

    public List<Post> getAllBySubCategoryId(Long id) {
        return subCategoryService.getSubCategoryById(id).getPosts();
    }

    public List<Post> getAll() {
        try {
            return postRepository.findAllPosts();
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public void deletePostById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        String postSubCategory = post.getSubCategory();
        SubCategory subCategory = subCategoryRepository.findBySubCategoryName(postSubCategory).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Category category = subCategory.getCategory();
        subCategory.setPostCount(subCategory.getPostCount() - 1);
        category.setPostCount(category.getPostCount() - 1);

        subCategoryRepository.save(subCategory);
        categoryRepository.save(category);
        postRepository.deleteById(id);
    }

    public void blockPost(Long postId) {
        Post post = postRepository.getById(postId);
        Notification notification = new Notification(NotificationMessages.BLOCK_POST, postId);
        post.setBlocked(true);

        postRepository.save(post);
        notificationService.create(post.getEmail(), "p", notification);
    }

    public boolean checkPost(Long id) {
        return postRepository.findById(id).isPresent();
    }
}
