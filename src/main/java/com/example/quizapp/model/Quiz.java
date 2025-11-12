package com.example.quizapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quizzes", uniqueConstraints = @UniqueConstraint(name = "uk_quiz_slug", columnNames = "slug"))
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String title;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String slug;

    @Size(max = 255)
    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private int durationSeconds = 180;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    public Quiz() {}

    public Quiz(String title, String slug, String description, int durationSeconds) {
        this.title = title;
        this.slug = slug;
        this.description = description;
        this.durationSeconds = durationSeconds;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getSlug() { return slug; }
    public String getDescription() { return description; }
    public int getDurationSeconds() { return durationSeconds; }
    public List<Question> getQuestions() { return questions; }

    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setSlug(String slug) { this.slug = slug; }
    public void setDescription(String description) { this.description = description; }
    public void setDurationSeconds(int durationSeconds) { this.durationSeconds = durationSeconds; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }
}
