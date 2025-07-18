package com.manager.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginPageController {	
	@GetMapping("manager/login") // <-- 這裡的路徑和 SecurityConfig 要對應
	public String loginPage(@RequestParam(value = "error", required = false) String error, Model model) {
		if (error != null) {
			model.addAttribute("errorMessage", "帳號或密碼錯誤，請再試一次");
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println("目前登入帳號：" + auth.getName());
		System.out.println("目前角色：" + auth.getAuthorities());
		return "manager/login"; 
	}
}
