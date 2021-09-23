package com.reviewia.reviewiabackend.post.favourite;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/api/user/post")
public class FavouriteController {
    private FavouriteService favouriteService;

    @GetMapping("/favourite")
    public ResponseEntity<Favourite> getFavourites(@RequestParam String email) {
        return ResponseEntity.ok(favouriteService.getAllByEmail(email));
    }

    @PostMapping("/favourite")
    public ResponseEntity<Favourite> addToFavourite(@RequestParam String email, @RequestParam Long id) {
        return ResponseEntity.ok(favouriteService.add(email, id));
    }

    @DeleteMapping("/favourite")
    public ResponseEntity<?> removeFromList(@RequestParam String email, @RequestParam Long id) {
        favouriteService.removePostFromList(email, id);
        return ResponseEntity.ok().build();
    }

}
