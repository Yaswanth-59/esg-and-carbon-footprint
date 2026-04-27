package com.example.carboncalculator.controller;

import com.example.carboncalculator.model.Event;
import com.example.carboncalculator.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    // Save event
    @PostMapping
    public Event addEvent(@RequestBody Event event) {
        return eventRepository.save(event);
    }

    // Get all events
    @GetMapping
    public List<Event> getEvents() {
        return eventRepository.findAll();
    }
}
