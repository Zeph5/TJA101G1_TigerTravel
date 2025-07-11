package com.member.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // 推薦使用 BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.manager.security.ManagerUserDetailService;
import com.member.security.CustomAuthenticationFailureHandler; // 如果您還需要這個
import com.member.security.MemberUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final MemberUserDetailsService memberUserDetailsService;
    private final ManagerUserDetailService managerUserDetailService;
    private final CustomAuthenticationFailureHandler failureHandler; // 假設您還需要它

    // 建構子注入所有依賴
    public SecurityConfig(MemberUserDetailsService memberUserDetailsService,
                          ManagerUserDetailService managerUserDetailService,
                          CustomAuthenticationFailureHandler failureHandler) { // 注入 failureHandler
        this.memberUserDetailsService = memberUserDetailsService;
        this.managerUserDetailService = managerUserDetailService;
        this.failureHandler = failureHandler;
    }

    // Bean: 用於會員的 SecurityFilterChain (優先順序較低，排在後面處理)
    @Bean
    @Order(2) // 設定這個 SecurityFilterChain 的優先順序，確保 Manager 的先匹配
    public SecurityFilterChain memberFilterChain(HttpSecurity http,
            AuthenticationProvider memberAuthenticationProvider) throws Exception {

        http
            .csrf(csrf -> csrf.disable()) // 停用 CSRF 防護（依實際情況調整）
            .securityMatcher("/", "/index", "/login", "/register", 
                "ticketOrders", "/member/receipt/**", "/member/password/**", // ✅ 密碼相關頁面
                "/member/**", "/ticket/**", "/scenery/**", "/logout",
                "/login?error", "/error", "/css/**", "/js/**", "/images/**", "/member/verify")
            .authorizeHttpRequests(auth -> auth
                // ✅ 允許匿名訪問的路徑
                .requestMatchers(
                    "/login", "/index", "/member/register", "/register",
                    "/login?error", "/error",
                    "/css/**", "/js/**", "/images/**",
                    "/member/verify", "/scenery/**"
                ).permitAll()

                .requestMatchers(
                	    "/member/forgotPassword",
                	    "/member/resetPassword",
                	    "/member/reset_Password_fail"
                	).permitAll()
                
                // ✅ 密碼重設相關頁面開放匿名訪問
                .requestMatchers(
                		"/member/reset", "/member/resetPassword","/member/reset-password",
                	"/member/forgot", "/member/forgotPassword", "/member/reset_Password",
                    "/member/password/forgotPassword",
                    "/member/reset", "/member/password/resetPassword", "/member/password/reset_Password_fail"
                ).permitAll()

                // ✅ 管理員專用路徑
                .requestMatchers("/member/list").hasRole("MANAGER")

                // ✅ 需登入的路徑
                .requestMatchers("/ticket/**").authenticated()
                .requestMatchers("/member/receipt/**").authenticated()
                .requestMatchers("/member/**").authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")                      // 自訂登入頁面
                .loginProcessingUrl("/member/login")      // 登入表單送出處理路徑
                .defaultSuccessUrl("/index", true)        // 成功後導向首頁
                .failureHandler(failureHandler)           // 自訂登入失敗處理
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/index")
                .permitAll()
            );

        http.authenticationProvider(memberAuthenticationProvider);
        return http.build();
    }

    // Bean: 用於管理員的 SecurityFilterChain (優先順序較高，先匹配 /manager/** 路徑)
    @Bean
    @Order(1) // Manager 的規則優先
    public SecurityFilterChain managerFilterChain(HttpSecurity http,
    		AuthenticationProvider managerAuthenticationProvider) throws Exception {
        http.securityMatcher("/manager/**") // 這個規則只看「管理者專屬的路」
                .authorizeHttpRequests(auth -> auth // 定義「誰能走，誰不能走」
                        .requestMatchers("/manager/register", "/manager/login").permitAll() // 對所有人（包括未登入的訪客）開放
                        .anyRequest().hasRole("ADMIN") // 其他所有 /manager/** 頁面需要 ADMIN 角色
                ).formLogin(form -> form // 定義「怎麼登入」
                        .loginPage("/manager/login") // Manager 的登入頁面 URL
                        .loginProcessingUrl("/manager/login") // Manager 表單提交的 URL
                        .defaultSuccessUrl("/manager/home", true) // 登入成功導向的 URL
                        .failureUrl("/manager/login?error") // 登入失敗導向的 URL
                        .permitAll())
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/manager/logout")) // Manager 的登出 URL
                        .logoutSuccessUrl("/manager/login")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .csrf(csrf -> csrf.disable()); // 暫時禁用 CSRF

        http.authenticationProvider(managerAuthenticationProvider);
        return http.build();
    }

    // Bean: 會員的 AuthenticationProvider
    @Bean
    public AuthenticationProvider memberAuthenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(memberUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    // Bean: 管理員的 AuthenticationProvider
    @Bean
    public AuthenticationProvider managerAuthenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(managerUserDetailService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}