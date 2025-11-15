package com.example.quizapp.controller;

import com.example.quizapp.model.Quiz;
import com.example.quizapp.service.QuizService;
import com.example.quizapp.support.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuizController.class)
@AutoConfigureMockMvc(addFilters = false)
class QuizControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuizService quizService;

    @Test
    void home_whenAnonymous_setsGuestFlags() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("isAuthenticated", false))
                .andExpect(model().attributeDoesNotExist("isAdmin"));
    }

    @Test
    void home_whenAdminSetsFlag() throws Exception {
        GrantedAuthority admin = () -> "ROLE_ADMIN";
        TestingAuthenticationToken token = new TestingAuthenticationToken("admin", "pwd", List.of(admin));
        token.setAuthenticated(true);

        mockMvc.perform(get("/").principal(token))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("isAuthenticated", true))
                .andExpect(model().attribute("isAdmin", true));
    }

    @Test
    void showQuiz_populatesModel() throws Exception {
        Quiz quiz = TestDataFactory.quiz(3L);
        when(quizService.getDefaultQuiz()).thenReturn(quiz);
        when(quizService.getQuestionsForQuiz(quiz)).thenReturn(List.of());
        when(quizService.getTotalQuestions(quiz)).thenReturn(5L);

        mockMvc.perform(get("/quiz"))
                .andExpect(status().isOk())
                .andExpect(view().name("quiz"))
                .andExpect(model().attribute("quiz", quiz))
                .andExpect(model().attributeExists("questions", "totalQuestions"));

        verify(quizService).getQuestionsForQuiz(quiz);
    }

    @Test
    void submitQuiz_delegatesToQuizService() throws Exception {
        Quiz quiz = TestDataFactory.quiz(1L);
        when(quizService.getDefaultQuiz()).thenReturn(quiz);
        when(quizService.extractAnswerMap(any())).thenReturn(Map.of(1L, 2));
        when(quizService.calculateScore(eq(quiz), anyMap())).thenReturn(1);
        when(quizService.getTotalQuestions(quiz)).thenReturn(5L);

        mockMvc.perform(post("/submit")
                        .param("username", "Player"))
                .andExpect(status().isOk())
                .andExpect(view().name("result"))
                .andExpect(model().attribute("score", 1))
                .andExpect(model().attribute("total", 5));

        verify(quizService).saveResult(quiz, "Player", 1, 5);
    }
}
