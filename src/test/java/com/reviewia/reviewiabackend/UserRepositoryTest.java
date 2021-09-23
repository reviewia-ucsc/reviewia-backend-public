package com.reviewia.reviewiabackend;

import com.reviewia.reviewiabackend.user.User;
import com.reviewia.reviewiabackend.user.UserRepository;
import com.reviewia.reviewiabackend.user.UserRole;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository repository;
    @Test
    public void testStoreUser() {
        HashSet<UserRole> roles = new HashSet<>();
        roles.add(UserRole.USER);
        User user = repository.save(new User("Unit", "Test 1", "utest@gmail.com", "12345678", UserRole.USER, ""));
        assertThat(user).isNotNull();
        assertThat(repository.count()).isEqualTo(1L);
    }

    @Test
    public void testStoreUserFail() {
        HashSet<UserRole> roles = new HashSet<>();
        roles.add(UserRole.USER);
        User user = repository.save(new User("Unit", "Test 1", "", "123", UserRole.USER, ""));
        assertThat(user).isNotNull();
        assertThat(repository.count()).isEqualTo(1L);
    }
}
