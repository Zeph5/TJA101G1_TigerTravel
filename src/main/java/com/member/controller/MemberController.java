package com.member.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.member.model.MemberService;
import com.member.model.memVO;

@Controller
@RequestMapping("/member")
public class MemberController {

	@Autowired
	private MemberService memberService;
	
	public MemberController(MemberService memberService) {
		this.memberService = memberService;
	}
	
	
	@GetMapping("/{id}")
	public String getMember(@PathVariable Integer id ,Model model) {
		memberService.findById(id).ifPresent(m ->model.addAttribute("member", m));
		return "member/detail";
	}
	
	
	@PostMapping("/register")
	public String register(@ModelAttribute memVO member,@RequestParam("avatar") MultipartFile avatarFile, Model model) {
		try {
			//若有圖片檔案，就轉成byte[] 放進Entity
			if(avatarFile != null && !avatarFile.isEmpty()) {
				member.setAvatar(avatarFile.getBytes());
			}
			
			memberService.register(member);
			model.addAttribute("message", "註冊成功！");
			return "redirect:/member/login"; //註冊成功導向登入頁
		}catch(IOException e) {
			e.printStackTrace();
			model.addAttribute("error", "圖片處理失敗");
			return "member/register";
		}
	}
}
