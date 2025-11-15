package com.example.quizapp.repository;

import com.example.quizapp.model.Quiz;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class QuizRepositoryTest {

    @Autowired
    private QuizRepository quizRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findBySlug_returnsPersistedQuiz() {
        Quiz quiz = new Quiz("Java Quiz", "java-quiz", "desc", 300);
        entityManager.persist(quiz);
        entityManager.flush();

        Optional<Quiz> found = quizRepository.findBySlug("java-quiz");

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Java Quiz");
    }

    @Test
    void findBySlug_whenMissing_returnsEmptyOptional() {
        Optional<Quiz> found = quizRepository.findBySlug("missing-slug");

        assertThat(found).isEmpty();
    }
}
