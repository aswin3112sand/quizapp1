package com.example.quizapp.repository;

import com.example.quizapp.model.Question;
import com.example.quizapp.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByQuizOrderByIdAsc(Quiz quiz);
    long countByQuiz(Quiz quiz);
}
