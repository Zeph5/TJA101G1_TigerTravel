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
    		AuthenticationProvider memberAuthenticationProvider) throws Exception { // 改名以避免與 Manager 的 filterChain 混淆
        http
        	.csrf(csrf -> csrf.disable()) // 停用保護
        	.securityMatcher("/", "/index", "/login", "/register", 
                    "/member/**", "/ticket/**", "/scenery/**", "/logout",
                    "/login?error", "/error", "/css/**", "/js/**", "/images/**", "/member/verify")
        	.authorizeHttpRequests(auth -> auth
        				.requestMatchers("/login", "/index", "/member/register", "/login?error", "/error","/register",
                        "/css/**", "/js/**", "/images/**", "/member/verify","/scenery/**") // 允許匿名訪問
                        .permitAll()
                        .requestMatchers("/scenery/**").permitAll()  // ✅ 加這行來開放瀏覽
                        .requestMatchers("/ticket/**").authenticated()  // 範例：登入才看票券
                        .requestMatchers("/member/list").hasRole("MANAGER") // 確保 Manager 角色才能訪問這個路徑
                        .requestMatchers("/member/**").authenticated() // 其他所有 /member/** 路徑需登入
                )
                // 登入動作
                .formLogin(form -> form
                        .loginPage("/login") // 頁面路徑
                        .loginProcessingUrl("/member/login") // 會員表單提交的 URL
                        .defaultSuccessUrl("/index", true) // 成功登入導向 home
                        .failureHandler(failureHandler) // 使用自訂的失敗處理器
                        .permitAll()
                )
                // 登出動作
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/index") // 登出後導向
                        .permitAll() // 讓所有人(含未登入者)都能登出
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