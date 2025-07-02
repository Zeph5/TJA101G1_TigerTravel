package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.member", "com"})
public class TigerTravelApplication {

    public static void main(String[] args) {
        SpringApplication.run(TigerTravelApplication.class, args);
    }
}