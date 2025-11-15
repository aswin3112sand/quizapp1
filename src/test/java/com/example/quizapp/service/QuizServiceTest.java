package com.example.quizapp.service;

import com.example.quizapp.dto.LeaderboardEntry;
import com.example.quizapp.model.Question;
import com.example.quizapp.model.Quiz;
import com.example.quizapp.model.Result;
import com.example.quizapp.model.Role;
import com.example.quizapp.model.User;
import com.example.quizapp.repository.QuestionRepository;
import com.example.quizapp.repository.QuizRepository;
import com.example.quizapp.repository.ResultRepository;
import com.example.quizapp.repository.UserRepository;
import com.example.quizapp.support.TestDataFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

    @Mock
    private QuizRepository quizRepository;
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private ResultRepository resultRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private QuizService quizService;

    private Quiz quiz;

    @BeforeEach
    void setUp() {
        quiz = TestDataFactory.quiz(1L);
    }

    @AfterEach
    void clearSecurity() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getDefaultQuiz_whenExisting_returnsEntity() {
        when(quizRepository.findBySlug(anyString())).thenReturn(Optional.of(quiz));

        Quiz result = quizService.getDefaultQuiz();

        assertThat(result).isSameAs(quiz);
        verify(quizRepository, never()).save(any());
    }

    @Test
    void getDefaultQuiz_whenMissing_createsDefaultAndSeedsQuestions() {
        when(quizRepository.findBySlug(anyString())).thenReturn(Optional.empty());
        Quiz savedQuiz = TestDataFactory.quiz(2L);
        when(quizRepository.save(any(Quiz.class))).thenReturn(savedQuiz);
        when(questionRepository.countByQuiz(savedQuiz)).thenReturn(0L);

        Quiz result = quizService.getDefaultQuiz();

        assertThat(result).isEqualTo(savedQuiz);
        verify(quizRepository).save(any(Quiz.class));
        verify(questionRepository).saveAll(argThat(iterable -> iterable.iterator().hasNext()));
    }

    @Test
    void extractAnswerMap_parsesQuestionIds() {
        Map<String, String> params = Map.of(
                "answers[1]", "2",
                "answers[10]", "3",
                "notAnAnswer", "x",
                "answers[bad]", "3"
        );

        Map<Long, Integer> answers = quizService.extractAnswerMap(params);

        assertThat(answers).containsEntry(1L, 2)
                .containsEntry(10L, 3)
                .hasSize(2);
    }

    @Test
    void calculateScore_countsOnlyCorrectAnswers() {
        Question q1 = TestDataFactory.question(1L, quiz, 1);
        Question q2 = TestDataFactory.question(2L, quiz, 0);
        when(questionRepository.findByQuizOrderByIdAsc(quiz)).thenReturn(List.of(q1, q2));

        Map<Long, Integer> answers = new LinkedHashMap<>();
        answers.put(1L, 1);
        answers.put(2L, 3);

        int score = quizService.calculateScore(quiz, answers);

        assertThat(score).isEqualTo(1);
        verify(questionRepository).findByQuizOrderByIdAsc(quiz);
    }

    @Test
    void getQuestionsForQuiz_readsRepository() {
        when(questionRepository.findByQuizOrderByIdAsc(quiz)).thenReturn(List.of(TestDataFactory.question(1L, quiz, 0)));

        List<Question> result = quizService.getQuestionsForQuiz(quiz);

        assertThat(result).hasSize(1);
    }

    @Test
    void saveResult_authenticatedUserUsesProfileName() {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken("user@mail.com", "pwd");
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = TestDataFactory.user(1L, Role.USER);
        user.setName("Jane Tester");
        when(userRepository.findByEmailIgnoreCase("user@mail.com")).thenReturn(Optional.of(user));

        Result savedResult = TestDataFactory.result(5L, user, quiz, 4, 5, LocalDateTime.now());
        when(resultRepository.save(any(Result.class))).thenReturn(savedResult);

        // leaderboard broadcast data
        when(resultRepository.findTop10ByQuizOrderByScoreDescCreatedAtDesc(quiz))
                .thenReturn(List.of(TestDataFactory.result(2L, user, quiz, 5, 5, LocalDateTime.now())));

        Result persisted = quizService.saveResult(quiz, "Provided", 4, 5);

        assertThat(persisted).isSameAs(savedResult);
        verify(resultRepository).save(argThat(result -> "Jane Tester".equals(result.getDisplayName())));
        verify(messagingTemplate).convertAndSend(eq("/topic/leaderboard"), anyList());
    }

    @Test
    void saveResult_whenAnonymousUsesFallbackName() {
        SecurityContextHolder.clearContext();
        when(resultRepository.save(any(Result.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, Result.class));
        when(resultRepository.findTop10ByQuizOrderByScoreDescCreatedAtDesc(quiz))
                .thenReturn(List.of());

        quizService.saveResult(quiz, "", 0, 5);

        ArgumentCaptor<Result> captor = ArgumentCaptor.forClass(Result.class);
        verify(resultRepository).save(captor.capture());
        assertThat(captor.getValue().getDisplayName()).isEqualTo("Anonymous Player");
    }

    @Test
    void getLeaderboardEntries_returnsMappedRecords() {
        User user = TestDataFactory.user(3L, Role.USER);
        LocalDateTime now = LocalDateTime.of(2024, 1, 1, 10, 15);
        when(resultRepository.findTop10ByQuizOrderByScoreDescCreatedAtDesc(quiz))
                .thenReturn(List.of(TestDataFactory.result(9L, user, quiz, 7, 10, now)));

        List<LeaderboardEntry> entries = quizService.getLeaderboardEntries(quiz);

        assertThat(entries).hasSize(1);
        LeaderboardEntry entry = entries.get(0);
        assertThat(entry.displayName()).isEqualTo(user.getName());
        assertThat(entry.submittedAt()).contains("2024-01-01");
    }
}
