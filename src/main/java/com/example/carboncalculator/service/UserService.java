package com.example.carboncalculator.service;

import com.example.carboncalculator.model.User;
import com.example.carboncalculator.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public boolean authenticate(String email, String password) {
        User user = userRepository.findByEmail(email);
        return user != null && encoder.matches(password, user.getPassword());
    }

    public void register(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(encoder.encode(password));
        userRepository.save(user);
    }
}
