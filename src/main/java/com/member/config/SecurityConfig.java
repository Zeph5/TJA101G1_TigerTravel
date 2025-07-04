package com.member.config;

  

  

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;

import org.springframework.core.annotation.Order;

import org.springframework.security.authentication.AuthenticationProvider;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.Customizer;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

  

import com.manager.security.ManagerUserDetailService;

import com.member.security.CustomAuthenticationFailureHandler;

import com.member.security.MemberUserDetailsService;

  

/*這邊的作用是：

1.保護網站的home頁面

2.自訂登入頁面

3.設定登入(成功/失敗)後要導向哪裡去

4.另外這邊使用lambda匿名寫法*/

//這邊告訴Spring 這是一個配置類，啟用Spring Security的基本設定

@Configuration

@EnableWebSecurity

public class SecurityConfig {

    @Autowired
    private MemberUserDetailsService memberDetailsService;
    
    @Autowired
    private CustomAuthenticationFailureHandler failureHandler;
    
    
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
//                .cors(cors -> cors.disable())
//                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()) // 暫時全開
//                .sessionManagement(session -> session.disable())
//                .httpBasic(Customizer.withDefaults())
//                .formLogin(Customizer.withDefaults());
//        return http.build();
//    }

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
                    "/css/**", "/js/**", "/images/**",// 靜態資源
                    "/member/verify"
                ).permitAll()

                // 🔐 其他 /member/** 路徑需登入
                .requestMatchers("/member/**").authenticated()
            )

            // 🔐 自訂登入流程
            .formLogin(form -> form
                .loginPage("/login") // 顯示登入畫面
                .loginProcessingUrl("/member/login") // 表單送出處理路徑（與 login.html 表單一致）
                .defaultSuccessUrl("/index") // 登入成功後導向
                .failureHandler(failureHandler) //使用自訂處理器
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

    @Bean
    public AuthenticationProvider authenticationProvider() {
    	DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    	authProvider.setUserDetailsService(memberDetailsService);
    	//指定會員服務
    	authProvider.setPasswordEncoder(NoOpPasswordEncoder.getInstance()); 
    	//明碼比對
    	return authProvider;
    }

private final ManagerUserDetailService managerUserDetailService; // 變數名改為 managerUserDetailService

  

// 建構子注入：Spring 會自動找到對應的 Bean 並注入

public SecurityConfig(ManagerUserDetailService managerUserDetailService, PasswordEncoder passwordEncoder) {

this.managerUserDetailService = managerUserDetailService;
}

  

@Bean

@Order(2) // 設定這個 SecurityFilterChain 的優先順序

public SecurityFilterChain filterChain(HttpSecurity http

,AuthenticationProvider memberAuthenticationProvider) throws Exception {

http.csrf(csrf -> csrf.disable()) // 停用保護 可以用get之外的請求方式

.securityMatcher("/", "/index", "/login", "/register", "/member/**", "/login?error", "/error",

"/css/**", "/js/**", "/images/**")

.authorizeHttpRequests(auth -> auth

// requestMatchers指的是這些網站大家都可以看

.requestMatchers("/login", "/index", "/register", "/login?error", "/error",

"/css/**", "/js/**", "/images/**")

.permitAll()

// 其他所有頁面，都需要登入才能看

.requestMatchers("/member/**").authenticated())

  

// 登入動作

.formLogin(form -> form.loginPage("/login") // 頁面路徑

.defaultSuccessUrl("/index", true)// 成功登入導向home

.permitAll().failureUrl("/login?error"))

  

// 登出動作

.logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/index") // 登出後導向

.permitAll() // 讓所有人(含未登入者)都能登出

);

http.authenticationProvider(memberAuthenticationProvider);

  

return http.build();

}

  

@Bean

@Order(1)

public SecurityFilterChain managerFilterChain(HttpSecurity http

,AuthenticationProvider managerAuthenticationProvider) throws Exception {

http.securityMatcher("/manager/**") // 這個規則只看「管理者專屬的路」

.authorizeHttpRequests(auth -> auth // 定義「誰能走，誰不能走」

.requestMatchers("/manager/register", "/manager/login").permitAll() // 對所有人（包括未登入的訪客）開放。允許訪問註冊和登入頁面

.anyRequest().hasRole("ADMIN") // 其他所有 /manager/** 頁面需要 ADMIN 角色 (或您設定的角色)

).formLogin(form -> form // 定義「怎麼登入」

.loginPage("/manager/login") // Manager 的登入頁面 URL

.loginProcessingUrl("/manager/login") // Manager 表單提交的 URL

.defaultSuccessUrl("/manager/home", true) // 登入成功導向的 URL

.failureUrl("/manager/login?error") // 登入失敗導向的 URL

.permitAll())

.logout(logout -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/manager/logout")) // Manager

// 的登出 URL

.logoutSuccessUrl("/manager/login").invalidateHttpSession(true).deleteCookies("JSESSIONID"))

.csrf(csrf -> csrf.disable()); // 暫時禁用 CSRF

  

// 設置 Manager 的 AuthenticationProvider

http.authenticationProvider(managerAuthenticationProvider);

  

return http.build();

}

  

@Bean

public AuthenticationProvider managerAuthenticationProvider(PasswordEncoder passwordEncoder) {

DaoAuthenticationProvider provider = new DaoAuthenticationProvider();

provider.setUserDetailsService(managerUserDetailService); // <--- 使用建構子注入的 managerUserDetailService

provider.setPasswordEncoder(passwordEncoder);

return provider;

}
}