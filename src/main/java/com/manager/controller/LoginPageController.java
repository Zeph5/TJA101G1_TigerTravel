package com.manager.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.manager.service.ManagerService;

@Controller
@RequestMapping("/manager")
public class LoginPageController {	
	
	private final ManagerService managerService;
	public LoginPageController(ManagerService managerService) {
		this.managerService = managerService;
	}
	
	@GetMapping("/login") // <-- 這裡的路徑和 SecurityConfig 要對應
	public String loginPage(@RequestParam(value = "error", required = false) String error, Model model) {
		if (error != null) {
			model.addAttribute("errorMessage", "帳號或密碼錯誤，請再試一次");
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		System.out.println("目前登入帳號：" + auth.getName());
		System.out.println("目前角色：" + auth.getAuthorities());
		return "manager/login"; 
	}
	@GetMapping("/forgetPassword")
	public String forgetPasswordPage() {
		return "manager/forgetPassword"; 
	}
	@PostMapping("/forgetPassword")
	public String forgetPassword(@RequestParam("email") String email, Model model) {
		
		
		boolean emailExists = managerService.checkEmailExists(email);// 假設這裡有一個方法檢查 email 是否存在於資料庫中
		 if (emailExists) {
		        model.addAttribute("message", "重設密碼的郵件已發送到 " + email);
		        return "manager/forgetPasswordSuccess";
		    } else {
		        model.addAttribute("error", "查無此電子郵件帳號，請確認後再試");
		        return "manager/forgetPassword";
		    }			
		
	}
	
}
