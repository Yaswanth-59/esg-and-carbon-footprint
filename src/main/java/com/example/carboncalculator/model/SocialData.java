package com.example.carboncalculator.model;

import jakarta.persistence.*;

@Entity
public class SocialData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private int employees;
    private int safety;
    private int training;
    private int community;
    private int score;

    // Getters and setters
    public Long getId() { return id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getEmployees() { return employees; }
    public void setEmployees(int employees) { this.employees = employees; }

    public int getSafety() { return safety; }
    public void setSafety(int safety) { this.safety = safety; }

    public int getTraining() { return training; }
    public void setTraining(int training) { this.training = training; }

    public int getCommunity() { return community; }
    public void setCommunity(int community) { this.community = community; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
}
