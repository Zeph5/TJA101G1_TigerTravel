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

	@GetMapping("/new") // 顯示新增計畫的表單頁面。
	public String showNewTPForm(Model model) {
		model.addAttribute("travelPlanCreationDto", new TravelPlanCreationDTO()); // 假設有一個 TravelPlan 類別
		return "admin/travelplans/form_step1_plan_details"; // 返回新增計畫的表單頁面
	}

	@GetMapping
	public String listTravelPlans(Model model) {
		// 從 Service 獲取數據
		List<TravelPlan> plans = travelPlanService.getAllTravelPlans();
		model.addAttribute("travelPlans", plans);
		return "admin/travelplans/list";
	}

	@PostMapping // ：處理提交新計畫的請求。
	public String createTravelPlan(@Valid @ModelAttribute("travelPlanCreationDto") TravelPlanCreationDTO dto
			,BindingResult result
			, RedirectAttributes redirectAttributes
			, HttpSession session) 
	{
		if (result.hasErrors()) {
			return "admin/travelplans/form_step1_plan_details"; // 如果有錯誤，返回新增計畫的表單頁面
		}

		// 呼叫服務層處理業務邏輯 (DTO 轉換為 Entity 並儲存，含圖片上傳)
		TravelPlan savedPlan = travelPlanService.createTravelPlanFromDto(dto);

		// 將新建立的旅行計畫 ID 儲存到 Session 中，供下一步使用
		session.setAttribute("currentTravelPlanId", savedPlan.getTravelPlanId());

		// 添加成功訊息，並在重定向後顯示
		redirectAttributes.addFlashAttribute("successMessage", "計畫基本資訊儲存成功，請繼續編輯行程細節。");
		// 重定向到下一步的行程細節編輯頁面
		return "redirect:/admin/travelplans/" + savedPlan.getTravelPlanId() + "/itinerary/new";
	}

	 // 編輯現有計畫的入口點 (可重用第一步表單)
    @GetMapping("/{id}/edit")
    public String editTravelPlan(@PathVariable("id") Integer id, Model model, 
    		HttpSession session
    		,BindingResult bindingResult
    		,TravelPlanCreationDTO travelPlanCreationDto) {
        
    	TravelPlan existingPlan = travelPlanService.getTravelPlanById(id)
                                    .orElseThrow(() -> new IllegalArgumentException("TravelPlan not found"));
        // 將 Entity 轉換為 DTO 填充表單 (如果需要編輯圖片，也要將當前 URL 傳遞給前端顯示)
        TravelPlanCreationDTO dto = new TravelPlanCreationDTO();
        dto.setTravelPlanId(existingPlan.getTravelPlanId());
        dto.setTraveltitle(existingPlan.getTravelTitle());
        dto.setTravelplandescription(existingPlan.getTravelPlanDescription());
        

        model.addAttribute("travelPlanCreationDto", dto);
        session.setAttribute("currentTravelPlanId", id); // 存入 Session

        return "admin/travelplans/form_step1_plan_details";
    }
    // ... (其他只與 TravelPlan 相關的方法，如刪除)
}
