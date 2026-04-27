package com.example.carboncalculator.controller;

import com.example.carboncalculator.model.Calculation;
import com.example.carboncalculator.model.SocialData;
import com.example.carboncalculator.model.GovernanceData;
import com.example.carboncalculator.model.EcoActivity;
import com.example.carboncalculator.repository.CalculationRepository;
import com.example.carboncalculator.repository.SocialRepository;
import com.example.carboncalculator.repository.GovernanceRepository;
import com.example.carboncalculator.repository.EcoActivityRepository;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private CalculationRepository calculationRepository;

    @Autowired
    private SocialRepository socialRepository;

    @Autowired
    private GovernanceRepository governanceRepository;

    @Autowired
    private EcoActivityRepository ecoActivityRepository;

    // ================= DASHBOARD =================
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {

        String email = (String) session.getAttribute("userEmail");

        if (email == null) {
            return "redirect:/login";
        }

        // ENVIRONMENTAL DATA
        List<Calculation> envList = calculationRepository.findByEmail(email);

        double totalElectricity = 0;
        double totalTransport = 0;
        double totalFood = 0;
        double totalWaste = 0;

        List<Double> trendValues = new ArrayList<>();

        for (Calculation c : envList) {
            totalElectricity += c.getElectricity();
            totalTransport += c.getTransport();
            totalFood += c.getFood();
            totalWaste += c.getWaste();
            trendValues.add(c.getEcoScore());
        }

        model.addAttribute("electricity", totalElectricity);
        model.addAttribute("food", totalFood);
        model.addAttribute("transport", totalTransport);
        model.addAttribute("waste", totalWaste);
        model.addAttribute("trendValues", trendValues);

        Calculation latest =
                calculationRepository.findTopByEmailOrderByIdDesc(email);

        int environmentalScore = latest == null
                ? 0
                : (int) latest.getEcoScore();

        // SOCIAL
        List<SocialData> socialList = socialRepository.findByEmail(email);
        int socialScore = socialList.isEmpty()
                ? 0
                : socialList.get(socialList.size() - 1).getScore();

        // GOVERNANCE
        List<GovernanceData> govList = governanceRepository.findByEmail(email);
        int governanceScore = govList.isEmpty()
                ? 0
                : govList.get(govList.size() - 1).getScore();

        // ESG SCORE
        int esgScore = (int) (
                (environmentalScore * 0.4) +
                (socialScore * 0.3) +
                (governanceScore * 0.3)
        );

        model.addAttribute("environmentalScore", environmentalScore);
        model.addAttribute("socialScore", socialScore);
        model.addAttribute("governanceScore", governanceScore);
        model.addAttribute("esgScore", esgScore);

        // CARBON EQUIVALENTS
        double totalCarbon = totalElectricity
                           + totalTransport
                           + totalFood
                           + totalWaste;

        int treesNeeded = (int) (totalCarbon / 20);
        int carKm = (int) (totalCarbon / 0.25);

        model.addAttribute("treesNeeded", treesNeeded);
        model.addAttribute("carKm", carKm);

        // ESG RISK
        String esgRisk;
        if (esgScore >= 80)
            esgRisk = "Low Risk";
        else if (esgScore >= 60)
            esgRisk = "Medium Risk";
        else
            esgRisk = "High Risk";

        model.addAttribute("esgRisk", esgRisk);

        // GREEN BADGE
        String ecoBadge;
        if (environmentalScore >= 90)
            ecoBadge = "🌱 Green Champion";
        else if (environmentalScore >= 75)
            ecoBadge = "🌿 Eco Friendly";
        else if (environmentalScore >= 50)
            ecoBadge = "🌍 Average";
        else
            ecoBadge = "⚠ Needs Improvement";

        model.addAttribute("ecoBadge", ecoBadge);

        // ESG ACTIVITY
     List<EcoActivity> activities = ecoActivityRepository.findByEmail(email);

int totalPoints = 0;
if (activities != null && !activities.isEmpty()) {
    for (EcoActivity a : activities) {
        totalPoints += a.getPoints();
    }
}

// Prevent chart from breaking when value is 0
if (totalPoints < 0) {
    totalPoints = 0;
}

model.addAttribute("activityPoints", totalPoints);


        // CARBON INTENSITY
        double carbonIntensity;
        if (totalPoints > 0)
            carbonIntensity = totalCarbon / totalPoints;
        else
            carbonIntensity = totalCarbon;

        carbonIntensity = Math.round(carbonIntensity * 100.0) / 100.0;
        model.addAttribute("carbonIntensity", carbonIntensity);

        // PREDICTED SCORE
        double predictedScore = environmentalScore;
        if (trendValues.size() >= 3) {
            int size = trendValues.size();
            predictedScore =
                    (trendValues.get(size - 1)
                   + trendValues.get(size - 2)
                   + trendValues.get(size - 3)) / 3.0;
        }
        model.addAttribute("predictedScore", (int) predictedScore);

        // LAST RESULT
        double lastEcoScore = latest == null ? 0 : latest.getEcoScore();
        String lastRating = latest == null ? "No Data" : latest.getRating();
        String lastAlert = latest == null
                ? "No data available."
                : (String) session.getAttribute("lastAlert");

        model.addAttribute("lastEcoScore", lastEcoScore);
        model.addAttribute("lastRating", lastRating);
        model.addAttribute("lastAlert", lastAlert);

        model.addAttribute("history", envList);

        return "dashboard";
    }

    // ================= ENVIRONMENTAL PAGE =================
    @GetMapping("/environmental")
    public String environmentalPage() {
        return "environmental";
    }

    // ================= SOCIAL PAGE =================
    @GetMapping("/social")
    public String socialPage() {
        return "social";
    }

    // ================= GOVERNANCE PAGE =================
    @GetMapping("/governance")
    public String governancePage() {
        return "governance";
    }

    // ================= ESG SCORE PAGE =================
    @GetMapping("/esg")
    public String esgPage(HttpSession session, Model model) {

        String email = (String) session.getAttribute("userEmail");
        if (email == null) {
            return "redirect:/login";
        }

        Calculation latest =
                calculationRepository.findTopByEmailOrderByIdDesc(email);

        int environmentalScore = latest == null
                ? 0
                : (int) latest.getEcoScore();

        List<SocialData> socialList =
                socialRepository.findByEmail(email);
        int socialScore = socialList.isEmpty()
                ? 0
                : socialList.get(socialList.size() - 1).getScore();

        List<GovernanceData> govList =
                governanceRepository.findByEmail(email);
        int governanceScore = govList.isEmpty()
                ? 0
                : govList.get(govList.size() - 1).getScore();

        int esgScore = (int) (
                (environmentalScore * 0.4) +
                (socialScore * 0.3) +
                (governanceScore * 0.3)
        );

        model.addAttribute("environmentalScore", environmentalScore);
        model.addAttribute("socialScore", socialScore);
        model.addAttribute("governanceScore", governanceScore);
        model.addAttribute("esgScore", esgScore);

        return "esg";
    }

    // ================= SOCIAL SUBMIT =================
    @PostMapping("/social-submit")
    public String saveSocial(HttpSession session,
                             @RequestParam int employees,
                             @RequestParam int safety,
                             @RequestParam int training,
                             @RequestParam int community) {

        String email = (String) session.getAttribute("userEmail");
        if (email == null) {
            return "redirect:/login";
        }

        int score = (employees + safety + training + community) / 4;

        SocialData social = new SocialData();
        social.setEmail(email);
        social.setEmployees(employees);
        social.setSafety(safety);
        social.setTraining(training);
        social.setCommunity(community);
        social.setScore(score);

        socialRepository.save(social);

        return "redirect:/dashboard";
    }

    // ================= GOVERNANCE SUBMIT =================
    @PostMapping("/governance-submit")
    public String saveGovernance(HttpSession session,
                                 @RequestParam int compliance,
                                 @RequestParam int audit,
                                 @RequestParam int policy,
                                 @RequestParam int diversity) {

        String email = (String) session.getAttribute("userEmail");
        if (email == null) {
            return "redirect:/login";
        }

        int score = (compliance + audit + policy + diversity) / 4;

        GovernanceData gov = new GovernanceData();
        gov.setEmail(email);
        gov.setCompliance(compliance);
        gov.setAudit(audit);
        gov.setPolicy(policy);
        gov.setDiversity(diversity);
        gov.setScore(score);

        governanceRepository.save(gov);

        return "redirect:/dashboard";
    }
}
