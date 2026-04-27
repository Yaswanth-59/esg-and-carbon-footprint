package com.example.carboncalculator.controller;

import com.example.carboncalculator.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SignupController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public String signup(@RequestParam String email,
                         @RequestParam String password) {

        userService.register(email, password);
        return "redirect:/login";
    }
}
