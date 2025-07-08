package com.member.controller;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
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
import com.scenery.model.SceneryService;
import com.scenery.model.SceneryVO;
import com.ticket.model.Ticket;
import com.ticket.repository.TicketRepository;
import com.ticket.service.TicketService;

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
	@Autowired
	private SceneryService sceneryService;
	@Autowired
	private TicketRepository ticketRepository;
	
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
	
//=============註冊註冊註冊註冊註冊註冊====================
	
	// 顯示註冊頁面
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("member", new memVO());
        return "member/register";
    }

    // 處理註冊流程
    @PostMapping("/register")
    public String processRegister(@ModelAttribute memVO member,
                                   @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
                                   @RequestParam(value = "code", required = false) String inputCode,
                                   @RequestParam("action") String action,
                                   HttpSession session,
                                   Model model) {
        try {
//            if ("send".equals(action)) {
//                // 產生驗證碼
//                String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
//                session.setAttribute("verificationCode", code);
//
//                // 頭像處理（預覽 + 暫存）
//                if (avatarFile != null && !avatarFile.isEmpty()) {
//                    byte[] avatarBytes = avatarFile.getBytes();
//                    member.setAvatar(avatarBytes);
//
//                    session.setAttribute("avatarBytes", avatarBytes);
//                    session.setAttribute("avatarPreview", Base64.getEncoder().encodeToString(avatarBytes));
//                } else {
//                    byte[] oldAvatar = (byte[]) session.getAttribute("avatarBytes");
//                    if (oldAvatar != null) {
//                        member.setAvatar(oldAvatar);
//                        session.setAttribute("avatarPreview", Base64.getEncoder().encodeToString(oldAvatar));
//                    }
//                }
//
//                // 暫存使用者資訊
//                session.setAttribute("tempMember", member);
//
//                // ✅ 防止寄信錯誤導致 response commit
//                try {
//                    mailService.sendVerificationEmail(member.getMemberEmail(), code);
//                    model.addAttribute("message", "驗證碼已寄出，請查收 Email！");
//                } catch (Exception e) {
//                    model.addAttribute("error", "寄送驗證碼失敗：" + e.getMessage());
//                }
//
//                model.addAttribute("member", member);
//                return "member/register";
//            }

            // 確認註冊按鈕：驗證碼比對
            if ("register".equals(action)) {
//                memVO tempMember = (memVO) session.getAttribute("tempMember");
                
                	//檢查帳號是否已存在
                	Optional<memVO> existing = memberService.findByAccount(inputCode);
                	if(existing.isPresent()) {
                		model.addAttribute("error", "此帳號已有人使用，請更換帳號");
                		model.addAttribute("member", member);
                		return "member/register";
                	}
                	
                	//圖片處理：轉成byte[] 存進member
                	if (!avatarFile.isEmpty()) {
                	    member.setAvatar(avatarFile.getBytes());
                	}
                	
                	// 建立驗證用token
                	String token = UUID.randomUUID().toString();
                	member.setVerifyToken(token);
                	member.setEmailVerified(false); //可用Boolen / Byte
                    member.setMemberStatus((byte) 0); //尚未啟用

                    // 還原頭像（保險起見）
                    byte[] avatarBytes = (byte[]) session.getAttribute("avatarBytes");
                    if (avatarBytes != null) {
                        member.setAvatar(avatarBytes);
                    }
                    //儲存會員(未啟用)
                    memberService.save(member);
                  
                    //寄出驗證信
                    String verifyUrl = "http://localhost:8080/member/verify?token=" + token;
                    mailService.sendVerificationLink(member.getMemberEmail(), verifyUrl);
                    
                    session.invalidate(); // 註冊完成清除 session

                    //顯示通知畫面
                    return "member/register_result";
            }

            // 未知操作
            model.addAttribute("error", "操作錯誤，請重新操作。");
            model.addAttribute("member", member);
            return "member/register";

        } 
//        catch (IOException e) {
//            model.addAttribute("error", "頭像處理失敗：" + e.getMessage());
//            model.addAttribute("member", member);
//            return "member/register";
//        } 
        catch (Exception e) {
            model.addAttribute("error", "註冊發生錯誤：" + e.getMessage());
            model.addAttribute("member", member);
            return "member/register";
        }
    }
    
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("avatar");
    }

    
    //顯示驗證碼輸入頁面
    @GetMapping("/verify")
    public String verifyEmail(@RequestParam("token") String token, Model model) {
        Optional<memVO> optionalMember = memberService.findByToken(token);

        if (optionalMember.isPresent()) {
            memVO member = optionalMember.get();

            if (!Boolean.TRUE.equals(member.getEmailVerified())) {
                member.setEmailVerified(true);
                member.setMemberStatus((byte) 1);
                member.setVerifyToken(null);
                memberService.save(member);
            }

            model.addAttribute("message", "驗證成功！請重新登入系統");
            return "member/verify/verify_success";
        } else {
            model.addAttribute("error", "連結無效或已過期");
            return "member/verify/verify_fail";
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
//	@Controller
//	public class HomeController{
//		@GetMapping("/index")
//		public String homePage(){
//			return "index";
//		}
//	}
	
//	@GetMapping("/index")
//	public String indexPage(@AuthenticationPrincipal MemberUserDetails loginUser, Model model) {
//	    memVO member = loginUser.getMember();
//	    model.addAttribute("member", member);
//
//	    if (member.getAvatar() != null) {
//	        String avatarBase64 = Base64.getEncoder().encodeToString(member.getAvatar());
//	        model.addAttribute("avatarBase64", avatarBase64);
//	    }
//	    
//	    List<SceneryVO> sceneries = sceneryService.findAllScenery(); //
//	    model.addAttribute("sceneries", sceneries);
//	    
//	    return "index";
//	}

	
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
	
	@GetMapping("/member/home")
	public String showMemberHome(Model model) {
		List<SceneryVO> allScenery = sceneryService.findAllScenery();
		
		List<SceneryVO> topScenery = allScenery.stream().limit(6).toList();
		
		model.addAttribute("sceneryLisy", topScenery);
		return "member/index";
	}
	
	//撈scenery 和 ticket的資訊放在index
//	@GetMapping({"/", "/index"})
//	public String showIndex(Model model) {
//	    List<SceneryVO> allScenery = sceneryService.findAllScenery(); // 或取熱門的前幾筆
//	    List<SceneryVO> top6Scenery = allScenery.stream().limit(6).toList();
//	    model.addAttribute("sceneries", top6Scenery);
//	    
//	    List<Ticket> ticketList = ticketRepository.findAll();
//	    model.addAttribute("ticketList", ticketList);
//	    
//	    System.out.println("票券數量：" + ticketList.size());
//	    System.out.println("景點數量：" + allScenery.size());
//	    System.out.println("✅ 成功進入 showIndex()");
//	    model.addAttribute("test", "Hello Index");
//	    return "index"; // 對應 templates/index.html
//	}
	
}
