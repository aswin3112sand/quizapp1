package com.example.quizapp.service;

import com.example.quizapp.model.Question;
import com.example.quizapp.model.Quiz;

import java.util.List;

public interface QuestionService {

    List<Question> findByQuiz(Quiz quiz);

    List<Question> findAll();

    Question findById(Long id);

    Question save(Question question);

    void deleteById(Long id);
}
