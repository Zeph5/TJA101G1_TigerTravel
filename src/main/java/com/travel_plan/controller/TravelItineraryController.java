package com.travel_plan.controller;
//
////顯示新增/編輯旅行行程第二步的表單 (/admin/travelplans/{planId}/itinerary/new / {planId}/itinerary/{itineraryId}/edit)。
////
////處理新增/編輯旅行行程第二步的表單提交 (/admin/travelplans/{planId}/itinerary)。
////
////處理旅行行程的刪除等操作。
//

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

import com.travel_plan.dto.TravelItineraryDTO;
import com.travel_plan.model.TravelItinerary;
import com.travel_plan.model.TravelPlan;
import com.travel_plan.service.TravelItineraryService;
import com.travel_plan.service.TravelPlanService;
import com.travel_plan.dto.TravelPlanCreationDTO;

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
	// 顯示旅行行程第二步的表單
    @GetMapping("/overview") // 這個路徑會在第一步驟完成後被訪問
    public String showTravelItineraryOverviewStep2(@PathVariable("planId") Integer planId
    		, Model model
    		) {    	
    	
    	// 1. 確保 TravelPlan 存在並取得實體，用於前端顯示及後續驗證
    	TravelPlanCreationDTO travelPlanDto = travelPlanService.getTravelPlanById(planId)
				.orElseThrow(() -> new IllegalArgumentException("找不到 ID 為 " + planId + " 的旅行計畫。")); 
    	 // 2. 計算所有日期並添加到 Model
    	
    	LocalDate currentDate = travelPlanDto.getStartDate();
    	LocalDate endDate = travelPlanDto.getEndDate();
    	if (currentDate == null || endDate == null) {
    	    throw new IllegalArgumentException("StartDate 或 EndDate 為 null");
    	}
    	List<LocalDate> itineraryDates = new ArrayList<>();
    	while (!currentDate.isAfter(endDate)){
			itineraryDates.add(currentDate);
			currentDate = currentDate.plusDays(1);
		}
		model.addAttribute("itineraryDates", itineraryDates); // 將日期列表添加到 Model 中
		model.addAttribute("travelPlanStartDate", travelPlanDto.getStartDate());
		model.addAttribute("travelPlanEndDate", travelPlanDto.getEndDate());
		model.addAttribute("travelPlanDTO", travelPlanDto);
		
//		 // 3. 獲取或初始化 TravelItineraryDTO
      
//        // 可能是根據 travelPlanId 查詢是否存在已有的行程總覽
        Optional<TravelItineraryDTO> existingItinerary = travelItineraryService.getTravelItineraryById(planId);
        TravelItineraryDTO travelItineraryDTO = existingItinerary.orElseGet(TravelItineraryDTO::new);
//        // 如果是新建，可能需要設置 planId 關聯
//        // travelItineraryDTO.setTravelPlanId(planId); // 假設 TravelItineraryDTO 有這個屬性
		
		model.addAttribute("travelItineraryDTO",travelItineraryDTO);
		model.addAttribute("travelPlanId",planId);// 初始化一個新的 DTO
		return "admin/travelplans/form_step2_itinerary_details";
	}
    @PostMapping("/overview") // 提交旅行行程第二步的表單
    public String createTravelItinerary(@PathVariable("planId") Integer planId,
    									@ModelAttribute("travelItineraryDTO")
										@Valid TravelItineraryDTO travelItineraryDto,										
										BindingResult result,
										Model model,
										HttpSession session,
										RedirectAttributes redirectAttributes) {
		
		 // 1. 驗證 TravelPlan ID 是否與 Session 中的 ID 一致
		if (!planId.equals(session.getAttribute("currentTravelPlanId"))) {
			redirectAttributes.addFlashAttribute("errorMessage", "無效的旅行計畫 ID，請從第一步重新開始。");
	        return "redirect:/admin/travelplans/new"; // 重定向回第一步或錯誤頁面惡意操作。
		}
		// 如果驗證失敗，則返回錯誤訊息並重定向到第一步驟
		if(result.hasErrors()) {
			TravelPlanCreationDTO travelPlanDto = travelPlanService.getTravelPlanById(planId)
					.orElseThrow(() -> new IllegalArgumentException("找不到 ID 為 " + planId + " 的旅行計畫。"));			
			
			LocalDate currentDate = travelPlanDto.getStartDate();
	    	LocalDate endDate = travelPlanDto.getEndDate();
	    	if (currentDate == null || endDate == null) {
	    	    throw new IllegalArgumentException("StartDate 或 EndDate 為 null");
	    	}
	    	List<LocalDate> itineraryDates = new ArrayList<>();
	    	while (!currentDate.isAfter(endDate)){
				itineraryDates.add(currentDate);
				currentDate = currentDate.plusDays(1);
			}
	        model.addAttribute("itineraryDates", itineraryDates);
	        model.addAttribute("travelPlanStartDate", travelPlanDto.getStartDate());
	        model.addAttribute("travelPlanEndDate", travelPlanDto.getEndDate());
	        model.addAttribute("travelPlanId", planId); // 再次傳遞 planId
	        model.addAttribute("errorMessage", "資料驗證失敗，請檢查輸入。");
	        return "admin/travelplans/form_step2_itinerary_details"; // 返回當前頁面顯示錯誤
	    }
		try {
			// 3. 確保 TravelPlan 存在 (這兩段都有)
			travelPlanService.getTravelPlanEntityById(planId)
				.orElseThrow(() -> new IllegalArgumentException("找不到 ID 為 " + planId + " 的旅行計畫。"));
			// 4. 調用 Service 保存或更新 TravelItineraryDTO
			TravelItinerary travelItinerary = travelItineraryService.createTravelItineraryFromDto(planId, travelItineraryDto);
			 // 5. 設定 session 屬性
			session.setAttribute("currentTravelItineraryId", travelItinerary.getTravelItineraryId());
			// 6. 添加成功訊息
			redirectAttributes.addFlashAttribute("successMessage", "行程總覽儲存成功，請繼續編輯行程細節。");
			// 7. 重定向到下一步驟的行程細節編輯頁面
			return "redirect:/admin/travelplans/" + planId + "/itinerary/" + travelItinerary.getTravelItineraryId() + "/days/overview";
		} catch (IllegalArgumentException e) { // 捕獲自定義的例外
	        redirectAttributes.addFlashAttribute("errorMessage", "儲存行程總覽失敗: " + e.getMessage());
	        return "redirect:/admin/travelplans/" + planId + "/itinerary/overview"; // 或者返回錯誤頁面
	    } catch (Exception e) { // 捕獲其他一般性例外
	        redirectAttributes.addFlashAttribute("errorMessage", "儲存行程總覽失敗: 發生未知錯誤。");
	        return "redirect:/admin/travelplans/" + planId + "/itinerary/overview"; // 或者返回錯誤頁面
	    }
	}
   
    @GetMapping("/{itineraryId}/edit")
    public String showEditItineraryForm(@PathVariable("planId") Integer planId,
										@PathVariable("itineraryId") Integer itineraryId,
										Model model,
										HttpSession session) {
    	
    	// 1. 確保 TravelPlan 存在並取得實體，用於前端顯示及後續驗證
        TravelPlan travelPlan = travelPlanService.getTravelPlanEntityById(planId)
                                    .orElseThrow(() -> new IllegalArgumentException("找不到 ID 為 " + planId + " 的旅行計畫。"));
        model.addAttribute("travelPlan", travelPlan); // 將 TravelPlan 實體傳遞到前端
        
        // 2. 從 Service 獲取 TravelItinerary Entity
        TravelItinerary existingItinerary = travelItineraryService.getTravelItineraryEntityById(itineraryId)
                .orElseThrow(() -> new IllegalArgumentException("找不到 ID 為 " + itineraryId + " 的旅行行程。"));
        
        
//		// 3. 【重要】驗證 TravelItinerary 是否真的屬於該 TravelPlan (安全性與數據一致性)
        if (!existingItinerary.getTravelPlan().getTravelPlanId().equals(planId)) {
            // 如果行程的 TravelPlan ID 與 URL 中的 planId 不匹配，可能是惡意請求或數據不一致
            // 拋出異常比靜默失敗或重定向更明確
            throw new IllegalArgumentException("行程 ID " + itineraryId + " 不屬於計畫 ID " + planId + "。");
            // ex: 你正在編輯「日本北海道五日遊」的行程細節，但網址裡的行程ID卻是「泰國曼谷三天兩夜」的，這明顯是錯誤或惡意操作。
        }
     // 4. 【重要】將 Entity 轉換為 DTO，傳遞給前端表單 (解決 Type Mismatch 錯誤)
        TravelItineraryDTO dto = travelItineraryService.convertToItineraryDto(existingItinerary);
        model.addAttribute("travelItineraryDTO", dto); // 將轉換後的 DTO 傳遞到表單

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