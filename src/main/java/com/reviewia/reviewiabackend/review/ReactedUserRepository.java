package com.reviewia.reviewiabackend.review;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReactedUserRepository extends JpaRepository<ReactedUser, Long> {
    ReactedUser findByEmail(String email);
}
