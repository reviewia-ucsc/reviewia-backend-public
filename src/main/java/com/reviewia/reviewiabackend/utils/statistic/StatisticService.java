package com.reviewia.reviewiabackend.utils.statistic;

import com.reviewia.reviewiabackend.chat.group.ChatGroupRepository;
import com.reviewia.reviewiabackend.chat.message.MessageRepository;
import com.reviewia.reviewiabackend.post.PostRepository;
import com.reviewia.reviewiabackend.post.category.CategoryRepository;
import com.reviewia.reviewiabackend.post.subCategory.SubCategoryRepository;
import com.reviewia.reviewiabackend.review.ReviewRepository;
import com.reviewia.reviewiabackend.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class StatisticService {
    private PostRepository postRepository;
    private UserRepository userRepository;
    private ReviewRepository reviewRepository;
    private CategoryRepository categoryRepository;
    private SubCategoryRepository subCategoryRepository;
    private ChatGroupRepository chatGroupRepository;
    private MessageRepository messageRepository;

    public Statistic generate() {
        Statistic statistic = new Statistic();

        statistic.setPosts(postRepository.count());
        statistic.setBlockedPosts(postRepository.countByBlockedTrue());
        statistic.setReviews(reviewRepository.count());
        statistic.setBlockedReviews(reviewRepository.countByIsBlockedTrue());
        statistic.setUsers(userRepository.count());
        statistic.setActiveUsers(userRepository.countByEnabledTrue());
        statistic.setCategories(categoryRepository.count());
        statistic.setSubCategories(subCategoryRepository.count());
        statistic.setChatGroups(chatGroupRepository.count());
        statistic.setMessages(messageRepository.count());

        return statistic;
    }

    public Long between(String type, String category, String subcategory, LocalDate start, LocalDate end) {
        if(type.equalsIgnoreCase("p")) {
            if (subcategory != null)
                return postRepository.countBySubCategoryAndCreatedAtBetween(subcategory, Timestamp.valueOf(start.atStartOfDay()), Timestamp.valueOf(end.atStartOfDay()));
            else if (category != null)
                return postRepository.countByCategoryAndCreatedAtBetween(category, Timestamp.valueOf(start.atStartOfDay()), Timestamp.valueOf(end.atStartOfDay()));
        }
        else if(type.equalsIgnoreCase("r"))
            return reviewRepository.countByCreatedAtBetween(Timestamp.valueOf(start.atStartOfDay()), Timestamp.valueOf(end.atStartOfDay()));
        return 0L;
    }

    public Map<LocalDate, Long> chart(String type, LocalDate start, LocalDate end) {
        List<Object[]> result = null;
        if(type.equalsIgnoreCase("p")) {
            result = (List<Object[]>) postRepository.findChartCount(start, end);
        }
        else if(type.equalsIgnoreCase("r")) {
            result = (List<Object[]>) reviewRepository.findChartCount(start, end);

        }
        Map<LocalDate, Long> map = null;
        if(result != null && !result.isEmpty()){
            map = new HashMap<>();
            for (Object[] object : result) {
                map.put(((LocalDate)object[0]), (Long) object[1]);
            }
        }
        return map;
    }
}