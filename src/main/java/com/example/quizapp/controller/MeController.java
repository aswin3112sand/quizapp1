package com.example.quizapp.controller;

import com.example.quizapp.model.Result;
import com.example.quizapp.repository.ResultRepository;
import com.example.quizapp.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MeController {

    private final ResultRepository resultRepository;
    private final UserRepository userRepository;

    public MeController(ResultRepository resultRepository, UserRepository userRepository) {
        this.resultRepository = resultRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/me/scores")
    public String myScores(Authentication authentication, Model model) {
        if (authentication == null || authentication.getName() == null) {
            return "redirect:/login";
        }
        return userRepository.findByEmailIgnoreCase(authentication.getName())
                .map(user -> {
                    List<Result> results = resultRepository
                            .findByUserOrderByCreatedAtDesc(user);
                    model.addAttribute("results", results);
                    return "me-scores";
                })
                .orElse("redirect:/login");
    }
}
