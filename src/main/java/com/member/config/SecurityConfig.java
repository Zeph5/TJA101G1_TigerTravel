package com.member.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import com.manager.config.LoginSuccessHandler;
import com.manager.service.CustomManagerDetailsService;
import com.member.security.CustomAuthenticationFailureHandler;
import com.member.security.MemberUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final MemberUserDetailsService memberUserDetailsService;
    private final CustomManagerDetailsService managerUserDetailService;
    private final CustomAuthenticationFailureHandler failureHandler;
    private final LoginSuccessHandler loginSuccessHandler;

    public SecurityConfig(MemberUserDetailsService memberUserDetailsService,
                         CustomManagerDetailsService managerUserDetailService,
                         CustomAuthenticationFailureHandler failureHandler,
                         LoginSuccessHandler loginSuccessHandler) {
        this.memberUserDetailsService = memberUserDetailsService;
        this.managerUserDetailService = managerUserDetailService;        
        this.failureHandler = failureHandler;
        this.loginSuccessHandler = loginSuccessHandler;
    }
    
    @Bean
    @Order(1) // Manager security chain - highest priority
    public SecurityFilterChain managerFilterChain(HttpSecurity http,
                                                  AuthenticationProvider managerAuthenticationProvider) throws Exception {
        http.securityMatcher("/manager/**")
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/manager/register", "/manager/login", "/manager/forgetPassword", "/manager/forgetPasswordSuccess").permitAll()
                .anyRequest().hasRole("ADMIN")
            )
            .formLogin(form -> form
                .loginPage("/manager/login")
                .loginProcessingUrl("/manager/login")
                .successHandler(loginSuccessHandler)
                .failureUrl("/manager/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/manager/logout"))
                .logoutSuccessUrl("/manager/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            )
            // Enable CSRF for manager with proper configuration
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringRequestMatchers("/manager/api/**") // if you have API endpoints
            );

        http.authenticationProvider(managerAuthenticationProvider);
        return http.build();
    }

    @Bean
    @Order(2) // Admin security chain
    public SecurityFilterChain adminFilterChain(HttpSecurity http,
                                               AuthenticationProvider managerAuthenticationProvider) throws Exception {
        http.securityMatcher("/admin/**")
            .authorizeHttpRequests(auth -> auth
                .anyRequest().hasRole("ADMIN")
            )
            .formLogin(form -> form
                .loginPage("/manager/login")
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
            // Enable CSRF for admin
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            );

        http.authenticationProvider(managerAuthenticationProvider);
        return http.build();
    }
    
    @Bean
    @Order(3) // Member and public security chain - lowest priority
    public SecurityFilterChain memberFilterChain(HttpSecurity http,
                                                AuthenticationProvider memberAuthenticationProvider) throws Exception {
        
        http.securityMatcher("/**")
            // Enable CSRF protection with proper configuration
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringRequestMatchers(
                    // Disable CSRF for specific API endpoints and delete operations
                    "/api/**",
                    "/sceneryscore/delete/**"
                )
            )
            
            .authorizeHttpRequests(auth -> auth

                // ✅ 靜態資源 & 基本公開頁面
                .requestMatchers("/css/**", "/js/**", "/images/**", "/homepage_images/**", "/logo_image/**","/favicon.ico", "/robots.txt", "/sitemap.xml").permitAll()
                .requestMatchers("/uploads/**").permitAll()
                .requestMatchers("/", "/index", "/login", "/register", "/error", "/login?error").permitAll()
                .requestMatchers("/ticketlist").permitAll()

                // ✅ 景點搜尋功能（開放給所有人）
                .requestMatchers("/search", "/scenery/search").permitAll()
                
                // Public pages
                .requestMatchers(
                    "/", "/index", "/login", "/register", 
                    "/error", "/login?error", "/ticketlist"
                ).permitAll()
                
                // ✅ NEW: Scenery Score Management (Admin only)
                .requestMatchers(
                    "/sceneryscore/**", 
                    "/sceneryscore/findAll", 
                    "/sceneryscore/search",
                    "/sceneryscore/delete/**",
                    "/sceneryscore/diagnostic",
                    "/sceneryscore/json",
                    "/sceneryscore/test",
                    "/sceneryscore/security-test",
                    "/sceneryscore/public-test"
                ).hasRole("ADMIN")
                
                // Search functionality (public) - Allow both GET and POST
                .requestMatchers(HttpMethod.GET, "/search", "/scenery/search").permitAll()
                .requestMatchers(HttpMethod.POST, "/search", "/scenery/search").permitAll()
                
                // Frontend scenery pages (public read access)
                .requestMatchers(HttpMethod.GET, "/frontend/**").permitAll()
                
                // Travel detail pages (public read access via IndexController)
                .requestMatchers(HttpMethod.GET, "/travel/detail/**").permitAll()
                
                // Scenery resources (images, banners)
                .requestMatchers(
                    "/scenery/banner/**", 
                    "/scenery/image/**"
                ).permitAll()
                
                // Password reset and registration (public)
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
                
                // Password reset POST (special case)
                .requestMatchers(HttpMethod.POST, "/member/resetPassword").permitAll()
                
                // Scenery interactions (require authentication)
                .requestMatchers(
                    HttpMethod.POST, 
                    "/frontend/scenery/detail/*/add-comment",
                    "/frontend/scenery/detail/*/favorite/add",
                    "/frontend/scenery/detail/*/favorite/remove",
                    "/scenery/favorite/add/*",
                    "/scenery/favorite/remove/*"
                ).authenticated()
                
                // Manager-only functions
                .requestMatchers("/member/list").hasRole("MANAGER")
                
                // Admin-only scenery management
                .requestMatchers(
                    "/scenery/listallscenery", "/scenery/addscenery", 
                    "/scenery/updatescenery/**", "/scenery/index", 
                    "/scenery/deleteimage/**", "/scenery/updatestatus",
                    "/scenery/{id}", "/scenery/{sceneryId}/addimage",
                    "/scenery/sceneryindex"
                ).hasRole("ADMIN")
                
                // Admin-only tag management (updated to include tagsdb)
                .requestMatchers(
                    "/tags/**", "/tags/addtags", "/tags/listalltags", 
                    "/tags/updatetag/**", "/tags/addtagsdb", 
                    "/tags/updatetagsdb", "/tags/searchresult",
                    "/tags/list",
                    "/tagsdb/**", "/tagsdb/listall"
                ).hasRole("ADMIN")
                
                // Member-only functions (require authentication)
                .requestMatchers(
                    "/member/edit", "/member/home", "/member/favorites", "/member/favorites/**",
                    "/member/receipt/**", "/member/ticketOrders", "/member/ticket/orders",
                    "/member/order/**", "/member/ticketOrderDetail", "/member/detail/**",
                    "/member/orders", "/member/travel/orders", "/member/tour-order/detail/**",
                    "/member/travel/**", // This covers both /member/travel/detail/{id} and /member/travel-plans
                    "/tour-order/create", "/member/tour-order/create",
                    "/member/travel-plans", // Allow travel plans list with search (authenticated users)
                    "/member/simulate-payment", "/member/simulate-payment/**",
                    "/member/payment-success", "/member/password/change", "/simulate-payment",
                    "/member/member-tour-order-form", "/member/favorites/add/**",
                    "/favorites/tour/add/**", "/favorites/tour/remove/**", 
                    "/favorites/scenery/remove/**"
                ).authenticated()
                
                // Ticket functions (require authentication)
                .requestMatchers("/ticket/**", "/ticketOrders").authenticated()
                
                // Catch-all for member routes before general permit
                .requestMatchers("/member/**").authenticated()
                
                // Allow all other requests (be careful with this - consider making it more restrictive)
                .anyRequest().permitAll()
            )
            
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/member/login")
                .defaultSuccessUrl("/index", true)
                .failureHandler(failureHandler)
                .permitAll()
            )
            
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/index")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            
            // Session management
            .sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            );

        http.authenticationProvider(memberAuthenticationProvider);
        return http.build();
    }

    @Bean
    public AuthenticationProvider memberAuthenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(memberUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationProvider managerAuthenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(managerUserDetailService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }
}