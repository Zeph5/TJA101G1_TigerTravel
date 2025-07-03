package com.member.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 將帳密顯示出來方便登入 (僅限開發測試)
        return NoOpPasswordEncoder.getInstance(); // 這是明碼比對，僅限開發測試
        // 在生產環境請使用：return new BCryptPasswordEncoder();
    }
}
