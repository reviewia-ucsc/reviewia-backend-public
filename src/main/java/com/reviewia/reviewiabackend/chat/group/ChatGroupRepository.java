package com.reviewia.reviewiabackend.chat.group;

import com.reviewia.reviewiabackend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatGroupRepository extends JpaRepository<ChatGroup, Long> {
    @Query("select c from ChatGroup c where c.createdBy=:user and c.isActive=true")
    ChatGroup findByCreatedUser(User user);
    List<ChatGroup> findAllByUsersContains(User user);
}
