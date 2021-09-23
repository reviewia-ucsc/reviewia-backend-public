package com.reviewia.reviewiabackend.review;

import com.reviewia.reviewiabackend.notification.Notification;
import com.reviewia.reviewiabackend.notification.NotificationMessages;
import com.reviewia.reviewiabackend.notification.NotificationService;
import com.reviewia.reviewiabackend.post.Post;
import com.reviewia.reviewiabackend.post.PostRepository;
import com.reviewia.reviewiabackend.post.PostService;
import com.reviewia.reviewiabackend.user.User;
import com.reviewia.reviewiabackend.user.UserService;
import com.reviewia.reviewiabackend.utils.sentiment.Sentiment;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@AllArgsConstructor
public class ReviewService {

    private UserService userService;
    private PostService postService;
    private PostRepository postRepository;
    private ReviewRepository reviewRepository;
    private ReactedUserRepository reactedUserRepository;
    private NotificationService notificationService;

    @Transactional
    public Review create(String email, Review review) {
        float finalRate;

        float userRate = review.getUserRate();
        Sentiment sentiment = new Sentiment(userRate);
        finalRate = sentiment.analyze(review.getDescription());

        review.setFinalRate(finalRate);
        review.setSentimentRate(sentiment.getVaderRate());

        User user = userService.getUser(email);
        Post post = postService.getPostById(review.getPostId());

        review.setEmail(email);
        post.setReviewCount(post.getReviews().size() + 1);
        post.incrementTotalRate(finalRate);
        post.setRate(post.getTotalRate() / post.getReviewCount());
        post.setReview(review);
        user.setReview(review);

        Notification notification = new Notification(NotificationMessages.NEW_REVIEW, post.getPostId());

        reviewRepository.save(review);
        postRepository.save(post);
        userService.saveUser(user);
        notificationService.create(post.getEmail(), "p", notification);
        return review;
    }

    public List<Review> getAll() {
        try {
            return reviewRepository.findAllReviews();
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public Review getById(Long id) {
        return reviewRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public List<Review> getAllByEmail(String email) {
        try {
            return reviewRepository.findAllByEmail(email);
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public List<Review> getAllByPostId(Long id) {
        try {
            return reviewRepository.findAllByPostId(id);
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public void delete(Long id) {
        try {
            reviewRepository.deleteById(id);
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

//    public void block(Long id) {
//        try {
//            Review review = reviewRepository.findById(id).get();
//            review.setBlocked(true);
//
//            Notification notification = new Notification(NotificationMessages.BLOCK_REVIEW, id);
//
//            reviewRepository.save(review);
//            notificationService.create(review.getEmail(), "r", notification);
//        }
//        catch (Exception e) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
//        }
//    }

    @Transactional
    public void block(Long id) {
        try {
            Review oldReview = reviewRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            oldReview.setBlocked(true);

            Post post = postRepository.findById(oldReview.getPostId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
//            post.decrementTotalRate(oldReview.getFinalRate());
            float tmp = post.getTotalRate() - oldReview.getFinalRate();
            System.out.println(tmp);
            post.setTotalRate(tmp);

            post.setReviewCount(post.getReviews().size() - 1);
            if(post.getReviewCount() > 0)
                post.setRate(post.getTotalRate() / post.getReviewCount());
            else post.setRate(0);
            Notification notification = new Notification(NotificationMessages.BLOCK_REVIEW, id);

            reviewRepository.save(oldReview);
//            reviewRepository.blockReview(id);
            postRepository.save(post);
            notificationService.create(oldReview.getEmail(), "r", notification);
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    public void setLikeDislike(String email, Long id, boolean like, boolean remove) {
        userService.getUser(email);
        ReactedUser reactedUser = reactedUserRepository.findByEmail(email);
        if(reactedUser == null) {
            reactedUser = new ReactedUser(email);
            reactedUserRepository.save(reactedUser);
        }

        try {
            Review review = getById(id);

            if(!remove) {
                Notification notification;
                if (like) {
                    notification = new Notification(NotificationMessages.REVIEW_LIKE, id);

                    if(review.getDislikedList().contains(reactedUser)) {
                        review.setDislikeCount(review.getDislikeCount() - 1);
                        review.getDislikedList().remove(reactedUser);
                    }
                    else if(review.getLikedList().contains(reactedUser)) return;

                    review.setLikeCount(review.getLikeCount() + 1);
                    review.addLikedUser(reactedUser);
                } else {
                    notification = new Notification(NotificationMessages.REVIEW_DISLIKE, id);
                    if(review.getLikedList().contains(reactedUser)) {
                        review.setLikeCount(review.getLikeCount() - 1);
                        review.getLikedList().remove(reactedUser);
                    }
                    else if(review.getDislikedList().contains(reactedUser)) return;

                    review.setDislikeCount(review.getDislikeCount() + 1);
                    review.addDislikedUser(reactedUser);
                }
                notificationService.create(review.getEmail(), "r", notification);
            }else {
                review.removeReaction(reactedUser);
                if (like) {
                    review.setLikeCount(review.getLikeCount() - 1);
                } else {
                    review.setDislikeCount(review.getDislikeCount() - 1);
                }
            }
            reviewRepository.save(review);
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public boolean checkReview(Long id) {
        return reviewRepository.findById(id).isPresent();
    }

    @Transactional
    public Review update(String email, Long reviewId, Review newReview) {
        float oldFinalRate;
        float newFinalRate;
        Review oldReview = reviewRepository.findById(reviewId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        float newUserRate = newReview.getUserRate();
        float oldUserRate = oldReview.getUserRate();

        Sentiment newSentiment = new Sentiment(newUserRate);
        Sentiment oldSentiment = new Sentiment(oldUserRate);

        newFinalRate = newSentiment.analyze(newReview.getDescription());
        oldFinalRate = oldSentiment.analyze(oldReview.getDescription());

        oldReview.setDescription(newReview.getDescription());
        oldReview.setFinalRate(newFinalRate);
        oldReview.setSentimentRate(newSentiment.getVaderRate());
        oldReview.setUserRate(newReview.getUserRate());

        Post post = postService.getPostByIdWithoutPostCountChange(oldReview.getPostId());

//        review.setEmail(email);
//        post.setReviewCount(post.getReviews().size() + 1);
        post.decrementTotalRate(oldFinalRate);
        post.incrementTotalRate(newFinalRate);
        post.setRate(post.getTotalRate() / post.getReviewCount());
//        post.setReview(oldReview);
//        user.setReview(review);

        Notification notification = new Notification(NotificationMessages.REVIEW_UPDATE, post.getPostId());

        oldReview = reviewRepository.save(oldReview);
        postRepository.save(post);
//        userService.saveUser(user);
        notificationService.create(post.getEmail(), "p", notification);
        return oldReview;
    }
}
