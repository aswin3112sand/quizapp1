package com.example.quizapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Question {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 500)
    @Column(nullable = false, length = 500)
    private String text;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "question_options", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "option_text", nullable = false)
    @OrderColumn(name = "option_index")
    private List<String> options = new ArrayList<>();

    @Min(0)
    private int correctIndex;

    public Question() {}

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    public Question(String text, List<String> options, int correctIndex, Quiz quiz) {
        this.text = text;
        this.options = options;
        this.correctIndex = correctIndex;
        this.quiz = quiz;
    }

    public Long getId() { return id; }
    public String getText() { return text; }
    public List<String> getOptions() { return options; }
    public int getCorrectIndex() { return correctIndex; }
    public Quiz getQuiz() { return quiz; }

    public void setId(Long id) { this.id = id; }
    public void setText(String text) { this.text = text; }
    public void setOptions(List<String> options) { this.options = options; }
    public void setCorrectIndex(int correctIndex) { this.correctIndex = correctIndex; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }

    public void ensureOptionSlots(int minSize) {
        if (options == null) {
            options = new ArrayList<>();
        }
        while (options.size() < minSize) {
            options.add("");
        }
    }

    private String getOptionValue(int index) {
        ensureOptionSlots(index + 1);
        return options.get(index);
    }

    private void setOptionValue(int index, String value) {
        ensureOptionSlots(index + 1);
        options.set(index, value == null ? "" : value);
    }

    @Transient
    public String getTitle() {
        return text;
    }

    public void setTitle(String title) {
        this.text = title;
    }

    @Transient
    public String getOptionA() {
        return getOptionValue(0);
    }

    public void setOptionA(String optionA) {
        setOptionValue(0, optionA);
    }

    @Transient
    public String getOptionB() {
        return getOptionValue(1);
    }

    public void setOptionB(String optionB) {
        setOptionValue(1, optionB);
    }

    @Transient
    public String getOptionC() {
        return getOptionValue(2);
    }

    public void setOptionC(String optionC) {
        setOptionValue(2, optionC);
    }

    @Transient
    public String getOptionD() {
        return getOptionValue(3);
    }

    public void setOptionD(String optionD) {
        setOptionValue(3, optionD);
    }

    @Transient
    public Integer getAnswer() {
        return correctIndex;
    }

    public void setAnswer(Integer answer) {
        if (answer != null) {
            this.correctIndex = answer;
        }
    }
}
