package com.example.carboncalculator.controller;

import com.example.carboncalculator.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session) {

        if (userService.authenticate(email, password)) {
            session.setAttribute("userEmail", email);
            return "redirect:/dashboard";
        }

        return "redirect:/login?error=true";
    }

    // LOGOUT METHOD (fixes your error)
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();   // destroys session
        return "redirect:/login";
    }
}
