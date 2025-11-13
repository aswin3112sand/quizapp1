package com.example.quizapp.controller;

import com.example.quizapp.model.Quiz;
import com.example.quizapp.service.QuizService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/")
    public String home(Authentication authentication, Model model) {
        boolean isAuthenticated = authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
        model.addAttribute("isAuthenticated", isAuthenticated);
        if (isAuthenticated) {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch("ROLE_ADMIN"::equals);
            model.addAttribute("isAdmin", isAdmin);
        }
        return "index";
    }

    @GetMapping("/quiz/home")
    public String quizHome() {
        return "index";
    }

    @GetMapping("/quiz")
    public String showQuiz(Model model) {
        Quiz quiz = quizService.getDefaultQuiz();
        model.addAttribute("quiz", quiz);
        model.addAttribute("questions", quizService.getQuestionsForQuiz(quiz));
        model.addAttribute("totalQuestions", quizService.getTotalQuestions(quiz));
        return "quiz";
    }

    @PostMapping("/submit")
    public String submitQuiz(
            @RequestParam(name = "username", required = false) String username,
            @RequestParam Map<String, String> formParams,
            Model model) {

        Quiz quiz = quizService.getDefaultQuiz();
        Map<Long, Integer> answers = quizService.extractAnswerMap(formParams);

        int score = quizService.calculateScore(quiz, answers);
        int total = (int) quizService.getTotalQuestions(quiz);
        quizService.saveResult(quiz, username, score, total);
        double percentage = total == 0 ? 0 : (score * 100.0) / total;

        model.addAttribute("score", score);
        model.addAttribute("total", total);
        model.addAttribute("percentage", Math.round(percentage));
        model.addAttribute("username", username);
        model.addAttribute("quiz", quiz);

        return "result";
    }
}
