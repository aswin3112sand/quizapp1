package com.example.quizapp.service;

import com.example.quizapp.model.Question;
import com.example.quizapp.model.Quiz;
import com.example.quizapp.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository repo;

    public QuestionServiceImpl(QuestionRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<Question> findByQuiz(Quiz quiz) {
        return repo.findByQuizOrderByIdAsc(quiz);
    }

    @Override
    public List<Question> findAll() {
        return repo.findAll();
    }

    @Override
    public Question findById(Long id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public Question save(Question question) {
        return repo.save(question);
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }
}
