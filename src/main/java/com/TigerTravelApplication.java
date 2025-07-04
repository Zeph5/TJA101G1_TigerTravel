package com;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(scanBasePackages = {"com.member", "com"})
public class TigerTravelApplication {

    public static void main(String[] args) {
        SpringApplication.run(TigerTravelApplication.class, args);
    }
    
    public CommandLineRunner printRoutes(ApplicationContext ctx) {
        return args -> {
            System.out.println("🎯 所有註冊的 Controller Beans:");
            String[] beanNames = ctx.getBeanNamesForAnnotation(org.springframework.stereotype.Controller.class);
            Arrays.sort(beanNames);
            for (String name : beanNames) {
                System.out.println("✔ " + name);
            }
        };
    }
}