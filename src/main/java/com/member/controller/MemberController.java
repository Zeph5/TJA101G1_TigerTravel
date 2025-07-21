package com.member.controller;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.member.model.FavoriteTravelPlan;
import com.member.model.FavoriteTravelPlanRepository;
import com.member.model.MemberRepository;
import com.member.model.memVO;
import com.member.security.MemberUserDetails;
import com.member.service.FavoriteSceneryService;
import com.member.service.MailService;
import com.member.service.MemberService;
import com.member.service.favorite.FavoriteTravelPlanService;
import com.scenery.model.SceneryService;
import com.scenery.model.SceneryVO;
import com.travel_plan.model.TravelItinerary;
import com.travel_plan.model.TravelPlan;
import com.travel_plan.service.TravelItineraryService;
import com.travel_plan.service.TravelPlanDayService;
import com.travel_plan.service.TravelPlanService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private TravelPlanDayService travelPlanDayService;

	private final PasswordEncoder passwordEncoder;

	private final FavoriteTravelPlanService favoriteTravelPlanSvc;

	private final FavoriteSceneryService favortieScenerySvc;
	
	private final TravelItineraryService travelItinerarySvc;
	
	private final TravelPlanService travelPlanSvc;

	private static final Logger log = LoggerFactory.getLogger(MemberController.class);

	public MemberController(MemberService memberService, PasswordEncoder passwordEncoder,
			FavoriteTravelPlanService favoriteTravelPlanSvc, FavoriteSceneryService favortieScenerySvc,
			TravelItineraryService travelItinerarySvc,TravelPlanService travelPlanSvc) {
		this.memberService = memberService;
		this.passwordEncoder = passwordEncoder;
		this.favoriteTravelPlanSvc = favoriteTravelPlanSvc;
		this.favortieScenerySvc = favortieScenerySvc;
		this.travelItinerarySvc = travelItinerarySvc;
		this.travelPlanSvc = travelPlanSvc;
	}

	// 會員中心
	@GetMapping("/home")
	public String memberHome(Model model, @AuthenticationPrincipal MemberUserDetails userDetails) {
		memVO member = userDetails.getMember();
		model.addAttribute("member", member);

		if (member.getAvatar() != null) {
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

	// =============註冊註冊註冊註冊註冊註冊====================
	// 處理註冊流程
	@PostMapping("/register")
	public String processRegister(@Valid @ModelAttribute("member") memVO member,
	                              BindingResult result,
	                              @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
	                              @RequestParam("action") String action,
	                              HttpSession session, Model model) {

	    if (result.hasErrors()) {
	        model.addAttribute("member", member);
	        return "member/register";
	    }

	    try {
	        if ("register".equals(action)) {

	            // 檢查帳號是否已存在
	            Optional<memVO> existing = memberService.findByAccount(member.getMemberAccount());
	            if (existing.isPresent()) {
	                model.addAttribute("error", "此帳號已有人使用，請更換帳號");
	                model.addAttribute("member", member);
	                return "member/register";
	            }

	            // 處理頭像
	            if (avatarFile != null && !avatarFile.isEmpty()) {
	                member.setAvatar(avatarFile.getBytes());
	            }

	            byte[] avatarBytes = (byte[]) session.getAttribute("avatarBytes");
	            if (avatarBytes != null) {
	                member.setAvatar(avatarBytes);
	            }

	            // 設定初始狀態與驗證資訊
	            member.setEmailVerified(false);
	            member.setMemberStatus((byte) 0);
	            member.setVerifyToken(UUID.randomUUID().toString());
	            member.setVerifyTokenCreatedTime(LocalDateTime.now());
	            memberService.save(member);

	            // 寄出驗證信
	            memberService.sendVerificationalEmail(member);

	            // 準備畫面資料
	            model.addAttribute("member", member);
	            model.addAttribute("secondsLeft", 900L); // 初始 15 分鐘
	            session.invalidate();

	            return "member/register_result";
	        }

	        model.addAttribute("error", "操作錯誤，請重新操作。");
	        model.addAttribute("member", member);
	        return "member/register";

	    } catch (Exception e) {
	        model.addAttribute("error", "註冊發生錯誤：" + e.getMessage());
	        model.addAttribute("member", member);
	        return "member/register";
	    }
	}

	@GetMapping("/resend-verification")
	public String resendVerification(@RequestParam("email") String email, Model model) {
	    Optional<memVO> optionalMember = memberService.findByEmail(email);

	    if (optionalMember.isEmpty()) {
	        model.addAttribute("error", "找不到此信箱的帳號");
	        return "member/register";
	    }

	    memVO member = optionalMember.get();

	    if (member.isEmailVerified()) {
	        model.addAttribute("message", "您的帳號已完成驗證，請直接登入。");
	        return "member/login";
	    }

	    // 檢查是否已超過 15 分鐘
	    Duration duration = Duration.between(member.getVerifyTokenCreatedTime(), LocalDateTime.now());
	    long secondsLeft = 900 - duration.getSeconds(); // 15分鐘 = 900秒

	    if (secondsLeft > 0) {
	        model.addAttribute("error", "請等待倒數結束後再重新發送驗證信");
	    } else {
	        // 重發驗證信並更新時間與 token
	        member.setVerifyToken(UUID.randomUUID().toString());
	        member.setVerifyTokenCreatedTime(LocalDateTime.now());
	        memberService.save(member);
	        memberService.sendVerificationalEmail(member);
	        model.addAttribute("message", "驗證信已重新寄出，請前往信箱查看。");
	        secondsLeft = 900;
	    }
	    
	    System.out.println("token 建立時間: " + member.getVerifyTokenCreatedTime());
	    System.out.println("現在時間: " + LocalDateTime.now());
	    System.out.println("相差分鐘: " + duration.toMinutes());

	    model.addAttribute("member", member);
	    model.addAttribute("secondsLeft", Math.max(secondsLeft, 0));

	    return "member/register_result";
	}



	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.setDisallowedFields("avatar");
	}

	// 顯示驗證碼輸入頁面
	@Transactional
	@GetMapping("/verify")
	public String verifyEmail(@RequestParam("account") String account,
	                          @RequestParam("token") String token,
	                          Model model) {
	    boolean isValid = memberService.checkVerifyTokenFromRedis(account, token);
	    if (!isValid) {
	        model.addAttribute("error", "驗證碼錯誤或已過期，請重新發送驗證信");
	        return "member/verify/verify_fail";
	    }

	    Optional<memVO> opt = memberService.findByAccount(account);
	    if (opt.isEmpty()) {
	        model.addAttribute("error", "找不到會員帳號");
	        return "member/verify/verify_fail";
	    }

	    memVO member = opt.get();
	    if (member.isEmailVerified()) {
	        model.addAttribute("message", "帳號已啟用，請直接登入");
	        return "redirect:/login";
	    }

	    // ✅ 設定啟用狀態
	    member.setEmailVerified(true);
	    member.setMemberStatus((byte) 1);
	    memberService.save(member); // 這會觸發 EntityManager.flush()

	    return "redirect:/login?registerSuccess";
	}


	// 導向errorpage動作
	@Controller
	public class LoginPageController {
		@GetMapping("/login")
		public String loginPage(@RequestParam(value = "error", required = false) String error, Model model) {
			if (error != null) {
				model.addAttribute("error", "帳號或密碼錯誤，請再試一次");
			}
			return "member/login";
		}
	}

	//===============會員登入後可使用的功能=======================
	// 進入編輯畫面
	@GetMapping("/edit")
	public String showEditForm(Model model, @AuthenticationPrincipal MemberUserDetails loginUser) {
		memVO member = loginUser.getMember();
		model.addAttribute("member", member);
		System.out.println("登入會員資料：" + loginUser.getMember().getMemberName());

		if (member.getAvatar() != null) {
			String avatarBase64 = Base64.getEncoder().encodeToString(member.getAvatar());
			model.addAttribute("avatarPreview", avatarBase64); // ✅ 新增預覽圖用
		}

		return "member/edit";
	}

	// 接收表單修改編輯
	@PostMapping("/edit")
	public String updateMember(@ModelAttribute("member") memVO member, 
								BindingResult bindingResult,
								@RequestParam("avatarFile") MultipartFile avatarFile, @AuthenticationPrincipal MemberUserDetails loginUser,
			RedirectAttributes redirectAttributes) {
		System.out.println("✅ 進入 updateMember 方法");

		if (bindingResult.hasErrors()) {
			System.out.println("❌ 有驗證錯誤：" + bindingResult.getAllErrors());
			return "member/edit";
		}

		memVO original = loginUser.getMember();

		// 只允許更新這四個欄位
		original.setMemberName(member.getMemberName());
		original.setMemberEmail(member.getMemberEmail());
		original.setMemberPhone(member.getMemberPhone());
		original.setMemberAddress(member.getMemberAddress());

		// 頭像優化：加大小/格式檢查
		try {
			if (avatarFile != null && !avatarFile.isEmpty()) {
				if (!avatarFile.getContentType().startsWith("image/")) {
					redirectAttributes.addFlashAttribute("error", "上傳的不是圖片檔案");
					return "redirect:/member/edit";
				}
				if (avatarFile.getSize() > 2 * 1024 * 1024) { // 限制2MB
					redirectAttributes.addFlashAttribute("error", "圖片大小超過2MB");
					return "redirect:/member/edit";
				}
				original.setAvatar(avatarFile.getBytes());
			}
		} catch (IOException e) {
			log.error("頭像上傳IO錯誤", e);
			redirectAttributes.addFlashAttribute("error", "上傳圖片失敗，請重試");
			return "redirect:/member/edit";
		} catch (Exception e) {
			log.error("頭像上傳未知錯誤", e);
			redirectAttributes.addFlashAttribute("error", "系統錯誤，請聯絡管理員");
			return "redirect:/member/edit";
		}

		memberRepository.save(original);
		redirectAttributes.addFlashAttribute("success", "會員資料更新成功～！");
		return "redirect:/member/home"; // redirect到會員中心，顯示成功訊息
	}
	
	

	@GetMapping("/member/profile/{id}")
	public String getMember(@PathVariable Integer id, Model model) {
		memberService.findById(id).ifPresent(m -> model.addAttribute("member", m));
		return "member/detail";
	}

	@GetMapping("/member/home")
	public String showMemberHome(Model model) {
		List<SceneryVO> allScenery = travelPlanDayService.findAllScenery();

		List<SceneryVO> topScenery = allScenery.stream().limit(6).toList();

		model.addAttribute("sceneryLisy", topScenery);
		return "member/index";
	}

	// ✅ 用來遮罩帳號顯示：保留前 3～4 碼，後面用 **** 取代
	private String mask(String account) { // 帳號遮罩 給resetPassword使用
		return account.length() > 4 ? account.substring(0, 4) + "****" : account.substring(0, 2) + "****";
	}

	// Step 1: 顯示忘記密碼頁面
	@GetMapping("/forgotPassword")
	public String showForgotPasswordForm() {
		return "member/password/forgotPassword"; // 對應到 forgot.html
	}

	// Step 3: 顯示 reset 密碼頁面
	@GetMapping("/reset-password")
	public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
		System.out.println("[DEBUG] /reset-password GET with token: " + token);
		Optional<memVO> member = memberService.findByResetToken(token);

		if (member.isPresent()) {
			memVO mem = member.get();
			model.addAttribute("token", token);

			// 遮罩帳號顯示
			String maskedAccount = mask(mem.getMemberAccount());
			model.addAttribute("maskedAccount", maskedAccount);

			return "member/password/resetPassword";
		} else {
			model.addAttribute("error", "連結無效或已過期");
			return "member/password/resetPassword";
		}
	}

	// Step 4: 提交新密碼
	@PostMapping("/resetPassword")
	public String processResetPassword(@RequestParam("token") String token,
			@RequestParam("newPassword") String newPassword, RedirectAttributes redirectAttributes) {

		Optional<memVO> member = memberService.findByResetToken(token);

		if (member.isPresent()) {
			memberService.resetPassword(member.get(), newPassword);
			redirectAttributes.addFlashAttribute("msg", "密碼已重設，請重新登入！");
			return "redirect:/member/reset-password-success"; // ✅ 要用 redirect!
		} else {
			redirectAttributes.addFlashAttribute("error", "Token 無效或已過期！");
			return "redirect:/member/reset-password-fail"; // ✅ 同樣使用 redirect
		}
	}

	// VV 顯示成功葉面(供SweetAlert 彈窗後導轉)
	@GetMapping("/reset-password-success")
	public String resetPasswordSuccess(Model model, @ModelAttribute("msg") String msg) {
		System.out.println("✅ 成功進入 reset-password-success controller");

		// 檢查 msg 有沒有帶進來
		System.out.println("🧪 成功訊息 msg = " + msg);

		model.addAttribute("msg", msg); // 加入 model，給 Thymeleaf 用

		return "member/password/reset-password-success";
	}

	// XX 顯示失敗葉面(供 SweetAlert 彈窗顯示錯誤)
	@GetMapping("/reset-password-fail")
	public String resetPasswordFail() {
		return "member/password/reset-password-fail";
	}

	// 導向變更密碼的畫面(已登入)
	@GetMapping("/password/change")
	public String showChangePasswordForm() {
		return "member/password/changePassword";
	}

	// 將更改密碼的資料送出(已登入)
	@PostMapping("/password/change")
	public String processChangePassword(@RequestParam("currentPassword") String currentPassword,
			@RequestParam("newPassword") String newPassword, @RequestParam("confirmPassword") String confirmPassword,
			@AuthenticationPrincipal MemberUserDetails loginUser, Model model) {

		memVO member = loginUser.getMember();

		// 1. 檢查新密碼與確認密碼是否一致
		if (!newPassword.equals(confirmPassword)) {
			model.addAttribute("error", "新密碼與確認密碼不一致！");
			return "member/password/changePassword";
		}

		// 2. 檢查舊密碼是否正確（你需根據你的密碼加密邏輯來比對）
		if (!passwordEncoder.matches(currentPassword, member.getMemberPassword())) {
			model.addAttribute("error", "目前密碼錯誤！");
			return "member/password/changePassword";
		}

		// 3. 修改密碼
//        member.setMemberPassword(passwordEncoder.encode(newPassword)); //加密
		member.setMemberPassword(newPassword); // 明碼測試 記得上限要刪掉
		memberService.save(member); // 假設你有 save 方法或 update 方法

		model.addAttribute("msg", "密碼變更成功！請重新登入～");

		return "member/password/changePassword";
	}

	@PostMapping("/forgot")
	public String processForgotPassword(@RequestParam("email") String email, Model model) {
		System.out.println("🔔 [DEBUG] 進入 forgot controller！");
		System.out.println("🔎 [DEBUG] 使用者輸入的 email: " + email);

		Optional<memVO> optional = memberRepository.findByMemberEmail(email);

		if (optional.isPresent()) {
			memVO member = optional.get();
			System.out.println("✅ [DEBUG] 找到會員帳號: " + member.getMemberAccount());
			memberService.generateResetToken(member);
			model.addAttribute("msg", "重設密碼連結已寄出，請檢查您的信箱");
		} else {
			System.out.println("❌ [DEBUG] 查無此 email！");
			model.addAttribute("error", "查無此 Email，請確認輸入是否正確");
		}

		return "member/password/forgotPassword";
	}

	@PostMapping("/reset")
	public String processResetPassword(@RequestParam("token") String token,
			@RequestParam("newPassword") String newPassword, @RequestParam("confirmPassword") String confirmPassword,
			Model model) {
		System.out.println("🎯 進入 processResetPassword()");

		System.out.println("[DEBUG] 提交重設密碼 token = " + token);

		if (!newPassword.equals(confirmPassword)) {
			model.addAttribute("error", "兩次輸入的密碼不一致");
			model.addAttribute("token", token); // 繼續保留 token 供表單使用
			return "member/password/reset_password";
		}

		Optional<memVO> optional = memberService.findByResetToken(token);
		if (optional.isPresent()) {
			memVO member = optional.get();
			memberService.resetPassword(member, newPassword);
			model.addAttribute("msg", "密碼已成功重設，請重新登入");
			return "member/login";
		} else {
			model.addAttribute("error", "連結無效或已過期");
			return "member/password/reset_password";
		}
	}

	@GetMapping("/favorites")
	public String showFavorites(@AuthenticationPrincipal MemberUserDetails loginUser, Model model) {
		memVO member = loginUser.getMember();

		// Get favorite travel plans
		List<TravelPlan> plans = favoriteTravelPlanSvc.getTravelPlansByMember(member);

		// Get favorite sceneries
		List<SceneryVO> sceneryFavorites = favortieScenerySvc.getFavoritesByMember(member);

		// Encode banner images for sceneries
		for (SceneryVO scenery : sceneryFavorites) {
			if (scenery.getSceneryBanner() != null) {
				String base64Image = Base64.getEncoder().encodeToString(scenery.getSceneryBanner());
				scenery.setImageUrl("data:image/png;base64," + base64Image);
			}
		}

		model.addAttribute("sceneryFavorites", sceneryFavorites);
		model.addAttribute("tourFavorites", plans);
		return "member/favorites";
	}

	// Add unfavorite endpoint
	@PostMapping("/favorites/scenery/remove/{id}")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> removeFavoriteScenery(@PathVariable Integer id,
			@AuthenticationPrincipal MemberUserDetails loginUser) {
		Map<String, Object> response = new HashMap<>();

		try {
			memVO member = loginUser.getMember();

			// Remove from favorites
			favortieScenerySvc.removeFavorite(member.getMemberId(), id);

			response.put("success", true);
			response.put("message", "已從收藏中移除");
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			System.err.println("Error removing favorite scenery: " + e.getMessage());
			e.printStackTrace();
			response.put("success", false);
			response.put("message", "操作失敗");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}
	
	// (旅程)加入收藏
	@PostMapping("/favorites/tour/add/{planId}")
	@ResponseBody
	public Map<String, Object> addFavorite(@PathVariable Integer planId,
	                                       @AuthenticationPrincipal MemberUserDetails loginUser) {
	    Map<String, Object> response = new HashMap<>();
	    memVO member = loginUser.getMember();

	    TravelPlan plan = travelPlanSvc.findById(planId).orElse(null);
	    if (plan == null) {
	        response.put("success", false);
	        response.put("message", "找不到旅程");
	        return response;
	    }

	    favoriteTravelPlanSvc.addFavorite(member, plan);
	    response.put("success", true);
	    return response;
	}


	
    // (旅程)移除收藏
	@PostMapping("/favorites/tour/remove/{id}")
	@ResponseBody
	public Map<String, Object> removeFavoritePlan(@PathVariable Integer id,
	                                              @AuthenticationPrincipal MemberUserDetails loginUser) {
	    Map<String, Object> result = new HashMap<>();
	    try {
	        memVO member = loginUser.getMember();

	        TravelPlan plan = travelPlanSvc.findById(id).orElse(null);
	        if (plan == null) {
	            result.put("success", false);
	            result.put("message", "找不到旅程");
	            return result;
	        }

	        favoriteTravelPlanSvc.removeFavorite(member, plan); // ❗這裡可能報錯

	        result.put("success", true);
	    } catch (Exception e) {
	        e.printStackTrace(); // ✅ 加這行！！
	        result.put("success", false);
	        result.put("message", "取消收藏失敗");
	    }
	    return result;
	}


}
