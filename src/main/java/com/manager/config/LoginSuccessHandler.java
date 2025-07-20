package com.manager.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.manager.model.Manager;
import com.manager.service.CustomManagerDetails;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                         Authentication authentication) throws IOException, ServletException {
        
        
        // 拿到登入者資料
        CustomManagerDetails userDetails = (CustomManagerDetails) authentication.getPrincipal();
        Manager manager = userDetails.getManager();
        
        // 存進 session
        HttpSession session = request.getSession();
        session.setAttribute("managerName", manager.getName());

        // 重導向到首頁
        response.sendRedirect(request.getContextPath() + "/manager/home");
        System.out.println("登入成功，managerName = " + manager.getName());
    }
}
