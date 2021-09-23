package com.reviewia.reviewiabackend.post;

import com.reviewia.reviewiabackend.post.brand.Brand;
import com.reviewia.reviewiabackend.post.brand.BrandRepository;
import com.reviewia.reviewiabackend.utils.search.GenericSpecification;
import com.reviewia.reviewiabackend.utils.search.SearchCriteria;
import com.reviewia.reviewiabackend.utils.search.SearchOperation;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api")
public class PostController {

    private PostService postService;
    private PostRepository postRepository;
    private BrandRepository brandRepository;

    // create new post
    @PostMapping(value = "/user/post/create",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Post> createPost(@RequestParam String email,
                                        @RequestParam Long subcategory,
                                        @RequestParam String brand,
                                        @RequestPart("post") String post,
                                        @RequestPart(value = "image", required = false) List<MultipartFile> images) {
        return ResponseEntity.status(201).body(postService.create(email, subcategory, brand, post, images));
    }

    // delete by post id
    @DeleteMapping("/user/post")
    public ResponseEntity<?> deletePost(@RequestParam("id") Long id) {
        postService.deletePostById(id);
        return ResponseEntity.ok().build();
    }

    // get all posts by user email
    @GetMapping("/user/posts")
    public ResponseEntity<List<Post>> getAllPostsByUserId(@RequestParam String email) {
        return ResponseEntity.ok(postService.getAllByCreatedBy(email));
    }

    /*
               PUBLIC END POINTS
    */

    // get all posts details
    @GetMapping("/public/posts/all")
    public ResponseEntity<List<Post>> getAll() {
        return ResponseEntity.ok(postService.getAll());
    }

    // get posts using filter
    @GetMapping("/public/posts")
    public ResponseEntity<Map<String, Object>> getAllPosts(@RequestParam(required = false) String search,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size,
                                                           @RequestParam(defaultValue = "updatedAt") String sort,
                                                           @RequestParam(defaultValue = "desc") String order) {
        try {
            List<Post> posts;
            Pageable paging;

            paging = order.equalsIgnoreCase("asc")
                    ? PageRequest.of(page, size, Sort.by(sort).ascending())
                    : PageRequest.of(page, size, Sort.by(sort).descending());

            Page<Post> pagePosts;
            if (search == null)
                pagePosts = postRepository.findAllPosts(paging);
            else {
                GenericSpecification<Post> genericSpecification = new GenericSpecification<>();

                String[] array = search.split(",");

                for (String field : array) {
                    if (field.contains(":")) {
                        String[] a = field.split(":");
                        if (a[0].equalsIgnoreCase("brand")) {
                            Brand brand = brandRepository.findBrandByNameContaining(a[1]);
                            genericSpecification.add(new SearchCriteria(a[0], brand, SearchOperation.EQUAL));
                        } else
                            genericSpecification.add(new SearchCriteria(a[0], a[1], SearchOperation.MATCH));
                    } else if (field.contains(">")) {
                        String[] a = field.split(">");
                        genericSpecification.add(new SearchCriteria(a[0], a[1], SearchOperation.GREATER_THAN_EQUAL));
                    } else if (field.contains("<")) {
                        String[] a = field.split("<");
                        genericSpecification.add(new SearchCriteria(a[0], a[1], SearchOperation.LESS_THAN_EQUAL));
                    }
                    else if (field.contains("=")) {
                        String[] a = field.split("=");
                        genericSpecification.add(new SearchCriteria(a[0], a[1], SearchOperation.EQUAL));
                    }
                }
                genericSpecification.add(new SearchCriteria("blocked",false, SearchOperation.EQUAL));
                pagePosts = postRepository.findAll(genericSpecification, paging);
            }

            posts = pagePosts.getContent();

            Map<String, Object> response = new HashMap<>();
            response.put("posts", posts);
            response.put("currentPage", pagePosts.getNumber());
            response.put("totalItems", pagePosts.getTotalElements());
            response.put("totalPages", pagePosts.getTotalPages());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // get by post id
    @GetMapping("/public/post")
    public ResponseEntity<Post> getPost(@RequestParam("id") Long id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    // get all posts by category id
    @GetMapping("/public/post/category")
    public ResponseEntity<List<Post>> getAllPostsByCategory(@RequestParam("id") Long id) {
        return ResponseEntity.ok(postService.getAllByCategoryId(id));
    }

    // get all posts by sub category
    @GetMapping("/public/post/category/sub")
    public ResponseEntity<List<Post>> getAllPostsBySubCategory(@RequestParam("id") Long id) {
        return ResponseEntity.ok(postService.getAllBySubCategoryId(id));
    }
}
