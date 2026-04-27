package com.example.carboncalculator.model;

import jakarta.persistence.*;

@Entity
public class GovernanceData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private int compliance;
    private int audit;
    private int policy;
    private int diversity;
    private int score;

    // Getters and setters
    public Long getId() { return id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getCompliance() { return compliance; }
    public void setCompliance(int compliance) { this.compliance = compliance; }

    public int getAudit() { return audit; }
    public void setAudit(int audit) { this.audit = audit; }

    public int getPolicy() { return policy; }
    public void setPolicy(int policy) { this.policy = policy; }

    public int getDiversity() { return diversity; }
    public void setDiversity(int diversity) { this.diversity = diversity; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
}
