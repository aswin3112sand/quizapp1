package com.example.quizapp.service;

import com.example.quizapp.model.Role;
import com.example.quizapp.model.User;
import com.example.quizapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    @Test
    void findByEmail_trimsInputAndDelegates() {
        User user = new User("User", "user@mail.com", "pwd", Role.USER);
        when(userRepository.findByEmailIgnoreCase("user@mail.com"))
                .thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail(" user@mail.com ");

        assertThat(result).contains(user);
        verify(userRepository).findByEmailIgnoreCase("user@mail.com");
    }

    @Test
    void existsByEmail_withNull_returnsFalse() {
        assertThat(userService.existsByEmail(null)).isFalse();
        verify(userRepository, never()).existsByEmailIgnoreCase(any());
    }

    @Test
    void register_persistsNewUserWithEncodedPassword() {
        when(userRepository.existsByEmailIgnoreCase("a@mail.com")).thenReturn(false);
        when(passwordEncoder.encode("secret1")).thenReturn("encoded");
        User saved = new User("Name", "a@mail.com", "encoded", Role.USER);
        when(userRepository.save(any(User.class))).thenReturn(saved);

        User user = userService.register("Name", "a@mail.com", "secret1");

        assertThat(user).isSameAs(saved);
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getPassword()).isEqualTo("encoded");
    }

    @Test
    void register_whenEmailExists_throwsException() {
        when(userRepository.existsByEmailIgnoreCase("taken@mail.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.register("N", "taken@mail.com", "secret1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already registered");
    }

    @Test
    void register_whenPasswordTooShort_throwsException() {
        when(userRepository.existsByEmailIgnoreCase("short@mail.com")).thenReturn(false);

        assertThatThrownBy(() -> userService.register("N", "short@mail.com", "123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password must be at least");
        verify(userRepository, never()).save(any());
    }
}
