package com.ticket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // 關閉 CSRF
            .authorizeRequests()
                .anyRequest().permitAll() // 所有請求都允許
            .and()
            .formLogin().disable() // 關閉登入畫面
            .httpBasic().disable(); // 關閉基本驗證

        return http.build();
    }
}