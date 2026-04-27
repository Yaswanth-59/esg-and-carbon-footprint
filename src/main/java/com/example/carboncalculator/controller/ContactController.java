package com.example.carboncalculator.controller;

import com.example.carboncalculator.model.Feedback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.carboncalculator.repository.FeedbackRepository;

@Controller
public class ContactController {

    @Autowired
    private FeedbackRepository contactRepository;

    @PostMapping("/contact")
    public String saveContact(@RequestParam String name,
                              @RequestParam String email,
                              @RequestParam String number,
                              @RequestParam String subject,
                              @RequestParam String message) {

        Feedback contact = new Feedback();
        contact.setName(name);
        contact.setEmail(email);
        contact.setNumber(number);
        contact.setSubject(subject);
        contact.setMessage(message);

        contactRepository.save(contact);

        return "redirect:/?success=true";
    }
}
