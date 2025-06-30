package com.member.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.member.security.MemberUserDetails;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/member")
public class MemberController {

	@Autowired
	private MemberService memberService;
	
	public MemberController(MemberService memberService) {
		this.memberService = memberService;
	}
	
	//會員中心
	@GetMapping("/home")
	public String memberHome(Model model, @AuthenticationPrincipal MemberUserDetails userDetails) {
		memVO member = userDetails.getMember();
		model.addAttribute("member", member);
		
		if(member.getAvatar() != null) {
			String avatarBase64 = Base64.getEncoder().encodeToString(member.getAvatar());
			model.addAttribute("avatarBase64", avatarBase64);
		}
		
		return "member/home";
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
	
	@PostMapping("/login")
	public String login(@RequestParam String memberAccount , @RequestParam String memberPassword, Model model, HttpSession session) {
		
		Optional<memVO> memberOpt = memberService.findByAccount(memberAccount);
		
		if(memberOpt.isPresent() && memberOpt.get().getMemberPassword().equals(memberPassword)) {
			session.setAttribute("loginMember", memberOpt.get());
			return "redirect:/member/" + memberOpt.get().getMemberId(); //登入後導向個人資訊頁面
		}else {
			model.addAttribute("error", "帳號或密碼錯誤");
			return "member/login";
		}
	}
	
	//導向errorpage動作
	@Controller
	public class LoginPageController {
	    @GetMapping("/login")
	    public String loginPage(@RequestParam(value = "error", required =false) String error,Model model) {
	    	if(error != null) {
	    		model.addAttribute("error","帳號或密碼錯誤，請再試一次");
	    	}
	    return "member/login"; 
	    }
	}
	
	//導向首頁動作
	@Controller
	public class HomeController{
		@GetMapping("/index")
		public String homePage(){
			return "index";
		}
	}
	
	@GetMapping("/index")
	public String indexPage() {
		return "index";
	}
	
	
	
	

	
	
}
