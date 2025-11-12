package com.example.quizapp.repository;

import com.example.quizapp.model.Quiz;
import com.example.quizapp.model.Result;
import com.example.quizapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ResultRepository extends JpaRepository<Result, Long> {
    List<Result> findByUserOrderByCreatedAtDesc(User user);
    List<Result> findTop10ByQuizOrderByScoreDescCreatedAtDesc(Quiz quiz);
}
