package com.travel_plan.controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.scenery.model.SceneryService;
import com.scenery.model.SceneryVO;
import com.travel_plan.dto.CombinedItineraryFormDTO;
import com.travel_plan.dto.DailyItineraryFormDTO;
import com.travel_plan.model.TravelItinerary;
import com.travel_plan.model.TravelPlan;
import com.travel_plan.service.TravelPlanService;
import com.travel_plan.dto.TravelPlanPreviewDTO;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

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
	        Integer sessionItineraryId = (Integer) session.getAttribute("currentTravelItineraryId");
	        TravelItinerary travelItinerary;

	        if (sessionItineraryId != null) {
	            travelItinerary = TravelPlanService.getTravelItineraryById(sessionItineraryId)
	                .orElseThrow(() -> new IllegalArgumentException("Travel Itinerary not found for ID: " + sessionItineraryId)); // 使用 sessionItineraryId
	        } else {
	            travelItinerary = TravelPlanService.getOrCreateTravelItineraryForPlan(planId);
	            session.setAttribute("currentTravelItineraryId", travelItinerary.getTravelItineraryId()); // 更新 Session
	        }

	        // 在這裡將最終確定的 travelItineraryId 賦值給一個 effectively final 的變數
	        final Integer currentItineraryId = travelItinerary.getTravelItineraryId(); // <-- 新增這行，確保它是 final 或 effectively final

	        // 1. 根據 TravelItinerary 取得旅行計畫的開始和結束日期
	        LocalDate itineraryStartDate = travelItinerary.getStartDate();
	        LocalDate itineraryEndDate = travelItinerary.getEndDate();

	        if (itineraryStartDate == null || itineraryEndDate == null) {
	            redirectAttributes.addFlashAttribute("errorMessage", "請先完成行程梯次設定 (日期和人數)。");
	            return "redirect:/admin/travelplans/" + planId + "/itineraries/add";
	        }

	        List<LocalDate> itineraryDates = TravelPlanService.generateDatesBetween(itineraryStartDate, itineraryEndDate);

	        LocalDate firstDate = itineraryDates.isEmpty() ? itineraryStartDate : itineraryDates.get(0);
	        
	        // 使用 currentItineraryId 來計算天數
	        Integer travelDayNumber = TravelPlanService.calculateTravelDayNumber(currentItineraryId, firstDate); // <-- 使用 currentItineraryId

	        model.addAttribute("travelPlanId", planId);
	        model.addAttribute("travelItineraryId", currentItineraryId); // <-- 傳遞 currentItineraryId
	        model.addAttribute("itineraryDates", itineraryDates);
	        model.addAttribute("currentEditDate", firstDate);
	        model.addAttribute("travelDayNumber", travelDayNumber);
	        model.addAttribute("dailyItineraryFormDTO", TravelPlanService.getDailyItineraryFormDTO(currentItineraryId, firstDate, travelDayNumber)); // <-- 使用 currentItineraryId

	        session.setAttribute("currentTravelPlanId", planId);

	        return "admin/travelplans/form_step2_itinerary_details";
	    } catch (IllegalArgumentException e) {
	        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
	        return "redirect:/admin/travelplans";
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("errorMessage", "載入行程頁面時發生錯誤：" + e.getMessage());
	        return "redirect:/admin/travelplans";
	    }
	}

	@GetMapping("/api/sceneries/all")
	@ResponseBody
	public ResponseEntity<List<SceneryVO>> getAllSceneries() {
		List<SceneryVO> sceneries = sceneryService.findAllScenery();
		return ResponseEntity.ok(sceneries);
	}
	@GetMapping("/{planId}/preview")
    public String previewTravelPlan(@PathVariable("planId") Integer travelPlanId, Model model, RedirectAttributes redirectAttributes) {

        try {
            // 呼叫 Service 層新的方法來獲取完整的預覽 DTO
            TravelPlanPreviewDTO travelPlanPreviewDTO = TravelPlanService.getFullTravelPlanDetails(travelPlanId);

            model.addAttribute("travelPlanPreview", travelPlanPreviewDTO);

            // 計算總天數，從 TravelPlanPreviewDTO 中獲取梯次日期
            if (travelPlanPreviewDTO.getStartDate() != null && travelPlanPreviewDTO.getEndDate() != null) {
                long totalDays = ChronoUnit.DAYS.between(travelPlanPreviewDTO.getStartDate(), travelPlanPreviewDTO.getEndDate()) + 1;
                model.addAttribute("totalTravelDays", totalDays);
            } else {
                model.addAttribute("totalTravelDays", 0);
            }

            return "admin/travelplans/preview_full_plan"; // 返回預覽頁面
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/travelplans"; // 如果找不到計畫或梯次，重導回列表頁
        } catch (Exception e) {
            // 這裡應該記錄錯誤日誌 e.g., logger.error("Error loading travel plan preview", e);
            redirectAttributes.addFlashAttribute("errorMessage", "載入旅行計畫預覽時發生錯誤：" + e.getMessage());
            return "redirect:/admin/travelplans";
        }
    }

}
