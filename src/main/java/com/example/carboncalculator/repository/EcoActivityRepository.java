package com.example.carboncalculator.repository;

import com.example.carboncalculator.model.EcoActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EcoActivityRepository extends JpaRepository<EcoActivity, Long> {
    List<EcoActivity> findByEmail(String email);
}
