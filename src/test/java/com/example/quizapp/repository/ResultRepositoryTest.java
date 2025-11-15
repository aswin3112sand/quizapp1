package com.example.quizapp.repository;

import com.example.quizapp.model.Quiz;
import com.example.quizapp.model.Result;
import com.example.quizapp.model.Role;
import com.example.quizapp.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ResultRepositoryTest {

    @Autowired
    private ResultRepository resultRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByUserOrderByCreatedAtDesc_returnsDescendingOrder() {
        Quiz quiz = new Quiz("Quiz", "slug-1", "desc", 60);
        User user = new User("User", "user@mail.com", "secret1", Role.USER);
        entityManager.persist(quiz);
        entityManager.persist(user);

        Result newer = new Result(user, quiz, "User", 5, 10);
        newer.setCreatedAt(LocalDateTime.now());
        Result older = new Result(user, quiz, "User", 3, 10);
        older.setCreatedAt(LocalDateTime.now().minusDays(1));
        entityManager.persist(older);
        entityManager.persist(newer);
        entityManager.flush();

        List<Result> results = resultRepository.findByUserOrderByCreatedAtDesc(user);

        assertThat(results).hasSize(2);
        assertThat(results.get(0).getScore()).isEqualTo(5);
    }

    @Test
    void findTop10ByQuizOrderByScoreDescCreatedAtDesc_returnsScoreSortedList() {
        Quiz quiz = new Quiz("Quiz", "slug-2", "desc", 60);
        entityManager.persist(quiz);
        User user = new User("User", "another@mail.com", "secret1", Role.USER);
        entityManager.persist(user);

        for (int i = 0; i < 3; i++) {
            Result result = new Result(user, quiz, "Player" + i, i + 1, 10);
            result.setCreatedAt(LocalDateTime.now().minusMinutes(i));
            entityManager.persist(result);
        }
        entityManager.flush();

        List<Result> results = resultRepository.findTop10ByQuizOrderByScoreDescCreatedAtDesc(quiz);

        assertThat(results).hasSize(3);
        assertThat(results.get(0).getScore()).isEqualTo(3);
    }
}
