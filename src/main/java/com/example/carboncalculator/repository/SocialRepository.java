package com.example.carboncalculator.repository;

import com.example.carboncalculator.model.SocialData;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SocialRepository extends JpaRepository<SocialData, Long> {
    List<SocialData> findByEmail(String email);
}
