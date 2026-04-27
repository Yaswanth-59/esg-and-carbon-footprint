package com.example.carboncalculator.controller;

import com.example.carboncalculator.model.Calculation;
import com.example.carboncalculator.model.EcoActivity;
import com.example.carboncalculator.model.Feedback;
import com.example.carboncalculator.model.SocialData;
import com.example.carboncalculator.model.GovernanceData;

import com.example.carboncalculator.repository.CalculationRepository;
import com.example.carboncalculator.repository.EcoActivityRepository;
import com.example.carboncalculator.repository.FeedbackRepository;
import com.example.carboncalculator.repository.SocialRepository;
import com.example.carboncalculator.repository.GovernanceRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class PageController {

    @Autowired
    private CalculationRepository calculationRepository;

    @Autowired
    private EcoActivityRepository ecoActivityRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private SocialRepository socialRepository;

    @Autowired
    private GovernanceRepository governanceRepository;

   
   // ================= HISTORY PAGE WITH FILTER =================
@GetMapping("/history")
public String history(
        HttpSession session,
        Model model,
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate,
        @RequestParam(required = false) String email) {

    String sessionEmail = (String) session.getAttribute("userEmail");
    if (sessionEmail == null) {
        return "redirect:/login";
    }

    // Use filtered email if provided
    String targetEmail = (email != null && !email.isEmpty())
            ? email
            : sessionEmail;

    // Environmental history
    List<Calculation> envList =
            calculationRepository.findByEmail(targetEmail);
    model.addAttribute("envList", envList);

    // Social history
    List<SocialData> socialList =
            socialRepository.findByEmail(targetEmail);
    model.addAttribute("socialList", socialList);

    // Governance history
    List<GovernanceData> govList =
            governanceRepository.findByEmail(targetEmail);
    model.addAttribute("govList", govList);

    return "history";
}


    // ================= ECO ACTIVITIES PAGE =================
    @GetMapping("/eco")
    public String ecoActivities(HttpSession session, Model model) {

        String email = (String) session.getAttribute("userEmail");
        if (email == null) {
            return "redirect:/login";
        }

        model.addAttribute("activities",
                ecoActivityRepository.findByEmail(email));

        return "eco";
    }

    // ================= ADD ECO ACTIVITY =================
    @PostMapping("/add-activity")
    public String addActivity(HttpSession session,
                              @RequestParam String activity,
                              @RequestParam int points) {

        String email = (String) session.getAttribute("userEmail");
        if (email == null) {
            return "redirect:/login";
        }

        EcoActivity eco = new EcoActivity();
        eco.setEmail(email);
        eco.setActivity(activity);
        eco.setPoints(points);

        ecoActivityRepository.save(eco);

        return "redirect:/eco";
    }

    // ================= USER RANKING =================
    @GetMapping("/ranking")
    public String ranking(Model model) {

        List<Object[]> ranking = calculationRepository.getUserRanking();
        model.addAttribute("ranking", ranking);

        return "ranking";
    }

    // ================= SAVE ESG FEEDBACK =================
    @PostMapping("/feedback")
    public String saveFeedback(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam(required = false) String number,
            @RequestParam(required = false) String subject,
            @RequestParam String message,
            Model model) {

        Feedback feedback = new Feedback();
        feedback.setName(name);
        feedback.setEmail(email);
        feedback.setNumber(number);
        feedback.setSubject(subject);
        feedback.setMessage(message);

        feedbackRepository.save(feedback);

        model.addAttribute("success", "Thank you for your feedback!");
        return "index";
    }
}
