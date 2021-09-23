package com.reviewia.reviewiabackend.post.favourite;

import com.reviewia.reviewiabackend.post.Post;
import com.reviewia.reviewiabackend.post.PostService;
import com.reviewia.reviewiabackend.user.User;
import com.reviewia.reviewiabackend.user.UserRepository;
import com.reviewia.reviewiabackend.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@AllArgsConstructor
public class FavouriteService {
    private UserService userService;
    private PostService postService;
    private FavouriteRepository favouriteRepository;
    private UserRepository userRepository;

    @Transactional
    public Favourite add(String email, Long postId) {
        User user = userService.getUser(email);
        Post post = postService.getPostById(postId);

        boolean favListExists = favouriteRepository.findByCreatedBy(email).isPresent();

        if(!favListExists) {
            Favourite favourite = new Favourite(post, user);
            user.setFavouriteList(favourite);
            userRepository.save(user);
            return favouriteRepository.save(favourite);
        }

        Favourite favouriteFromDB = favouriteRepository.findByCreatedBy(email).get();
        if(favouriteFromDB.getPosts().contains(post)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already exists");
        }
        favouriteFromDB.getPosts().add(post);
        return favouriteRepository.save(favouriteFromDB);
    }

    public Favourite getAllByEmail(String email) {
        return favouriteRepository.findByCreatedBy(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT));
    }

    public void removePostFromList(String email, Long id) {
        boolean favListExists = favouriteRepository.findByCreatedBy(email).isPresent();
        if(!favListExists) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "List is empty");
        }

        Post post = postService.getPostById(id);
        Favourite favourite = favouriteRepository.findByCreatedBy(email).get();

        favourite.getPosts().removeIf(c -> Objects.equals(c.getPostId(), id));
//        if(!favourite.getPosts().contains(post)) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");
//        }
        favourite.getPosts().remove(post);
        favouriteRepository.save(favourite);
    }
}
