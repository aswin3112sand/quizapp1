package com.example.quizapp.controller;

import com.example.quizapp.model.Result;
import com.example.quizapp.model.Role;
import com.example.quizapp.model.User;
import com.example.quizapp.repository.ResultRepository;
import com.example.quizapp.repository.UserRepository;
import com.example.quizapp.support.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MeController.class)
@AutoConfigureMockMvc(addFilters = false)
class MeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResultRepository resultRepository;
    @MockBean
    private UserRepository userRepository;

    @Test
    void myScores_whenAnonymous_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/me/scores"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void myScores_whenUserNotFound_redirects() throws Exception {
        TestingAuthenticationToken token = new TestingAuthenticationToken("user@mail.com", "pwd");
        when(userRepository.findByEmailIgnoreCase("user@mail.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/me/scores").principal(token))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void myScores_whenUserExists_returnsScores() throws Exception {
        TestingAuthenticationToken token = new TestingAuthenticationToken("user@mail.com", "pwd");
        User user = TestDataFactory.user(1L, Role.USER);
        when(userRepository.findByEmailIgnoreCase("user@mail.com")).thenReturn(Optional.of(user));
        Result result = TestDataFactory.result(2L, user, TestDataFactory.quiz(1L), 5, 10, null);
        when(resultRepository.findByUserOrderByCreatedAtDesc(user)).thenReturn(List.of(result));

        mockMvc.perform(get("/me/scores").principal(token))
                .andExpect(status().isOk())
                .andExpect(view().name("me-scores"))
                .andExpect(model().attributeExists("results"));
    }
}
