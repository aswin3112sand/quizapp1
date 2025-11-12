package com.example.quizapp.controller;

import com.example.quizapp.dto.LeaderboardEntry;
import com.example.quizapp.model.Quiz;
import com.example.quizapp.service.QuizService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class LeaderboardController {

    private final QuizService quizService;

    public LeaderboardController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/leaderboard")
    public String viewLeaderboard(Model model) {
        Quiz quiz = quizService.getDefaultQuiz();
        model.addAttribute("quiz", quiz);
        return "leaderboard";
    }

    @GetMapping("/api/leaderboard")
    @ResponseBody
    public List<LeaderboardEntry> leaderboardData() {
        return quizService.getLeaderboardEntries(quizService.getDefaultQuiz());
    }
}
