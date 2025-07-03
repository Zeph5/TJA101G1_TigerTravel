package com.travel_plan.controller;
//
////顯示新增/編輯旅行行程第二步的表單 (/admin/travelplans/{planId}/itinerary/new / {planId}/itinerary/{itineraryId}/edit)。
////
////處理新增/編輯旅行行程第二步的表單提交 (/admin/travelplans/{planId}/itinerary)。
////
////處理旅行行程的刪除等操作。
//

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.travel_plan.dto.TravelItineraryDTO;
import com.travel_plan.model.TravelItinerary;
import com.travel_plan.model.TravelPlan;
import com.travel_plan.service.TravelItineraryService;
import com.travel_plan.service.TravelPlanService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/travelplans/{planId}/itinerary") // 繼承 planId 的路徑

public class TravelItineraryController {
	private final TravelItineraryService travelItineraryService;
	private final TravelPlanService travelPlanService;
	
	@Autowired
	public TravelItineraryController(TravelItineraryService travelItineraryService, 
			TravelPlanService travelPlanService) {
		this.travelItineraryService = travelItineraryService;
		this.travelPlanService = travelPlanService;
	}    
    @GetMapping("/new")
    public String showCreateItineraryForm(@PathVariable("planId") Integer planId
    		, Model model
    		, HttpSession session) {
		// 驗證 planId 是否與 session 中保存的 currentTravelPlanId 一致
		if (!planId.equals(session.getAttribute("currentTravelPlanId"))) {
			return "redirect:/admin/travelplans/new"; // 重定向回第一步或錯誤頁面
		}
		model.addAttribute("itineraryFormDto", new TravelItineraryDTO()); // <-- 使用 DTO
		model.addAttribute("travelPlanId", planId); // 傳遞 PlanId 到表單
		return "admin/travelplans/form_step2_itinerary_details";
	}

    @PostMapping
    public String saveTravelItineraryStep2(@Valid @PathVariable("planId") Integer planId
										   ,TravelItineraryDTO dto
										   ,BindingResult bindingresult
										   ,RedirectAttributes redirectattributes// <-- 使用 DTO
										   ,Model model
										   ,HttpSession session) {
		// 驗證 planId 是否與 session 中保存的 currentTravelPlanId 一致
		if (!planId.equals(session.getAttribute("currentTravelPlanId"))) {
			return "redirect:/admin/travelplans/new"; // 重定向回第一步或錯誤頁面
		}

		// 確保 TravelPlan 存在並取得實體
		travelPlanService.getTravelPlanById(planId)
				.orElseThrow(() -> new IllegalArgumentException("TravelPlan not found for ID: " + planId));

		// <-- 呼叫 Service 處理 DTO 到 Entity 的轉換和保存 -->
		travelItineraryService.createTravelItineraryFromDto(planId, dto);

		session.setAttribute("currentTravelItineraryId", dto.getTravelItineraryId());

		model.addAttribute("successMessage", "行程細節儲存成功，請繼續編輯每日行程。");
		return "redirect:/admin/travelplans/" + planId + "/itinerary/" + dto.getTravelItineraryId() + "/days/new"; // 導向第三步
	}
    
    //編輯現有行程 (人數、價格等 - TravelItinerary) 的入口點
    @GetMapping("/{itineraryId}/edit")
    public String showEditItineraryForm(@PathVariable("planId") Integer planId,
										@PathVariable("itineraryId") Integer itineraryId,
										Model model,
										HttpSession session) {
    	
    	// 1. 確保 TravelPlan 存在並取得實體，用於前端顯示及後續驗證
        TravelPlan travelPlan = travelPlanService.getTravelPlanById(planId)
                                    .orElseThrow(() -> new IllegalArgumentException("找不到 ID 為 " + planId + " 的旅行計畫。"));
        model.addAttribute("travelPlan", travelPlan); // 將 TravelPlan 實體傳遞到前端
        
        // 2. 從 Service 獲取 TravelItinerary Entity
        TravelItinerary existingItinerary = travelItineraryService.getTravelItineraryById(itineraryId)
                .orElseThrow(() -> new IllegalArgumentException("找不到 ID 為 " + itineraryId + " 的旅行行程。"));
//		// 3. 【重要】驗證 TravelItinerary 是否真的屬於該 TravelPlan (安全性與數據一致性)
        if (!existingItinerary.getTravelPlan().getTravelPlanId().equals(planId)) {
            // 如果行程的 TravelPlan ID 與 URL 中的 planId 不匹配，可能是惡意請求或數據不一致
            // 拋出異常比靜默失敗或重定向更明確
            throw new IllegalArgumentException("行程 ID " + itineraryId + " 不屬於計畫 ID " + planId + "。");
            // ex: 你正在編輯「日本北海道五日遊」的行程細節，但網址裡的行程ID卻是「泰國曼谷三天兩夜」的，這明顯是錯誤或惡意操作。
        }
     // 4. 【重要】將 Entity 轉換為 DTO，傳遞給前端表單 (解決 Type Mismatch 錯誤)
        TravelItineraryDTO dto = travelItineraryService.convertToDto(existingItinerary);
        model.addAttribute("travelItineraryDto", dto); // 將轉換後的 DTO 傳遞到表單

        // 5. 【重要】更新 Session 中的 ID，確保一致性
        // 這樣從編輯入口進入時，Session 狀態也能正確初始化，供後續步驟使用
        session.setAttribute("currentTravelPlanId", planId);
        session.setAttribute("currentTravelItineraryId", itineraryId);

        // 6. 傳遞訊息給前端 (可選)
        model.addAttribute("message", "正在編輯現有行程基本細節。");

        // 7. 返回視圖名稱
        return "admin/travelplans/form_step2_itinerary_details";
	}  

}