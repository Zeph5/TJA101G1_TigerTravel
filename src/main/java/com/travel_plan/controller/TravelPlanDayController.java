package com.travel_plan.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.scenery.model.SceneryService;
import com.scenery.model.SceneryVO;
import com.travel_plan.dto.DailyItineraryFormDTO;
import com.travel_plan.model.TravelItinerary;
import com.travel_plan.model.TravelPlan;
import com.travel_plan.service.TravelPlanService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/travelplans")
public class TravelPlanDayController {
	private final TravelPlanService TravelPlanService;
	private final SceneryService sceneryService;

	@Autowired
	public TravelPlanDayController(TravelPlanService travelPlanService, SceneryService sceneryService) {
		this.TravelPlanService = travelPlanService;
		this.sceneryService = sceneryService;
	}

	@GetMapping("/{planId}/itinerary/overview")
	public String showItineraryOverview(@PathVariable Integer planId, Model model, HttpSession session,
			RedirectAttributes redirectAttributes) {

		try {
			// 1. 根據 planId 取得旅行計畫的開始和結束日期
			// 這需要 TravelPlanService 提供一個方法來獲取這些日期
			TravelPlan travelPlan = TravelPlanService.getTravelPlanEntityById(planId)
					.orElseThrow(() -> new IllegalArgumentException("Travel Plan not found for ID: " + planId));
			// 2. 計算所有行程日期
			// 從開始日期到結束日期，生成所有天數的日期列表 (LocalDate)
			List<LocalDate> itineraryDates = TravelPlanService.generateDatesBetween(travelPlan.getStartDate(),
					travelPlan.getEndDate());

			// 3. 獲取或創建對應的 TravelItinerary (如果一個 TravelPlan 可以有多個 Itinerary，可能需要更多邏輯)
			// 目前看來，一個 TravelPlan 對應一個 Itinerary，若無則創建。
			TravelItinerary travelItinerary = TravelPlanService.getOrCreateTravelItineraryForPlan(planId);
			Integer travelItineraryId = travelItinerary.getTravelItineraryId(); // 取得行程 ID
			// 4. 預設載入第一個日期的行程數據 (作為初始顯示)
			LocalDate firstDate = itineraryDates.get(0);
			Integer travelDayNumber = 1; // 第一天
			// 5. 準備 DailyItineraryFormDTO 來填充表單
			// 這會從資料庫載入該日期的所有 TravelPlanDay 項目
			DailyItineraryFormDTO dailyItineraryFormDTO = TravelPlanService.getDailyItineraryFormDTO(travelItineraryId,
					firstDate, travelDayNumber);

			// 6. 將數據添加到 Model 中，供 Thymeleaf 使用
			model.addAttribute("travelPlanId", planId);
			model.addAttribute("travelItineraryId", travelItineraryId);
			model.addAttribute("itineraryDates", itineraryDates); // 所有日期列表
			model.addAttribute("currentEditDate", firstDate); // 當前編輯的日期 (預設為第一天)
			model.addAttribute("travelDayNumber", travelDayNumber); // 當前編輯的天數
			model.addAttribute("dailyItineraryFormDTO", dailyItineraryFormDTO); // 當天行程的數據

			// 7. 將當前行程 ID 存入 Session，供 Ajax 請求使用
			session.setAttribute("currentTravelItineraryId", travelItineraryId);
			session.setAttribute("currentTravelPlanId", planId);

			return "admin/travelplans/form_step2_itinerary_details";
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			return "redirect:/admin/travelplans";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "載入行程頁面時發生錯誤：" + e.getMessage());
			// logger.error("Error loading itinerary overview", e);
			return "redirect:/admin/travelplans";
		} // 返回行程概覽頁面
	}

	@GetMapping("/api/sceneries/all")
	@ResponseBody
	public ResponseEntity<List<SceneryVO>> getAllSceneries() {
		List<SceneryVO> sceneries = sceneryService.findAllScenery();
		return ResponseEntity.ok(sceneries);
	}

}
