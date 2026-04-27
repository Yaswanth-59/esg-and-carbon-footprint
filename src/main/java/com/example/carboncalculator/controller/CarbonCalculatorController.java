package com.example.carboncalculator.controller;

import com.example.carboncalculator.model.Calculation;
import com.example.carboncalculator.repository.CalculationRepository;
import com.example.carboncalculator.service.ESGCalculationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
public class CarbonCalculatorController {

    @Autowired
    private CalculationRepository calculationRepository;

    @Autowired
    private ESGCalculationService esgService;

    @PostMapping("/calculate")
    public String calculate(@RequestParam double electricity,
                            @RequestParam double transport,
                            @RequestParam double food,
                            @RequestParam double waste,
                            HttpSession session) {

        String email = (String) session.getAttribute("userEmail");

        if (email == null) {
            return "redirect:/login";
        }

        // ================= CARBON CALCULATION =================
        double electricityCarbon = electricity * 0.5;
        double transportCarbon = transport * 0.2;
        double foodCarbon = food * 2.5;
        double wasteCarbon = waste * 1.8;

        double total = electricityCarbon
                     + transportCarbon
                     + foodCarbon
                     + wasteCarbon;

        // ================= FIXED ECO SCORE =================
        double ecoScore = Math.max(0, 100 - (total / 10));

        // ================= RATING + ALERT =================
        String rating = esgService.getRating(ecoScore);
        String alert = esgService.getCarbonAlert(total);

        // ================= SAVE =================
        Calculation c = new Calculation();
        c.setEmail(email);
        c.setElectricity(electricityCarbon);
        c.setTransport(transportCarbon);
        c.setFood(foodCarbon);
        c.setWaste(wasteCarbon);
        c.setTotal(total);
        c.setEcoScore(ecoScore);
        c.setRating(rating);
        c.setCalculationDate(LocalDate.now());

        calculationRepository.save(c);

        // Store in session
        session.setAttribute("lastEcoScore", ecoScore);
        session.setAttribute("lastRating", rating);
        session.setAttribute("lastAlert", alert);

        return "redirect:/dashboard";
    }
}
