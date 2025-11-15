package com.example.quizapp.controller;

import com.example.quizapp.model.Question;
import com.example.quizapp.model.Quiz;
import com.example.quizapp.service.QuestionService;
import com.example.quizapp.service.QuizService;
import com.example.quizapp.support.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminQuestionController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminQuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuestionService questionService;
    @MockBean
    private QuizService quizService;

    @Test
    void listQuestions_rendersTemplateWithData() throws Exception {
        Quiz quiz = TestDataFactory.quiz(1L);
        when(quizService.getDefaultQuiz()).thenReturn(quiz);
        when(questionService.findByQuiz(quiz)).thenReturn(List.of(TestDataFactory.question(1L, quiz, 1)));

        mockMvc.perform(get("/admin/questions"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/questions"))
                .andExpect(model().attribute("quiz", quiz))
                .andExpect(model().attributeExists("questions"));
    }

    @Test
    void newQuestion_setsFormBackingBean() throws Exception {
        when(quizService.getDefaultQuiz()).thenReturn(TestDataFactory.quiz(3L));

        mockMvc.perform(get("/admin/questions/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/question-form"))
                .andExpect(model().attributeExists("question", "quiz"));
    }

    @Test
    void saveQuestion_createsAndRedirects() throws Exception {
        Quiz quiz = TestDataFactory.quiz(2L);
        when(quizService.getDefaultQuiz()).thenReturn(quiz);
        when(questionService.save(any(Question.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/admin/questions")
                        .param("text", "Q1")
                        .param("optionA", "A")
                        .param("optionB", "B")
                        .param("optionC", "C")
                        .param("optionD", "D")
                        .param("answer", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/questions"));

        verify(questionService).save(any(Question.class));
    }

    @Test
    void showEditForm_whenQuestionFound_rendersForm() throws Exception {
        Quiz quiz = TestDataFactory.quiz(1L);
        Question question = TestDataFactory.question(4L, quiz, 2);
        when(quizService.getDefaultQuiz()).thenReturn(quiz);
        when(questionService.findById(4L)).thenReturn(question);

        mockMvc.perform(get("/admin/questions/edit/4"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/question-form"))
                .andExpect(model().attribute("question", question));
    }

    @Test
    void showEditForm_whenQuestionMissing_redirectsWithError() throws Exception {
        when(questionService.findById(9L)).thenReturn(null);

        mockMvc.perform(get("/admin/questions/edit/9"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/questions"));
    }

    @Test
    void deleteQuestion_whenFound_removesAndRedirects() throws Exception {
        Quiz quiz = TestDataFactory.quiz(1L);
        Question question = TestDataFactory.question(9L, quiz, 2);
        when(questionService.findById(9L)).thenReturn(question);

        mockMvc.perform(post("/admin/questions/delete/9"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/questions"));

        verify(questionService).deleteById(9L);
    }
}
