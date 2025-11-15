package com.example.quizapp.support;

import com.example.quizapp.model.Question;
import com.example.quizapp.model.Quiz;
import com.example.quizapp.model.Result;
import com.example.quizapp.model.Role;
import com.example.quizapp.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class TestDataFactory {

    private TestDataFactory() {
    }

    public static Quiz quiz(Long id) {
        Quiz quiz = new Quiz("Sample Quiz", "sample-slug" + (id == null ? "" : id), "desc", 120);
        quiz.setId(id);
        return quiz;
    }

    public static Question question(Long id, Quiz quiz, int correctIndex) {
        List<String> options = new ArrayList<>(List.of("A", "B", "C", "D"));
        Question question = new Question("Question " + (id == null ? 1 : id), options, correctIndex, quiz);
        question.setId(id);
        return question;
    }

    public static User user(Long id, Role role) {
        User user = new User("User" + (id == null ? "" : id), "user" + (id == null ? "" : id) + "@mail.com", "encoded", role);
        user.setId(id);
        return user;
    }

    public static Result result(Long id, User user, Quiz quiz, int score, int total, LocalDateTime createdAt) {
        Result result = new Result(user, quiz, user != null ? user.getName() : "guest", score, total);
        result.setId(id);
        result.setCreatedAt(createdAt == null ? LocalDateTime.now() : createdAt);
        return result;
    }
}
