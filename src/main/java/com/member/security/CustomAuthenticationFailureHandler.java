package com.member.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            org.springframework.security.core.AuthenticationException exception)
            throws IOException, ServletException {
    	System.out.println("🔥 [FailureHandler] 登入失敗類型：" + exception.getClass().getSimpleName());

        if (exception instanceof DisabledException) {
            // 帳號被停權（來自 isEnabled() 回傳 false）
            response.sendRedirect("/login?error=disabled");
        } else if (exception instanceof BadCredentialsException) {
            // 帳號或密碼錯誤
            response.sendRedirect("/login?error=badCredentials");
        } else {
            // 其他錯誤
            response.sendRedirect("/login?error=unknown");
        }
    }
}