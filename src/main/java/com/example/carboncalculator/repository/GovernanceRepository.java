package com.example.carboncalculator.repository;

import com.example.carboncalculator.model.GovernanceData;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GovernanceRepository extends JpaRepository<GovernanceData, Long> {
    List<GovernanceData> findByEmail(String email);
}
