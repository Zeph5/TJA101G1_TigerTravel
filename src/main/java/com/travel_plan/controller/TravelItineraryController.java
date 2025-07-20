package com.travel_plan.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
import com.travel_plan.model.TravelPlanDay;
import com.travel_plan.service.TravelItineraryService; // 確保使用這個 Service
import com.travel_plan.service.TravelPlanDayService;
import com.travel_plan.service.TravelPlanService; // 用於獲取 TravelPlan 資訊

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/travelplans/{planId}/itinerary") // 繼承 planId 的路徑，這是正確的基礎路徑

public class TravelItineraryController {

	private final TravelItineraryService travelItineraryService;
	private final TravelPlanService travelPlanService;
	private final TravelPlanDayService travelPlanDayService; // 確保有這個 Service 用於處理每日行程

	@Autowired
	public TravelItineraryController(TravelItineraryService travelItineraryService, TravelPlanService travelPlanService,
			TravelPlanDayService travelPlanDayService) {
		this.travelItineraryService = travelItineraryService;
		this.travelPlanService = travelPlanService;
		this.travelPlanDayService = travelPlanDayService; // 初始化每日行程服務
	}

	// 顯示新增旅行行程第二步的表單
	@GetMapping
	public String listItinerariesForTravelPlan(@PathVariable("planId") Integer planId, Model model) {
		// 確保 TravelPlan 存在並取得基本資訊
		TravelPlan travelPlan = travelPlanService.getTravelPlanEntityById(planId)
				.orElseThrow(() -> new IllegalArgumentException("找不到 ID 為 " + planId + " 的旅行計畫。"));

		// 從 Service 獲取特定 TravelPlan 下的所有 TravelItinerary 梯次
		// 注意：這裡應該呼叫類似 getItinerariesByTravelPlanId(planId) 的服務方法
		List<TravelItinerary> itineraries = travelItineraryService.getItinerariesByTravelPlanId(planId);
		
		Map<TravelPlan, List<TravelItinerary>> groupedItineraries = new LinkedHashMap<>();
		groupedItineraries.put(travelPlan, itineraries); // 只一筆也一樣包進去

		model.addAttribute("groupedItineraries", groupedItineraries);

		return "admin/travelplans/listItinerary"; // 返回顯示行程梯次列表的視圖
	}

	@GetMapping("/new")
	public String showAddItineraryForm(@PathVariable("planId") Integer planId, Model model) {

		TravelPlan travelPlan = travelPlanService.getTravelPlanEntityById(planId)
				.orElseThrow(() -> new IllegalArgumentException("找不到 ID 為 " + planId + " 的旅行計畫。"));

		// 初始化一個新的 TravelItineraryDTO，用於表單的輸入，只預設 planId
		TravelItineraryDTO travelItineraryDTO = new TravelItineraryDTO();
		travelItineraryDTO.setTravelPlanId(planId); // 預設關聯的 TravelPlan ID

		model.addAttribute("travelItineraryDTO", travelItineraryDTO);
		model.addAttribute("travelPlanId", planId); // 傳遞 planId 給前端
		model.addAttribute("travelPlanTitle", travelPlan.getTravelTitle()); // 傳遞計畫名稱給前端顯示

		return "admin/travelplans/form_step2_itinerary_details";
	}

	// 處理編輯旅行行程第二步的表單提交
	// 路徑: POST /admin/travelplans/{planId}/itinerary/save
	@PostMapping("/save")
	public String saveItinerary(@PathVariable("planId") Integer planId,
			@ModelAttribute("travelItineraryDTO") @Valid TravelItineraryDTO travelItineraryDto, BindingResult result,
			Model model, HttpSession session, RedirectAttributes redirectAttributes) {

		// 新增行程的邏輯，不用判斷是否有 ID，因為是新增
		if (travelItineraryDto.getTravelItineraryId() != null) {
			redirectAttributes.addFlashAttribute("errorMessage", "新增時不應包含 ID！");
			return "redirect:/admin/travelplans/" + planId + "/itinerary/new";
		}

		if (result.hasErrors()) {
			travelPlanService.getTravelPlanEntityById(planId).ifPresent(plan -> {
				model.addAttribute("travelPlanTitle", plan.getTravelTitle());
			});
			model.addAttribute("travelPlanId", planId);
			model.addAttribute("errorMessage", "資料驗證失敗，請檢查輸入。");
			return "admin/travelplans/createItinerary"; // 假設這是新增畫面
		}

		try {
			TravelItinerary saved = travelItineraryService.saveTravelItineraryFromDto(travelItineraryDto);
			session.setAttribute("currentTravelItineraryId", saved.getTravelItineraryId());
			redirectAttributes.addFlashAttribute("successMessage", "行程梯次新增成功！");
			return "redirect:/admin/travelplans/" + planId + "/itinerary";
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "新增失敗：" + e.getMessage());
			return "redirect:/admin/travelplans/" + planId + "/itinerary/new";
		}
	}

	
	@GetMapping("/{itineraryId}/edit")
	public String showEditItineraryForm(@PathVariable("planId") Integer planId,
			@PathVariable("itineraryId") Integer itineraryId, Model model, HttpSession session) {

		// 1. 確保 TravelPlan 存在並取得基本資訊，用於前端顯示計畫名稱
		TravelPlan travelPlan = travelPlanService.getTravelPlanEntityById(planId)
				.orElseThrow(() -> new IllegalArgumentException("找不到 ID 為 " + planId + " 的旅行計畫。"));
		model.addAttribute("travelPlanTitle", travelPlan.getTravelTitle()); // 傳遞計畫名稱

		// 2. 從 Service 獲取 TravelItinerary Entity
		TravelItinerary existingItinerary = travelItineraryService.getTravelItineraryEntityById(itineraryId)
				.orElseThrow(() -> new IllegalArgumentException("找不到 ID 為 " + itineraryId + " 的旅行行程。"));

		// 3. 【重要】驗證 TravelItinerary 是否真的屬於該 TravelPlan (安全性與數據一致性)
		if (!existingItinerary.getTravelPlan().getTravelPlanId().equals(planId)) {
			// ex: 你正在編輯「日本北海道五日遊」的行程細節，但網址裡的行程ID卻是「泰國曼谷三天兩夜」的，這明顯是錯誤或惡意操作。
			throw new IllegalArgumentException("行程 ID " + itineraryId + " 不屬於計畫 ID " + planId + "。");
		}

		// 4. 【重要】將 Entity 轉換為 DTO，傳遞給前端表單 (解決 Type Mismatch 錯誤)
		TravelItineraryDTO dto = travelItineraryService.convertToItineraryDto(existingItinerary);
		dto.setTravelPlanId(planId);
		model.addAttribute("travelItineraryDTO", dto); // 將轉換後的 DTO 傳遞到表單

		// 5. 【重要】更新 Session 中的 ID，確保一致性
		session.setAttribute("currentTravelPlanId", planId);
		session.setAttribute("currentTravelItineraryId", itineraryId);

		// 6. 傳遞訊息給前端 (可選)
		model.addAttribute("message", "正在編輯現有梯次基本細節。");
		model.addAttribute("travelPlanId", planId); // 傳遞 planId 給前端用於表單提交路徑等

		// 7. 返回視圖名稱
		return "admin/travelplans/form_step2_itinerary_details";
	}

	@PostMapping("/{itineraryId}/update")
	public String updateItinerary(@PathVariable("planId") Integer planId,
			@PathVariable("itineraryId") Integer itineraryId,
			@ModelAttribute("travelItineraryDTO") @Valid TravelItineraryDTO travelItineraryDto, BindingResult result,
			Model model, RedirectAttributes redirectAttributes) {

		// 驗證 URL id 和 DTO 一致
		if (!Objects.equals(itineraryId, travelItineraryDto.getTravelItineraryId())) {
			redirectAttributes.addFlashAttribute("errorMessage", "行程 ID 不一致！");
			return "redirect:/admin/travelplans/" + planId + "/itinerary/" + itineraryId + "/edit";
		}

		if (result.hasErrors()) {
			travelPlanService.getTravelPlanEntityById(planId).ifPresent(plan -> {
				model.addAttribute("travelPlanTitle", plan.getTravelTitle());
			});
			model.addAttribute("travelPlanId", planId);
			model.addAttribute("travelItineraryId", itineraryId);
			model.addAttribute("errorMessage", "資料驗證失敗，請檢查輸入。");
			return "admin/travelplans/form_step2_itinerary_details";
		}

		try {
			travelItineraryService.updateTravelItineraryFromDto(travelItineraryDto);
			redirectAttributes.addFlashAttribute("successMessage", "行程梯次更新成功！");
			return "redirect:/admin/travelplans/" + planId + "/itinerary" ;
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "更新失敗：" + e.getMessage());
			return "redirect:/admin/travelplans/" + planId + "/itinerary/" + itineraryId + "/edit";
		}
	}
}