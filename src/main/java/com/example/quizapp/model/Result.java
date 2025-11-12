package com.example.quizapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
public class Result {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String displayName;
    @Min(0)
    private int score;
    @Min(0)
    private int total;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Result() {}

    public Result(User user, Quiz quiz, String displayName, int score, int total) {
        this.user = user;
        this.quiz = quiz;
        this.displayName = displayName;
        this.score = score;
        this.total = total;
    }

    public Long getId() { return id; }
    public User getUser() { return user; }
    public Quiz getQuiz() { return quiz; }
    public String getDisplayName() { return displayName; }
    public int getScore() { return score; }
    public int getTotal() { return total; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setScore(int score) { this.score = score; }
    public void setTotal(int total) { this.total = total; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
