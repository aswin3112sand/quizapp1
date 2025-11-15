package com.example.quizapp.repository;

import com.example.quizapp.model.Question;
import com.example.quizapp.model.Quiz;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class QuestionRepositoryTest {

    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    void findByQuizOrderByIdAsc_returnsOrderedQuestions() {
        Quiz quiz = new Quiz("Quiz", "quiz-slug", "desc", 60);
        entityManager.persist(quiz);

        Question first = new Question("First", List.of("A", "B", "C", "D"), 1, quiz);
        Question second = new Question("Second", List.of("A", "B", "C", "D"), 2, quiz);
        entityManager.persist(first);
        entityManager.persist(second);
        entityManager.flush();

        List<Question> result = questionRepository.findByQuizOrderByIdAsc(quiz);

        assertThat(result).hasSize(2)
                .isSortedAccordingTo((a, b) -> a.getId().compareTo(b.getId()));
    }

    @Test
    void countByQuiz_returnsNumberOfQuestions() {
        Quiz quiz = new Quiz("Quiz2", "quiz-slug2", "desc", 60);
        entityManager.persist(quiz);
        entityManager.persist(new Question("Q1", List.of("A", "B", "C", "D"), 1, quiz));
        entityManager.persist(new Question("Q2", List.of("A", "B", "C", "D"), 2, quiz));
        entityManager.flush();

        long count = questionRepository.countByQuiz(quiz);

        assertThat(count).isEqualTo(2);
    }
}
