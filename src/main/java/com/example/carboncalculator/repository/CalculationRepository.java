package com.example.carboncalculator.repository;

import com.example.carboncalculator.model.Calculation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CalculationRepository extends JpaRepository<Calculation, Long> {

    // Get all records for a user
    List<Calculation> findByEmail(String email);

    // Get records ordered by date
    List<Calculation> findByEmailOrderByCalculationDateDesc(String email);

    // Get latest record by ID
    Calculation findTopByEmailOrderByIdDesc(String email);

    // User ranking based on average eco score
    @Query("SELECT c.email, AVG(c.ecoScore) FROM Calculation c GROUP BY c.email ORDER BY AVG(c.ecoScore) DESC")
    List<Object[]> getUserRanking();
}
