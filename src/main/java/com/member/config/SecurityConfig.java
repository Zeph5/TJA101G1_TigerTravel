package com.member.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests(auth -> auth 
					//requestMatchers指的是這些網站大家都可以看
					.requestMatchers("/login","/index", "/register","/login?error", "/error", "/css/**", "/js/**", "/images/**").permitAll()
	                //其他所有頁面，都需要登入才能看
					.requestMatchers("/member/**").authenticated()
	               )
			
					//登入動作
					.formLogin( form -> form
						.loginPage("/login") //頁面路徑
						.defaultSuccessUrl("/index", true)//成功登入導向home
						.permitAll()
						.failureUrl("/login?error")
					)
					
					
					//登出動作
					.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/index") //登出後導向
						.permitAll() //讓所有人(含未登入者)都能登出
					)
					.requestCache(requestCache -> requestCache.disable())
					.userDetailsService(memberDetailsService);
		
		return http.build();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		//將帳密顯示出來方便登入
		return NoOpPasswordEncoder.getInstance(); //這是明碼比對，僅限開發測試
	}

}