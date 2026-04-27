package com.example.carboncalculator.repository;

import com.example.carboncalculator.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
}
