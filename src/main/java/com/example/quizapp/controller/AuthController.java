package com.example.quizapp.controller;

import com.example.quizapp.service.UserService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    public static class RegisterRequest {
        @NotBlank(message = "Name is required")
        private String name;
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email")
        private String email;
        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "At least 6 characters")
        private String password;
        @NotBlank(message = "Confirm password")
        private String confirm;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getConfirm() { return confirm; }
        public void setConfirm(String confirm) { this.confirm = confirm; }
    }

    @ModelAttribute("form")
    public RegisterRequest registerFormBacking() {
        return new RegisterRequest();
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("form", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@Valid @ModelAttribute("form") RegisterRequest form,
                             BindingResult result,
                             HttpServletRequest request,
                             Model model) {
        if (!form.getPassword().equals(form.getConfirm())) {
            result.rejectValue("confirm", "mismatch", "Passwords do not match");
        }
        if (userService.existsByEmail(form.getEmail())) {
            result.rejectValue("email", "exists", "Email already registered");
        }
        if (result.hasErrors()) {
            return "register";
        }

        userService.register(form.getName(), form.getEmail(), form.getPassword());
        return "redirect:/login?registered=true";
    }
}
