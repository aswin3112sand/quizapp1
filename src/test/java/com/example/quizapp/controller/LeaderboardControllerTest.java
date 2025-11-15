package com.example.quizapp.controller;

import com.example.quizapp.dto.LeaderboardEntry;
import com.example.quizapp.model.Quiz;
import com.example.quizapp.service.QuizService;
import com.example.quizapp.support.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LeaderboardController.class)
@AutoConfigureMockMvc(addFilters = false)
class LeaderboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuizService quizService;

    @Test
    void viewLeaderboard_rendersTemplate() throws Exception {
        Quiz quiz = TestDataFactory.quiz(4L);
        when(quizService.getDefaultQuiz()).thenReturn(quiz);

        mockMvc.perform(get("/leaderboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("leaderboard"))
                .andExpect(model().attribute("quiz", quiz));
    }

    @Test
    void leaderboardData_returnsJsonPayload() throws Exception {
        Quiz quiz = TestDataFactory.quiz(2L);
        when(quizService.getDefaultQuiz()).thenReturn(quiz);
        when(quizService.getLeaderboardEntries(quiz))
                .thenReturn(List.of(new LeaderboardEntry("Player", 5, 10, "2024-01-01 10:00")));

        mockMvc.perform(get("/api/leaderboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].displayName").value("Player"))
                .andExpect(jsonPath("$[0].score").value(5));
    }
}
