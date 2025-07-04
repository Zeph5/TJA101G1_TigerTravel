package com.travel_plan.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.travel_plan.dto.TravelPlanCreationDTO;
import com.travel_plan.model.TravelPlan;
import com.travel_plan.service.TravelPlanService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

//顯示所有旅行計畫列表 (/admin/travelplans)。
//
//顯示新增/編輯旅行計畫第一步的表單 (/admin/travelplans/new / {id}/edit)。
//
//處理新增/編輯旅行計畫第一步的表單提交 (/admin/travelplans)。
//
//處理旅行計畫的刪除、發布等操作。

@Controller
@RequestMapping("/admin/travelplans")
public class TravelPlanController {

	private final TravelPlanService travelPlanService;

	@Autowired
	public TravelPlanController(TravelPlanService travelPlanService) {
		this.travelPlanService = travelPlanService;
	}

	// 這是 顯示所有旅行計畫列表 的頁面入口。
	@GetMapping
	public String listTravelPlans(Model model) {
		// 從 Service 獲取數據
		List<TravelPlan> plans = travelPlanService.getAllTravelPlans(); // 從資料庫中取出所有已存在的旅行計畫資料。
		model.addAttribute("travelPlans", plans); // 放到 Model 中，以便 Thymeleaf 模板可以使用它們。
		return "admin/travelplans/list";
	}

	@GetMapping("/new") // 在 list.html 頁面點擊「新增計畫」按鈕/連結
	// 或直接訪問 http://localhost:8080/admin/travelplans/new 時，這個方法會被觸發。
	public String showNewTPForm(Model model) {
		model.addAttribute("travelPlanCreationDto", new TravelPlanCreationDTO()); // 假設有一個 TravelPlan 類別
		return "admin/travelplans/form_step1_plan_details";
	}

	@PostMapping // 使用者在 form_step1_plan_details.html 頁面填寫完資料並點擊「下一步」按鈕時觸發這個方法。
	public String createTravelPlan(@Valid @ModelAttribute("travelPlanCreationDto") TravelPlanCreationDTO dto,
			BindingResult result, RedirectAttributes redirectAttributes,
			@RequestParam(value = "bannerImage", required = false) MultipartFile bannerImage, HttpSession session,
			Model model) {
		 if (result.hasErrors()) {
		        model.addAttribute("travelPlanCreationDto", dto);		   
		        model.addAttribute("errorMessage", "資料驗證失敗，請檢查輸入。");
		        return "admin/travelplans/form_step1_plan_details"; // 返回表單頁面
		    }
		
		TravelPlan savedPlan ; // 用於儲存新建立的旅行計畫實體
		 if (dto.getTravelPlanId() != null && dto.getTravelPlanId() != 0) {
	            // 更新現有計畫 (假設 Service 有此方法)
	            savedPlan = travelPlanService.updateTravelPlan(dto.getTravelPlanId(), dto, bannerImage);
	            redirectAttributes.addFlashAttribute("successMessage", "計畫基本資訊更新成功，請繼續編輯行程細節。");
	        } else {
	            // 創建新計畫 (假設 Service 有此方法)
	            savedPlan = travelPlanService.createTravelPlanFromDto(dto, bannerImage);
	            redirectAttributes.addFlashAttribute("successMessage", "計畫基本資訊儲存成功，請繼續編輯行程細節。");
	        }	

		// 將新建立的旅行計畫 ID 儲存到 Session 中，供下一步使用
		session.setAttribute("currentTravelPlanId", savedPlan.getTravelPlanId());
		session.removeAttribute("currentTravelItineraryId"); // 清除可能存在的行程 ID，因為新增計畫時不需要行程 ID
		// 添加成功訊息，並在重定向後顯示
		redirectAttributes.addFlashAttribute("successMessage", "計畫基本資訊儲存成功，請繼續編輯行程細節。");
		// 重定向到下一步的行程細節編輯頁面
		return "redirect:/admin/travelplans/" + savedPlan.getTravelPlanId() + "/itinerary/overview";
	}

	// 編輯現有計畫的入口點 (可重用第一步表單)
	@GetMapping("/{id}/edit")
	public String editTravelPlan(@PathVariable("id") Integer id, Model model, HttpSession session) {

		// 確保計畫 ID 存在並取得實體
		// 如果不存在，則拋出異常或返回錯誤頁面
		TravelPlan existingPlan = travelPlanService.getTravelPlanEntityById(id)
				.orElseThrow(() -> new IllegalArgumentException("TravelPlan not found"));

		// 將 Entity 轉換為 DTO 填充表單 (如果需要編輯圖片，也要將當前 URL 傳遞給前端顯示)
		TravelPlanCreationDTO dto = travelPlanService.convertToCreationDto(existingPlan);

		model.addAttribute("travelPlanCreationDto", dto); // 將 DTO 傳遞到前端表單

		session.setAttribute("currentTravelPlanId", id); // 將當前計畫 ID 儲存到 Session 中，供後續使用
		session.removeAttribute("currentTravelItineraryId"); // 清除可能存在的行程 ID，因為編輯計畫時不需要行程 ID

		model.addAttribute("message", "正在編輯現有旅行資訊");

		return "admin/travelplans/form_step1_plan_details";
	}
	// ... (其他只與 TravelPlan 相關的方法，如刪除)
}
