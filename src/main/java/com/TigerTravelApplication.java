package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = {"com.member", "com"})
@EnableJpaAuditing
public class TigerTravelApplication {

    public static void main(String[] args) {
        SpringApplication.run(TigerTravelApplication.class, args);
    }
}