package com.example.quizapp.service;

import com.example.quizapp.model.Question;
import com.example.quizapp.model.Quiz;
import com.example.quizapp.repository.QuestionRepository;
import com.example.quizapp.support.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionServiceImplTest {

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QuestionServiceImpl questionService;

    private Quiz quiz;

    @BeforeEach
    void setUp() {
        quiz = TestDataFactory.quiz(1L);
    }

    @Test
    void findByQuiz_returnsOrderedQuestions() {
        List<Question> questions = List.of(
                TestDataFactory.question(1L, quiz, 1),
                TestDataFactory.question(2L, quiz, 2)
        );
        when(questionRepository.findByQuizOrderByIdAsc(quiz)).thenReturn(questions);

        List<Question> result = questionService.findByQuiz(quiz);

        assertThat(result).hasSize(2)
                .containsExactlyElementsOf(questions);
        verify(questionRepository).findByQuizOrderByIdAsc(quiz);
    }

    @Test
    void findAll_returnsEveryQuestion() {
        when(questionRepository.findAll()).thenReturn(List.of(TestDataFactory.question(10L, quiz, 0)));

        List<Question> result = questionService.findAll();

        assertThat(result).hasSize(1);
        verify(questionRepository).findAll();
    }

    @Test
    void findById_whenMissing_returnsNull() {
        when(questionRepository.findById(5L)).thenReturn(Optional.empty());

        Question result = questionService.findById(5L);

        assertThat(result).isNull();
        verify(questionRepository).findById(5L);
    }

    @Test
    void save_persistsEntity() {
        Question question = TestDataFactory.question(null, quiz, 1);
        when(questionRepository.save(question)).thenReturn(question);

        Question persisted = questionService.save(question);

        assertThat(persisted).isSameAs(question);
        verify(questionRepository).save(question);
    }

    @Test
    void deleteById_removesEntity() {
        questionService.deleteById(7L);

        verify(questionRepository).deleteById(7L);
    }
}
