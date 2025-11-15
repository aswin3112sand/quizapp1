package com.example.quizapp.controller;

import com.example.quizapp.model.Role;
import com.example.quizapp.model.User;
import com.example.quizapp.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void loginPage_returnsLoginTemplate() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void registerForm_returnsRegisterTemplate() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @Test
    void doRegister_whenPasswordsMismatch_returnsForm() throws Exception {
        when(userService.existsByEmail("user@mail.com")).thenReturn(false);

        mockMvc.perform(post("/register")
                        .param("name", "User")
                        .param("email", "user@mail.com")
                        .param("password", "secret1")
                        .param("confirm", "different"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));

        verify(userService, never()).register(any(), any(), any());
    }

    @Test
    void doRegister_whenEmailExists_returnsFormWithError() throws Exception {
        when(userService.existsByEmail("user@mail.com")).thenReturn(true);

        mockMvc.perform(post("/register")
                        .param("name", "User")
                        .param("email", "user@mail.com")
                        .param("password", "secret1")
                        .param("confirm", "secret1"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));

        verify(userService, never()).register(any(), any(), any());
    }

    @Test
    void doRegister_whenValid_redirectsToLogin() throws Exception {
        when(userService.existsByEmail("user@mail.com")).thenReturn(false);
        when(userService.register("User", "user@mail.com", "secret1"))
                .thenReturn(new User("User", "user@mail.com", "encoded", Role.USER));

        mockMvc.perform(post("/register")
                        .param("name", "User")
                        .param("email", "user@mail.com")
                        .param("password", "secret1")
                        .param("confirm", "secret1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered=true"));

        verify(userService).register("User", "user@mail.com", "secret1");
    }
}
