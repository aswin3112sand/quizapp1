package com.example.quizapp.service;

import com.example.quizapp.dto.LeaderboardEntry;
import com.example.quizapp.model.Question;
import com.example.quizapp.model.Quiz;
import com.example.quizapp.model.Result;
import com.example.quizapp.model.User;
import com.example.quizapp.repository.QuestionRepository;
import com.example.quizapp.repository.QuizRepository;
import com.example.quizapp.repository.ResultRepository;
import com.example.quizapp.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class QuizService {

    private static final String DEFAULT_QUIZ_SLUG = "general-knowledge";
    private static final Pattern ANSWER_KEY_PATTERN = Pattern.compile("answers\\[(\\d+)]");
    private static final DateTimeFormatter LEADERBOARD_TS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final ResultRepository resultRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public QuizService(QuizRepository quizRepository,
                       QuestionRepository questionRepository,
                       ResultRepository resultRepository,
                       UserRepository userRepository,
                       SimpMessagingTemplate messagingTemplate) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.resultRepository = resultRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional(readOnly = true)
    public Quiz getDefaultQuiz() {
        return quizRepository.findBySlug(DEFAULT_QUIZ_SLUG)
                .orElseThrow(() -> new IllegalStateException("Default quiz not configured"));
    }

    @Transactional(readOnly = true)
    public List<Question> getQuestionsForQuiz(Quiz quiz) {
        return questionRepository.findByQuizOrderByIdAsc(quiz);
    }

    @Transactional(readOnly = true)
    public long getTotalQuestions(Quiz quiz) {
        return questionRepository.countByQuiz(quiz);
    }

    public Map<Long, Integer> extractAnswerMap(Map<String, String> formParams) {
        Map<Long, Integer> answers = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : formParams.entrySet()) {
            Matcher matcher = ANSWER_KEY_PATTERN.matcher(entry.getKey());
            if (matcher.matches() && entry.getValue() != null && !entry.getValue().isBlank()) {
                try {
                    Long questionId = Long.valueOf(matcher.group(1));
                    answers.put(questionId, Integer.valueOf(entry.getValue()));
                } catch (NumberFormatException ignored) {}
            }
        }
        return answers;
    }

    public int calculateScore(Quiz quiz, Map<Long, Integer> selectedAnswers) {
        List<Question> questions = getQuestionsForQuiz(quiz);
        int score = 0;
        for (Question question : questions) {
            Integer chosen = selectedAnswers.get(question.getId());
            if (chosen != null && chosen == question.getCorrectIndex()) {
                score++;
            }
        }
        return score;
    }

    public Result saveResult(Quiz quiz, String requestedDisplayName, int score, int total) {
        User user = resolveAuthenticatedUser().orElse(null);
        String displayName = determineDisplayName(requestedDisplayName, user);
        Result result = new Result(user, quiz, displayName, score, total);
        Result persisted = resultRepository.save(result);
        broadcastLeaderboard(quiz);
        return persisted;
    }

    private Optional<User> resolveAuthenticatedUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()
                    && !"anonymousUser".equals(authentication.getName())) {
                return userRepository.findByEmailIgnoreCase(authentication.getName());
            }
        } catch (Exception ignored) {}
        return Optional.empty();
    }

    private String determineDisplayName(String provided, User user) {
        if (user != null) {
            if (user.getName() != null && !user.getName().isBlank()) {
                return user.getName();
            }
            return user.getEmail();
        }
        if (provided != null && !provided.isBlank()) {
            return provided.trim();
        }
        return "Anonymous Player";
    }

    public void broadcastLeaderboard(Quiz quiz) {
        messagingTemplate.convertAndSend("/topic/leaderboard", getLeaderboardEntries(quiz));
    }

    @Transactional(readOnly = true)
    public List<LeaderboardEntry> getLeaderboardEntries(Quiz quiz) {
        return resultRepository.findTop10ByQuizOrderByScoreDescCreatedAtDesc(quiz)
                .stream()
                .map(result -> new LeaderboardEntry(
                        result.getDisplayName(),
                        result.getScore(),
                        result.getTotal(),
                        result.getCreatedAt().format(LEADERBOARD_TS)
                ))
                .toList();
    }
}
