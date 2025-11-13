package com.example.quizapp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final CustomUserDetailsService customUserDetailsService;

  public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
    this.customUserDetailsService = customUserDetailsService;
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .userDetailsService(customUserDetailsService)
      .csrf(csrf -> csrf
        .ignoringRequestMatchers("/ws/**", "/h2/**"))
      .headers(headers -> headers
        .frameOptions(frame -> frame.disable()))
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/", "/login", "/register",
                         "/css/**", "/js/**", "/images/**",
                         "/webjars/**", "/favicon.ico",
                         "/ws/**", "/api/**", "/h2/**", "/error").permitAll()
        .requestMatchers("/admin/**").hasRole("ADMIN")
        .anyRequest().authenticated()
      )
      .formLogin(form -> form
        .loginPage("/login")
        .loginProcessingUrl("/login")
        .successHandler(roleAwareSuccessHandler())
        .failureUrl("/login?error=true")
        .permitAll()
      )
      .logout(logout -> logout
        .logoutUrl("/logout")
        .logoutSuccessUrl("/login?logout=true")
        .deleteCookies("JSESSIONID")
        .permitAll()
      );
    return http.build();
  }

  private AuthenticationSuccessHandler roleAwareSuccessHandler() {
    return (request, response, authentication) -> {
      boolean isAdmin = authentication.getAuthorities().stream()
          .anyMatch(granted -> "ROLE_ADMIN".equals(granted.getAuthority()));
      String target = isAdmin ? "/admin" : "/quiz/home";
      if (!response.isCommitted()) {
        response.sendRedirect(target);
      }
    };
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
