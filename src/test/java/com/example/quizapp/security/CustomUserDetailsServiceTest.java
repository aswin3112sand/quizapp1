package com.example.quizapp.security;

import com.example.quizapp.model.Role;
import com.example.quizapp.model.User;
import com.example.quizapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    void loadUserByUsername_returnsSpringSecurityUser() {
        User user = new User("User", "user@mail.com", "secret1", Role.ADMIN);
        when(userRepository.findByEmailIgnoreCase("user@mail.com"))
                .thenReturn(Optional.of(user));

        UserDetails details = userDetailsService.loadUserByUsername("user@mail.com");

        assertThat(details.getUsername()).isEqualTo("user@mail.com");
        assertThat(details.getAuthorities()).extracting("authority")
                .contains("ROLE_ADMIN");
    }

    @Test
    void loadUserByUsername_whenMissing_throwsException() {
        when(userRepository.findByEmailIgnoreCase("missing@mail.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("missing@mail.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
