package com.manager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import com.manager.model.DTO.ManagerRegisterDTO;
import com.manager.service.InManagerService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/manager")
public class ManagerController {
	@Autowired
	private InManagerService inManagerService;

	// 1. 顯示註冊表單頁面 (GET 請求)
	@GetMapping("/register")
	public String registerPage(Model model) {
		model.addAttribute("managerRegisterDTO", new ManagerRegisterDTO());
		return "manager/register";
	}

	// 2. 處理註冊表單提交 (POST 請求)	
	@PostMapping("/register")
	public String register(@ModelAttribute("managerRegisterDTO") @Valid ManagerRegisterDTO managerRegisterDTO
			,BindingResult bindingResult,
			Model model,
			RedirectAttributes redirectAttributes) 
	{
		// 檢查 @Valid 產生的驗證錯誤
		if (bindingResult.hasErrors()) {			
			return "manager/register";
		}	
	try
	{
		// 呼叫 Service 執行註冊
		inManagerService.register(managerRegisterDTO);
		// 註冊成功，導向登入頁面並附帶成功訊息
		redirectAttributes.addFlashAttribute("successMessage", "註冊成功，請登入！");
		return "redirect:/manager/login"; // 註冊成功導向登入頁
	}catch(	IllegalArgumentException e)
	{
		// 處理業務邏輯錯誤（例如帳號已存在）
		model.addAttribute("error", e.getMessage());
		return "manager/register"; // 返回註冊頁面顯示錯誤
	}
}
}

