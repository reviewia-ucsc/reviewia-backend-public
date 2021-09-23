package com.reviewia.reviewiabackend.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE User a " + "SET a.enabled = TRUE WHERE a.email = ?1")
    int enableAppUser(String email);

    Long countByEnabledTrue();

    @Query("select u.id as id, u.firstName as firstName, u.lastName as lastName, u.email as email, u.role as role, u.locked as locked, u.enabled as enabled, u.avatar as avatar, u.reportCount as reportCount from User u where u.locked=true")
    List<UserView> findAllByLockedTrue();
}
