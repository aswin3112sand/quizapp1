package com.example.quizapp.controller;

import com.example.quizapp.model.Question;
import com.example.quizapp.service.QuestionService;
import com.example.quizapp.service.QuizService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/questions")
public class AdminQuestionController {

    private final QuestionService questionService;
    private final QuizService quizService;

    public AdminQuestionController(QuestionService questionService, QuizService quizService) {
        this.questionService = questionService;
        this.quizService = quizService;
    }

    @GetMapping
    public String listQuestions(Model model) {
        var quiz = quizService.getDefaultQuiz();
        model.addAttribute("quiz", quiz);
        model.addAttribute("questions", questionService.findByQuiz(quiz));
        return "admin/questions";
    }

    @GetMapping("/new")
    public String newQuestion(Model model) {
        Question question = new Question();
        question.ensureOptionSlots(4);
        model.addAttribute("question", question);
        model.addAttribute("quiz", quizService.getDefaultQuiz());
        return "admin/question-form";
    }

    @PostMapping
    public String saveQuestion(@ModelAttribute("question") Question question, RedirectAttributes ra) {
        question.ensureOptionSlots(4);
        question.setQuiz(quizService.getDefaultQuiz());
        boolean isUpdate = question.getId() != null;
        questionService.save(question);
        ra.addFlashAttribute("success", isUpdate ? "Question updated successfully!" : "Question created successfully!");
        return "redirect:/admin/questions";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        Question question = questionService.findById(id);
        if (question == null) {
            ra.addFlashAttribute("error", "Question not found.");
            return "redirect:/admin/questions";
        }
        question.ensureOptionSlots(4);
        model.addAttribute("question", question);
        model.addAttribute("quiz", quizService.getDefaultQuiz());
        return "admin/question-form";
    }

    @PostMapping("/delete/{id}")
    public String deleteQuestion(@PathVariable("id") Long id, RedirectAttributes ra) {
        Question existing = questionService.findById(id);
        if (existing == null) {
            ra.addFlashAttribute("error", "Question not found.");
        } else {
            questionService.deleteById(id);
            ra.addFlashAttribute("success", "Question deleted successfully!");
        }
        return "redirect:/admin/questions";
    }
}
