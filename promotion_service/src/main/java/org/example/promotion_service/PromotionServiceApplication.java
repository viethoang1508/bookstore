package org.example.promotion_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@SpringBootApplication
public class PromotionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PromotionServiceApplication.class, args);
    }

}
