package com.example.quizapp.repository;

import com.example.quizapp.model.Role;
import com.example.quizapp.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByEmailIgnoreCase_returnsUser() {
        User user = new User("User", "person@mail.com", "secret1", Role.USER);
        entityManager.persist(user);
        entityManager.flush();

        Optional<User> result = userRepository.findByEmailIgnoreCase("PERSON@mail.com");

        assertThat(result).isPresent();
    }

    @Test
    void existsByEmailIgnoreCase_detectsExistingAccount() {
        User user = new User("User", "exists@mail.com", "secret1", Role.USER);
        entityManager.persist(user);
        entityManager.flush();

        boolean exists = userRepository.existsByEmailIgnoreCase("exists@mail.com");

        assertThat(exists).isTrue();
    }
}
