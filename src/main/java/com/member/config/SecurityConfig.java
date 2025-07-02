package com.member.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.manager.security.ManagerUserDetailService;
import com.member.security.MemberUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private MemberUserDetailsService memberDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // ✅ 允許匿名訪問的頁面
                .requestMatchers(
                		"/test",
                    "/login", "/member/login",         // 登入畫面與登入處理
                    "/register", "/member/register",   // 註冊畫面與送出
                    "/index", "/error", "/login?error", // 首頁、錯誤頁
                    "/css/**", "/js/**", "/images/**"  // 靜態資源
                ).permitAll()

                // 🔐 其他 /member/** 路徑需登入
                .requestMatchers("/member/**").authenticated()
            )

            // 🔐 自訂登入流程
            .formLogin(form -> form
                .loginPage("/login") // 顯示登入畫面
                .loginProcessingUrl("/member/login") // 表單送出處理路徑（與 login.html 表單一致）
                .defaultSuccessUrl("/index", true) // 登入成功後導向
                .failureUrl("/login?error") // 登入失敗導向
                .permitAll()
            )

            // 🔓 登出設定
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/index")
                .permitAll()
            );

        return http.build();
    }

    // ❗ 僅供開發測試用，正式環境請改為 BCryptPasswordEncoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
