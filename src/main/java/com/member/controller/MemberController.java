package com.member.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.member.model.MemberRepository;
import com.member.model.memVO;
import com.member.security.MemberUserDetails;
import com.member.service.MailService;
import com.member.service.MemberService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/member")
public class MemberController {

	@Autowired
	private MemberService memberService;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private MailService mailService;
	
	public MemberController(MemberService memberService) {
		this.memberService = memberService;
	}
	
	@GetMapping("/test")
	public String test() {
	    return "test";
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
	
//=============註冊註冊註冊註冊註冊註冊====================
	
	// 顯示註冊頁面
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("member", new memVO());
        return "member/register";
    }

    // 處理註冊表單送出 → 暫時寄送驗證碼 + 留在頁面上顯示連結
    @PostMapping("/register")
    public String register(@ModelAttribute memVO member,
                           @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
                           @RequestParam(value = "code", required = false) String inputCode,
                           @RequestParam("action") String action,
                           HttpSession session,
                           Model model) {
        try {
            if ("send".equals(action)) {
                String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
                session.setAttribute("verificationCode", code);

                // 頭像處理
                if (avatarFile != null && !avatarFile.isEmpty()) {
                	byte[] avatarBytes = avatarFile.getBytes();
                    member.setAvatar(avatarFile.getBytes());
                    session.setAttribute("avatarBytes", member.getAvatar());
                    
                    String base64Image = Base64.getEncoder().encodeToString(avatarBytes);
                    session.setAttribute("avatarPreview", base64Image);
                } else {
                    byte[] oldAvatar = (byte[]) session.getAttribute("avatarBytes");
                    if (oldAvatar != null) {
                        member.setAvatar(oldAvatar);
                        
                        String base64Image = Base64.getEncoder().encodeToString(oldAvatar);
                        session.setAttribute("avatarPreview", base64Image);
                    }
                }

                // 暫存 member
                session.setAttribute("tempMember", member);

                // 發送驗證碼
                mailService.sendVerificationEmail(member.getMemberEmail(), code);

                model.addAttribute("message", "驗證碼已寄出，請查收 Email！");
                model.addAttribute("member", member);
                return "member/register";

            } else if ("register".equals(action)) {
                String savedCode = (String) session.getAttribute("verificationCode");
                memVO tempMember = (memVO) session.getAttribute("tempMember");

                if (savedCode != null && savedCode.equalsIgnoreCase(inputCode)) {
                    //設定會員狀態為啟用
                    tempMember.setMemberStatus((byte) 1);

                    // 恢復圖像
                    byte[] avatarBytes = (byte[]) session.getAttribute("avatarBytes");
                    if (avatarBytes != null) {
                        tempMember.setAvatar(avatarBytes);
                    }

                    memberService.save(tempMember);
                    session.invalidate();

                    // ✅ 改這裡：註冊成功後導向 register_success.html 頁面
                    return "member/register_success";
                }else {
                    model.addAttribute("error", "驗證碼錯誤，請重新輸入！");
                    memVO retryMember = (memVO) session.getAttribute("tempMember");
                    if (retryMember != null) {
                        model.addAttribute("member", retryMember);
                    } else {
                        model.addAttribute("member", new memVO());
                    }
                    return "member/register";
                }
            }

            model.addAttribute("error", "操作錯誤，請重新操作。");
            model.addAttribute("member", member);
            return "member/register";

        } catch (IOException e) {
            model.addAttribute("error", "註冊失敗：" + e.getMessage());
            model.addAttribute("member", member);
            return "member/register";
        }
    }
    
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("avatar");
    }

    
    //顯示驗證碼輸入頁面
    @GetMapping("/member/verify")
    public String showVerifyPage() {
    	return "member/verify";
    }

    //驗證使用者輸入的驗證碼
    @PostMapping("/member/verify")
    public String handleVerify(@RequestParam("code") String inputCode,
    						HttpSession session,
    						RedirectAttributes ra) {
    	String savedCode = (String)session.getAttribute("verificationCode");
    	memVO tempMember = (memVO) session.getAttribute("tempMember");
    	
    	if(savedCode != null && savedCode.equalsIgnoreCase(inputCode)) {
    		memberService.save(tempMember);
    		session.removeAttribute("verificationCode");
    		session.removeAttribute("tempMember");
    		return "redirect:/login?verifySuccess";
    	}else {
    		ra.addFlashAttribute("error" , "驗證碼錯誤，請重新輸入");
    		return "redirect:/member/verify";
    	}
    }
    
	
//	@PostMapping("/member/register")
//	public String register(@ModelAttribute memVO member,
//						@RequestParam("avatar") MultipartFile avatarFile, Model model) {
//		try {
//			//產生驗證用 token 與設定驗證狀態
//			String token = UUID.randomUUID().toString();
//			member.setVerifyToken(token);
//			member.setEmailVerified(false);
//			
//			//若有圖片檔案，就轉成byte[] 放進Entity
//			if(avatarFile != null && !avatarFile.isEmpty()) {
//				member.setAvatar(avatarFile.getBytes());
//			}
//			
//			//儲存會員資料
//			memberService.register(member);
//			//寄送驗證信
//			mailService.sendVerificationEmail(member.getMemberEmail(), token);
//			
//			model.addAttribute("message", "註冊成功！");
//			return "redirect:/member/login"; //註冊成功導向登入頁
//		}catch(IOException e) {
//			e.printStackTrace();
//			model.addAttribute("error", "圖片處理失敗");
//			return "member/register";
//		}
//		
//	}
	
    
//====================登入登入登入登入登入登入================
//	@PostMapping("/login")
//	public String login(@RequestParam String memberAccount , @RequestParam String memberPassword, Model model, HttpSession session) {
//		
//		Optional<memVO> memberOpt = memberService.findByAccount(memberAccount);
//		
//		if(memberOpt.isPresent() && memberOpt.get().getMemberPassword().equals(memberPassword)) {
//			session.setAttribute("loginMember", memberOpt.get());
//			return "redirect:/member/" + memberOpt.get().getMemberId(); //登入後導向個人資訊頁面
//		}else {
//			model.addAttribute("error", "帳號或密碼錯誤");
//			return "member/login";
//		}
//	}
	
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
	public String indexPage(@AuthenticationPrincipal MemberUserDetails loginUser, Model model) {
	    memVO member = loginUser.getMember();
	    model.addAttribute("member", member);

	    if (member.getAvatar() != null) {
	        String avatarBase64 = Base64.getEncoder().encodeToString(member.getAvatar());
	        model.addAttribute("avatarBase64", avatarBase64);
	    }

	    return "index";
	}

	
	//進入編輯畫面
	@GetMapping("/edit")
	public String showEditForm(Model model, @AuthenticationPrincipal MemberUserDetails loginUser) {
		memVO member = loginUser.getMember();
		model.addAttribute("member", member);
		System.out.println("登入會員資料：" + loginUser.getMember().getMemberName());

		return "member/edit";
	}
	
	//接收表單修改編輯
	@PostMapping("/edit")
	public String updateMember(@ModelAttribute("member") memVO member, 
							@RequestParam("avatarFile") MultipartFile avatarFile,
							@AuthenticationPrincipal MemberUserDetails loginUser,Model model) {
		memVO original = loginUser.getMember();
		
		//只允許更新這四個欄位
		original.setMemberName(member.getMemberName());
		original.setMemberEmail(member.getMemberEmail());
		original.setMemberPhone(member.getMemberPhone());
		original.setMemberAddress(member.getMemberAddress());
		
		//若有上傳新頭像，儲存進資料庫
		try {
			if(avatarFile != null && !avatarFile.isEmpty()) {
				original.setAvatar(avatarFile.getBytes());
			}
		}catch(IOException e) {
			e.printStackTrace();
			model.addAttribute("error", "上傳圖片失敗");
			return "member/edit";
		}
		
		if(!avatarFile.getContentType().startsWith("image/")) {
			model.addAttribute("error", "上傳的不是圖片檔案");
			return "member/edit";
		}
		
		model.addAttribute("success", "會員資料更新成功～！");
		memberRepository.save(original);
		return "redirect:/index";
		
	}
	
	@GetMapping("/member/profile/{id}")
	public String getMember(@PathVariable Integer id ,Model model) {
		memberService.findById(id).ifPresent(m ->model.addAttribute("member", m));
		return "member/detail";
	}
	
}
