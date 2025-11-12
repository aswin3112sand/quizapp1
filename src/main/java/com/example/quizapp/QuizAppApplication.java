package com.example.quizapp;

import com.example.quizapp.model.Question;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootApplication
public class QuizAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuizAppApplication.class, args);
    }

    @Bean
    CommandLineRunner seedQuiz(QuizRepository quizzes, QuestionRepository questions) {
        return args -> {
            Quiz quiz = quizzes.findBySlug("general-knowledge")
                    .orElseGet(() -> quizzes.save(new Quiz(
                            "General Knowledge",
                            "general-knowledge",
                            "Starter quiz from the GUVI tutorial",
                            180
                    )));

            if (questions.countByQuiz(quiz) == 0) {
                questions.saveAll(List.of(
                        new Question(
                                "What is the capital of France?",
                                List.of("Berlin", "Madrid", "Paris", "Rome"),
                                2,
                                quiz
                        ),
                        new Question(
                                "2 + 2 = ?",
                                List.of("3", "4", "5", "22"),
                                1,
                                quiz
                        ),
                        new Question(
                                "Which language runs in a web browser?",
                                List.of("C", "Java", "Python", "JavaScript"),
                                3,
                                quiz
                        ),
                        new Question(
                                "Spring Data JPA is used for ____.",
                                List.of("Security", "Database access", "UI Styling", "Caching"),
                                1,
                                quiz
                        )
                ));
                System.out.println("✅ Seeded default quiz and questions.");
            }
        };
    }

    @Bean
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
