package com.example.carboncalculator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.carboncalculator.repository")
@EntityScan(basePackages = "com.example.carboncalculator.model")
public class CarbonCalculatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarbonCalculatorApplication.class, args);
    }
}
