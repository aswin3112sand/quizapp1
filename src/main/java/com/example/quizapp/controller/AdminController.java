package com.example.quizapp.controller;

import com.example.quizapp.service.QuizService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final QuizService quizService;

    public AdminController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping
    public String adminHome(Model model) {
        model.addAttribute("quiz", quizService.getDefaultQuiz());
        return "admin";
    }
}
