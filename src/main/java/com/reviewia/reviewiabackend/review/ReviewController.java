package com.reviewia.reviewiabackend.review;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/user/review")
    public ResponseEntity<Review> create(@RequestParam("email") String email, @RequestBody Review review) {
        return ResponseEntity.status(201).body(reviewService.create(email, review));
    }

    @PutMapping("/user/review")
    public ResponseEntity<Review> create(@RequestParam("email") String email, Long id, @RequestBody Review review) {
        return ResponseEntity.status(201).body(reviewService.update(email, id, review));
    }

    @GetMapping("/user/review/all")
    public ResponseEntity<List<Review>> getAll() {
        return ResponseEntity.ok(reviewService.getAll());
    }

    @GetMapping("/public/review/all")
    public ResponseEntity<List<Review>> getAllByPostId(@RequestParam("id") Long id) {
        return ResponseEntity.ok(reviewService.getAllByPostId(id));
    }

    @GetMapping("/user/review/{id}")
    public ResponseEntity<Review> getByReviewId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(reviewService.getById(id));
    }

    @GetMapping("/user/review")
    public ResponseEntity<List<Review>> getAllByEmail(@RequestParam("email") String email) {
        return ResponseEntity.ok(reviewService.getAllByEmail(email));
    }

    @GetMapping("/public/review/react")
    public ResponseEntity<?> likeOrDislike(
            @RequestParam("email") String email,
            @RequestParam("id") Long id,
            @RequestParam("like") boolean like,
            @RequestParam("remove") boolean remove
    ) {
        reviewService.setLikeDislike(email, id, like, remove);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/review/block")
    public ResponseEntity<?> block(@RequestParam("id") Long id) {
        reviewService.block(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/admin/review")
    public ResponseEntity<?> delete(@RequestParam("id") Long id) {
        reviewService.delete(id);
        return ResponseEntity.ok().build();
    }
}
