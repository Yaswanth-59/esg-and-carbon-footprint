package com.example.carboncalculator.service;

import org.springframework.stereotype.Service;

@Service
public class ESGCalculationService {

    // ================= ENVIRONMENTAL SCORE =================
    // Lower carbon = higher score
    public double calculateEnvironmentalScore(double totalCarbon) {
        double score = 100 - totalCarbon;
        return clamp(score);
    }

    // ================= SOCIAL SCORE =================
    public double calculateSocialScore(int totalEmployees,
                                       int femaleEmployees,
                                       double trainingHours,
                                       int accidents) {

        if (totalEmployees <= 0) {
            return 0;
        }

        // Gender diversity ratio (0–1)
        double genderRatio = (double) femaleEmployees / totalEmployees;

        // Safety score (penalty for accidents)
        double safetyScore = 100 - (accidents * 10);

        // Training score capped at 100
        double trainingScore = Math.min(trainingHours * 0.5, 100);

        // Weighted social score
        double score =
                (genderRatio * 100 * 0.4)   // 40% weight
              + (trainingScore * 0.3)       // 30% weight
              + (safetyScore * 0.3);        // 30% weight

        return clamp(score);
    }

    // ================= GOVERNANCE SCORE =================
    public double calculateGovernanceScore(double auditScore,
                                           double policyCompliance,
                                           int boardMeetings,
                                           int ethicsViolations) {

        // Board meetings contribution capped
        double meetingScore = Math.min(boardMeetings * 5, 100);

        double score =
                (auditScore * 0.4)
              + (policyCompliance * 0.4)
              + (meetingScore * 0.2)
              - (ethicsViolations * 10);

        return clamp(score);
    }

    // ================= FINAL ESG =================
    public double calculateFinalESG(double e,
                                    double s,
                                    double g) {

        double finalScore = (e * 0.4) + (s * 0.3) + (g * 0.3);
        return clamp(finalScore);
    }

    // ================= NEW: ESG RATING =================
    public String getRating(double score) {
        if (score >= 80) return "Excellent";
        if (score >= 60) return "Good";
        if (score >= 40) return "Average";
        return "Poor";
    }

    // ================= NEW: ALERT MESSAGE =================
    public String getCarbonAlert(double totalCarbon) {
        if (totalCarbon > 500) {
            return "High carbon emission! Reduce electricity and transport usage.";
        } else if (totalCarbon > 300) {
            return "Moderate carbon level. Try to reduce energy consumption.";
        } else {
            return "Carbon level is within safe limits.";
        }
    }

    // ================= HELPER =================
    private double clamp(double value) {
        if (value < 0) return 0;
        if (value > 100) return 100;
        return value;
    }
}
