package com.example.carboncalculator.repository;

import com.example.carboncalculator.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
