package com.example.quizapp.controller;

import com.example.quizapp.model.Question;
import com.example.quizapp.model.Quiz;
import com.example.quizapp.repository.QuestionRepository;
import com.example.quizapp.service.QuizService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final QuestionRepository questionRepository;
    private final QuizService quizService;

    public AdminController(QuestionRepository questionRepository, QuizService quizService) {
        this.questionRepository = questionRepository;
        this.quizService = quizService;
    }

    @GetMapping
    public String adminHome(Model model) {
        model.addAttribute("quiz", quizService.getDefaultQuiz());
        return "admin";
    }

    @GetMapping("/questions")
    public String listQuestions(Model model) {
        Quiz quiz = quizService.getDefaultQuiz();
        model.addAttribute("quiz", quiz);
        model.addAttribute("questions", questionRepository.findByQuizOrderByIdAsc(quiz));
        return "admin-questions";
    }

    @GetMapping("/questions/new")
    public String newQuestion(Model model) {
        model.addAttribute("q", new Question());
        model.addAttribute("quiz", quizService.getDefaultQuiz());
        return "admin-question-form";
    }

    @PostMapping("/questions")
    public String createQuestion(@RequestParam("text") String text,
                                 @RequestParam("option1") String option1,
                                 @RequestParam("option2") String option2,
                                 @RequestParam("option3") String option3,
                                 @RequestParam("option4") String option4,
                                 @RequestParam("correctIndex") int correctIndex) {
        List<String> options = new ArrayList<>();
        options.add(option1);
        options.add(option2);
        options.add(option3);
        options.add(option4);
        Quiz quiz = quizService.getDefaultQuiz();
        Question q = new Question(text, options, correctIndex, quiz);
        questionRepository.save(q);
        return "redirect:/admin/questions";
    }

    @GetMapping("/questions/{id}/edit")
    public String editQuestion(@PathVariable Long id, Model model) {
        Optional<Question> q = questionRepository.findById(id);
        if (q.isEmpty()) return "redirect:/admin/questions";
        model.addAttribute("q", q.get());
        model.addAttribute("quiz", quizService.getDefaultQuiz());
        return "admin-question-form";
    }

    @PostMapping("/questions/{id}")
    public String updateQuestion(@PathVariable Long id,
                                 @RequestParam("text") String text,
                                 @RequestParam("option1") String option1,
                                 @RequestParam("option2") String option2,
                                 @RequestParam("option3") String option3,
                                 @RequestParam("option4") String option4,
                                 @RequestParam("correctIndex") int correctIndex) {
        return questionRepository.findById(id).map(existing -> {
            existing.setText(text);
            List<String> options = new ArrayList<>();
            options.add(option1);
            options.add(option2);
            options.add(option3);
            options.add(option4);
            existing.setOptions(options);
            existing.setCorrectIndex(correctIndex);
            existing.setQuiz(quizService.getDefaultQuiz());
            questionRepository.save(existing);
            return "redirect:/admin/questions";
        }).orElse("redirect:/admin/questions");
    }

    @PostMapping("/questions/{id}/delete")
    public String deleteQuestion(@PathVariable Long id) {
        questionRepository.deleteById(id);
        return "redirect:/admin/questions";
    }
}
