package com.example.quizapp;

import com.example.quizapp.config.DefaultQuizData;
import com.example.quizapp.model.Quiz;
import com.example.quizapp.model.Role;
import com.example.quizapp.model.User;
import com.example.quizapp.repository.QuestionRepository;
import com.example.quizapp.repository.QuizRepository;
import com.example.quizapp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class QuizAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuizAppApplication.class, args);
    }

    @Bean
    @Profile("!test")
    CommandLineRunner seedQuiz(QuizRepository quizzes, QuestionRepository questions) {
        return args -> {
            Quiz quiz = quizzes.findBySlug(DefaultQuizData.DEFAULT_SLUG)
                    .orElseGet(() -> quizzes.save(DefaultQuizData.buildDefaultQuiz()));

            if (questions.countByQuiz(quiz) == 0) {
                questions.saveAll(DefaultQuizData.buildQuestions(quiz));
                System.out.println("✅ Seeded default quiz and questions.");
            }
        };
    }

    @Bean
    @Profile("!test")
    CommandLineRunner seedAdmin(UserRepository users, PasswordEncoder encoder) {
        return args -> {
            users.findByEmailIgnoreCase("admin3112@gmail.com").ifPresentOrElse(existing -> {
                boolean updated = false;
                if (existing.getRole() != Role.ADMIN) {
                    existing.setRole(Role.ADMIN);
                    updated = true;
                }
                if (!encoder.matches("nextnext", existing.getPassword())) {
                    existing.setPassword(encoder.encode("nextnext"));
                    updated = true;
                }
                if (updated) {
                    users.save(existing);
                }
            }, () -> {
                User admin = new User("Admin", "admin3112@gmail.com", encoder.encode("nextnext"), Role.ADMIN);
                users.save(admin);
                System.out.println("✅ Seeded admin admin3112@gmail.com / nextnext");
            });
        };
    }
}
