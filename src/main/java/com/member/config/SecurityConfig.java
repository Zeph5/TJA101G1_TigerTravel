package com.member.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import org.springframework.http.HttpMethod;

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

    @Bean
    @Order(2) // 放在 manager/member 後面處理
    public SecurityFilterChain adminFilterChain(HttpSecurity http,
                                                AuthenticationProvider managerAuthenticationProvider) throws Exception {
        http
            .securityMatcher("/admin/**") // 只處理 /admin/** 開頭的路徑
            .authorizeHttpRequests(auth -> auth
                .anyRequest().hasRole("ADMIN") // 必須具備 ADMIN 角色
            )
            .formLogin(form -> form
                .loginPage("/manager/login") // 沿用 manager 登入頁
                .loginProcessingUrl("/manager/login")
                .defaultSuccessUrl("/admin", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/admin/logout"))
                .logoutSuccessUrl("/index")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            )
            .csrf(csrf -> csrf.disable());

        http.authenticationProvider(managerAuthenticationProvider);
        return http.build();
    }
    
    // Bean: 用於會員的 SecurityFilterChain (優先順序較低，排在後面處理)
    @Bean
    @Order(3)
    public SecurityFilterChain memberFilterChain(HttpSecurity http,
            AuthenticationProvider memberAuthenticationProvider) throws Exception {
    	
    	http.securityMatcher("/**")
            // ❗ 關閉 CSRF（如果沒有表單驗證需求）
            .csrf(csrf -> csrf.disable())

            // ✅ 授權路徑規則
            .authorizeHttpRequests(auth -> auth

                // ✅ 靜態資源 & 基本公開頁面
                .requestMatchers("/css/**", "/js/**", "/images/**", "/homepage_images/**", "/logo_image/**").permitAll()
                .requestMatchers("/", "/index", "/login", "/register", "/error", "/login?error").permitAll()
                .requestMatchers("/ticketlist").permitAll()

                // ✅ 景點搜尋功能（開放給所有人）
                .requestMatchers("/search", "/scenery/search").permitAll()
                
                // ✅ 前台景點頁面（開放給所有人瀏覽）- 對應 templates/frontend/scenery/
                .requestMatchers(HttpMethod.GET, "/frontend/**").permitAll()
                
                // ✅ 景點評論功能（需要登入）- 對應 templates/frontend/scenery/Scenery.html
                .requestMatchers(HttpMethod.POST, "/frontend/scenery/detail/*/add-comment").authenticated()

                // ✅ 景點相關資源（圖片等）
                .requestMatchers("/scenery/banner/**", "/scenery/image/**").permitAll()

                // ✅ 靜態資源（包含 favicon）
                .requestMatchers("/favicon.ico").permitAll()

                // ✅ 忘記密碼與註冊流程（開放）
                .requestMatchers(
                    "/member/register", "/member/verify",
                    "/member/forgot", "/member/forgotPassword",
                    "/member/reset", "/member/resetPassword",
                    "/member/reset-password", 
                    "/member/password/forgotPassword",
                    "/member/password/resetPassword",
                    "/member/password/reset-password-success",
                    "/member/password/reset-password-fail"
                ).permitAll()

                // ✅ 密碼重設畫面（特殊開放）
                .requestMatchers(HttpMethod.POST, "/member/resetPassword").permitAll()

                // ✅ 管理員功能
                .requestMatchers("/member/list").hasRole("MANAGER")

                // ✅ 一般會員功能（需要登入）
                .requestMatchers(
                    "/member/edit", "/member/home", "/member/favorites",
                    "/member/receipt/**",
                    "/member/ticketOrders", "/member/ticket/orders",
                    "/member/order/**", "/member/ticketOrderDetail",
                    "/member/detail/**",
                    "/member/orders", // ✅ ⬅️ 加這行，允許登入會員看訂單整合頁
                    "/member/travel/orders", // 若有這路徑建議也加
                    "/member/tour-order/detail/**"
                ).authenticated()

                // ✅ 票券功能（也要登入）
                .requestMatchers("/ticket/**", "/ticketOrders").authenticated()

                // ✅ 景點後台管理功能（需要管理員權限）- 對應 templates/scenery/
                .requestMatchers(
                    "/scenery/listallscenery", "/scenery/addscenery", "/scenery/updatescenery/**",
                    "/scenery/index", "/scenery/deleteimage/**", "/scenery/updatestatus",
                    "/scenery/{id}", "/scenery/{sceneryId}/addimage"
                ).hasRole("ADMIN")

                // ✅ 標籤後台管理功能（需要管理員權限）- 對應 templates/tags/
                .requestMatchers(
                    "/tags/**", // 所有標籤管理功能
                    "/tags/addtags", "/tags/listalltags", "/tags/updatetag/**",
                    "/tags/addtagsdb", "/tags/updatetagsdb", "/tags/searchresult"
                ).hasRole("ADMIN")

                // ❗ 最後兜底規則（不建議用 denyAll 除非你真的想封鎖）
                // .requestMatchers("/member/**").denyAll()
            )

            // ✅ 登入設定
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/member/login")
                .defaultSuccessUrl("/index", true)
                .failureHandler(failureHandler)
                .permitAll()
            )

            // ✅ 登出設定
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/index")
                .permitAll()
            );

        // ✅ 加入自定義會員登入機制
        http.authenticationProvider(memberAuthenticationProvider);

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

    // Note: PasswordEncoder bean is already defined in PasswordEncoderConfig.class
}