package com.travel_plan.controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.scenery.model.SceneryService;
import com.scenery.model.SceneryVO;
import com.travel_plan.dto.CombinedItineraryFormDTO;
import com.travel_plan.dto.DailyItineraryFormDTO;
import com.travel_plan.dto.TravelPlanDayDTO;
import com.travel_plan.model.TravelItinerary;
import com.travel_plan.model.TravelPlan;
import com.travel_plan.model.TravelPlanDay;
import com.travel_plan.repository.TravelPlanDayRepository;
import com.travel_plan.service.TravelItineraryService;
import com.travel_plan.service.TravelPlanDayService;
import com.travel_plan.service.TravelPlanService;
import com.travel_plan.dto.TravelPlanPreviewDTO;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/travelplans/{planId}/itinerary/{itineraryId}/days")
public class TravelPlanDayController {
	private final TravelPlanService travelPlanService;
	private final SceneryService sceneryService;
	private final TravelPlanDayRepository travelPlanDayRepository;
	private final TravelPlanDayService travelPlanDayService;
	private final TravelItineraryService travelItineraryService;

	@Autowired
	public TravelPlanDayController(TravelPlanService travelPlanService, SceneryService sceneryService,
			TravelPlanDayRepository travelPlanDayRepository, TravelPlanDayService travelPlanDayService,
			TravelItineraryService travelItineraryService) {
		this.travelPlanService = travelPlanService;
		this.sceneryService = sceneryService;
		this.travelPlanDayRepository = travelPlanDayRepository;
		this.travelPlanDayService = travelPlanDayService;
		this.travelItineraryService = travelItineraryService;

	}

	@GetMapping
	public String showDailyItineraryView(@PathVariable Integer planId, @PathVariable Integer itineraryId,
			@RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
			Model model, RedirectAttributes redirectAttributes) {

		// 取得梯次資料
		TravelItinerary itinerary = travelPlanService.getTravelItineraryById(itineraryId)
				.orElseThrow(() -> new IllegalArgumentException("找不到行程梯次"));

		// 如果沒帶日期，預設第一天
		LocalDate currentEditDate = (date != null) ? date : itinerary.getStartDate();

		// 取得該日期的每日行程資料
		List<TravelPlanDay> travelPlanDays = travelPlanDayService
				.getDaysByItineraryIdAndDateSortedBySequence(itineraryId, currentEditDate);
		// 計算可選日期區間
		List<LocalDate> itineraryDates = travelPlanService.generateItineraryDates(itinerary.getStartDate(),
				itinerary.getEndDate());
		// 計算第幾天
		int travelDayNumber = travelPlanDayService.calculateTravelDayNumber(itineraryId, currentEditDate);
		// 取得當日DTO
		DailyItineraryFormDTO dailyDTO = travelPlanService.getDailyItineraryFormDTO(itineraryId, currentEditDate,
				travelDayNumber);


		List<SceneryVO> allSceneriesList = travelPlanDayService.findAllScenery();

		Map<Integer, String> allSceneriesMap = allSceneriesList.stream()
				.collect(Collectors.toMap(SceneryVO::getSceneryId, SceneryVO::getSceneryName));

		model.addAttribute("startDate", itinerary.getStartDate());
		model.addAttribute("endDate", itinerary.getEndDate());
		model.addAttribute("itineraryDates", itineraryDates);
		model.addAttribute("currentEditDate", currentEditDate);
		model.addAttribute("travelPlanDays", travelPlanDays);
		model.addAttribute("dailyItineraryFormDTO", dailyDTO);
		model.addAttribute("travelPlanId", planId);
		model.addAttribute("travelItineraryId", itineraryId);
		model.addAttribute("duplicateSequenceSet", model.asMap().get("duplicateSequenceSet"));

		return "admin/travelplans/listTravelPlanDay";
	}
///////////////更改順序/////////////////////////////////////////////	
	@PostMapping("/updateSequence")
	public String updateTravelPlanDaySequence(
			@PathVariable Integer planId, 
			@PathVariable Integer itineraryId,
			@RequestParam Integer travelPlanDayId,
			@RequestParam String action,
			@RequestParam String currentEditDate			
			) {
		travelPlanDayService.updateTravelPlanDaySequence(travelPlanDayId, action);
		return "redirect:/admin/travelplans/" + planId + "/itinerary/" + itineraryId + "/days?date=" + currentEditDate;
		
	}
	@PostMapping("/update-order")
	public ResponseEntity<Void> updateOrder(@RequestBody List<Integer> sortedIds){
		travelPlanDayService.updateOrder(sortedIds);
		System.out.println("更新排序順序: " + sortedIds);
	    return ResponseEntity.ok().build();
	}

	
	// 提供所有景點的 API
	// URL: GET
	// /admin/travelplans/{planId}/itinerary/{itineraryId}/days/api/sceneries/all
	@GetMapping("/api/sceneries/all")
	@ResponseBody
	public ResponseEntity<List<SceneryVO>> getAllSceneries() {

		List<SceneryVO> sceneries = travelPlanDayService.findAllScenery();

		return ResponseEntity.ok(sceneries);
	}

	// 顯示編輯每日行程的頁面
	@GetMapping("/edit/{travelPlanDayId}")
	public String showEditDailyItineraryPage(@PathVariable Integer planId, @PathVariable Integer travelPlanDayId,
			@PathVariable Integer itineraryId,
			@RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
			Model model, HttpSession session) {

		TravelPlanDayDTO travelPlanDayDTO = travelPlanDayService.getTravelPlanDayDTOById(travelPlanDayId)
				.orElseThrow(() -> new IllegalArgumentException("Travel Plan Day not found"));

		// 從 Service 獲取梯次資訊，以便計算所有天數
		TravelItinerary itinerary = travelPlanService.getTravelItineraryById(itineraryId)
				.orElseThrow(() -> new IllegalArgumentException("Itinerary not found"));

		// 計算梯次的所有日期
		List<LocalDate> itineraryDates = travelPlanService.generateItineraryDates(itinerary.getStartDate(),
				itinerary.getEndDate());

		// 確定當前要編輯的日期
		LocalDate currentEditDate = (date != null) ? date : itinerary.getStartDate();
		int travelDayNumber = travelPlanDayService.calculateTravelDayNumber(itineraryId, currentEditDate);


		List<SceneryVO> allSceneriesList = travelPlanDayService.findAllScenery();

		Map<Integer, String> allSceneriesMap = allSceneriesList.stream()
				.collect(Collectors.toMap(SceneryVO::getSceneryId, SceneryVO::getSceneryName));
		model.addAttribute("allSceneries", allSceneriesMap);
		

		// 將所需數據添加到 Model
		model.addAttribute("travelPlanId", planId);
		model.addAttribute("travelItineraryId", itineraryId);
		model.addAttribute("travelPlanDayId", travelPlanDayId);
		model.addAttribute("itineraryDates", itineraryDates);
		model.addAttribute("currentEditDate", currentEditDate);
		model.addAttribute("travelDayNumber", travelDayNumber);
		model.addAttribute("travelPlanDayDTO", travelPlanDayDTO); // 傳遞當前編輯日期

		// 這裡可以選擇預先載入第一天的 DailyItineraryFormDTO
		// 但前端會通過 AJAX 再次載入，所以這裡可以只傳一個空的 DTO 讓 Thymeleaf 綁定
		DailyItineraryFormDTO dailyItineraryFormDTO = new DailyItineraryFormDTO();
		// 如果希望頁面載入時就有資料，可以在這裡調用 service 載入資料
		// List<TravelPlanDayDTO> initialDailyItems =
		// travelPlanService.getDailyItemsForDate(itineraryId, currentEditDate);
		// dailyItineraryFormDTO.setDailyItems(initialDailyItems);
		model.addAttribute("dailyItineraryFormDTO", dailyItineraryFormDTO);

		return "admin/travelplans/form_step3_day_details"; // 返回您的模板名稱
	}

	@PostMapping("/save")
	public String saveDailyItineraryFormView(
	        @PathVariable Integer planId,
	        @PathVariable Integer itineraryId,
	        @ModelAttribute DailyItineraryFormDTO dailyItineraryFormDTO,
	        Model model,
	        RedirectAttributes redirectAttributes) {

	    TravelItinerary itinerary = travelItineraryService.getTravelItineraryEntityById(itineraryId)
	            .orElseThrow(() -> new IllegalArgumentException("找不到行程梯次"));

	    // ⛳ 將行程依照日期分組
	    Map<LocalDate, List<TravelPlanDayDTO>> groupedByDate = dailyItineraryFormDTO.getDailyItems().stream()
	            .collect(Collectors.groupingBy(TravelPlanDayDTO::getTraveltime));

	    try {
	        for (Map.Entry<LocalDate, List<TravelPlanDayDTO>> entry : groupedByDate.entrySet()) {
	            LocalDate date = entry.getKey();
	            List<TravelPlanDayDTO> itemsForDate = entry.getValue();

	            // 1️⃣ 檢查日期是否合法
	            if (date.isBefore(itinerary.getStartDate()) || date.isAfter(itinerary.getEndDate())) {
	                redirectAttributes.addFlashAttribute("errorMessage", "旅行日期 " + date + " 不在行程期間內！");
	                return "redirect:/admin/travelplans/" + planId + "/itinerary/" + itineraryId + "/days/new";
	            }

	            // 2️⃣ 查出當日已存在的最大序號
	            List<Integer> existingSequences = travelPlanDayService.findSequenceNumbersByItineraryIdAndDate(itineraryId, date);
	            int nextSequence = existingSequences.stream().mapToInt(Integer::intValue).max().orElse(0);

	            // 3️⃣ 每一筆補上正確序號與天數
	            int travelDayNumber = travelPlanDayService.calculateTravelDayNumber(itineraryId, date);
	            for (TravelPlanDayDTO item : itemsForDate) {
	                item.setTravelSequenceNumber(++nextSequence);
	                item.setTravelDayNumber(travelDayNumber);
	            }

	            // 4️⃣ 儲存
	            travelPlanDayService.saveDailyItems(itineraryId, date, itemsForDate);
	        }

	        redirectAttributes.addFlashAttribute("successMessage", "每日行程已成功儲存！");

	    } catch (Exception e) {
	        e.printStackTrace();
	        redirectAttributes.addFlashAttribute("errorMessage", "儲存失敗：" + e.getMessage());
	    }

	    // 💡 儲存完導回第一個日期（或你想導的那個）
	    LocalDate firstDate = groupedByDate.keySet().stream().sorted().findFirst().orElse(itinerary.getStartDate());
	    return "redirect:/admin/travelplans/" + planId + "/itinerary/" + itineraryId + "/days?date=" + firstDate;
	}





	// 預覽旅行計畫
	@GetMapping("/preview")
	public String previewTravelPlan(@PathVariable Integer planId, @PathVariable Integer itineraryId, Model model,
			RedirectAttributes redirectAttributes) {

		try {
			// 假設你有一個 service 方法可以包裝所有預覽資料
			TravelPlanPreviewDTO previewDTO = travelPlanService.getTravelPlanPreview(itineraryId);
			if (previewDTO == null) {
				redirectAttributes.addFlashAttribute("errorMessage", "找不到該行程的預覽資料");
				// 重定向回編輯頁面，因為這裡的 preview 是針對特定梯次的
				return "redirect:/admin/travelplans/" + planId + "/itinerary/" + itineraryId + "/days/edit";
			}

			int totalDays = travelPlanService.calculateTotalDays(previewDTO.getStartDate(), previewDTO.getEndDate());

			model.addAttribute("travelPlanPreview", previewDTO);
			model.addAttribute("totalTravelDays", totalDays);

			return "admin/travelplans/preview"; // 頁面路徑：resources/templates/admin/travelplans/preview.html
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "預覽失敗：" + e.getMessage());
			// 這裡的重定向可能需要調整，因為預覽是針對梯次層級的，所以應該留在梯次相關的頁面
			return "redirect:/admin/travelplans/" + planId + "/itinerary/" + itineraryId + "/days/edit";
		}
	}

	// 導向新增每日行程的表單頁面
	@GetMapping("/new")
	public String createNewDailyItinerary(
	        @PathVariable Integer planId,
	        @PathVariable Integer itineraryId,
	        @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
	        Model model) {

	    TravelItinerary itinerary = travelItineraryService.getTravelItineraryEntityById(itineraryId)
	            .orElseThrow(() -> new IllegalArgumentException("找不到行程梯次"));

	    List<LocalDate> itineraryDates = travelPlanService.generateItineraryDates(itinerary.getStartDate(),
	            itinerary.getEndDate());

	    // ✅ 若有帶日期就用該日期，否則預設第一天
	    LocalDate currentEditDate = (date != null) ? date : itinerary.getStartDate();

	    int travelDayNumber = travelPlanDayService.calculateTravelDayNumber(itineraryId, currentEditDate);

	    // 查該日已存在的最大序號
	    List<TravelPlanDay> existingDays = travelPlanDayService
	            .getDaysByItineraryIdAndDateSortedBySequence(itineraryId, currentEditDate);
	    int nextSequence = existingDays.stream()
	            .mapToInt(TravelPlanDay::getTravelSequenceNumber)
	            .max()
	            .orElse(0) + 1;

	    DailyItineraryFormDTO dailyItineraryFormDTO = new DailyItineraryFormDTO();
	    TravelPlanDayDTO emptyItem = new TravelPlanDayDTO();
	    emptyItem.setTraveltime(currentEditDate);
	    emptyItem.setTravelSequenceNumber(nextSequence);
	    dailyItineraryFormDTO.setDailyItems(List.of(emptyItem));

	    List<SceneryVO> allSceneriesList = travelPlanDayService.findAllScenery();
	    Map<Integer, String> allSceneriesMap = allSceneriesList.stream()
	            .collect(Collectors.toMap(SceneryVO::getSceneryId, SceneryVO::getSceneryName));

	    model.addAttribute("travelPlanId", planId);
	    model.addAttribute("travelItineraryId", itineraryId);
	    model.addAttribute("itineraryDates", itineraryDates);
	    model.addAttribute("currentEditDate", currentEditDate); // ✅ 傳給下拉選單
	    model.addAttribute("travelDayNumber", travelDayNumber);
	    model.addAttribute("dailyItineraryFormDTO", dailyItineraryFormDTO);
	    model.addAttribute("allSceneries", allSceneriesMap);

	    return "admin/travelplans/form_step3_day_details";
	}

	// 更新編輯單個每日行程
	@PostMapping("/update/{travelPlanDayId}")
	public String updateSingleTravelPlanDay(@PathVariable Integer planId, @PathVariable Integer itineraryId,
			@PathVariable Integer travelPlanDayId, @ModelAttribute TravelPlanDayDTO travelPlanDayDTO,
			RedirectAttributes redirectAttributes) {
		try {
			travelPlanDayDTO.setTravelPlanDayId(travelPlanDayId); // 確保有ID
			travelPlanDayService.updateTravelPlanDay(travelPlanDayDTO, itineraryId);
			redirectAttributes.addFlashAttribute("successMessage", "行程已成功更新！");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("errorMessage", "更新失敗：" + e.getMessage());
		}
		return "redirect:/admin/travelplans/" + planId + "/itinerary/" + itineraryId + "/days";
	}

	@GetMapping("/{travelPlanDayId}/delete")
	public String deleteTravelPlanDay(@PathVariable Integer planId, @PathVariable Integer itineraryId,
			@PathVariable Integer travelPlanDayId,
			@RequestParam(value = "date",required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
			RedirectAttributes redirectAttributes) {

		try {
			travelPlanDayService.deleteTravelPlanDayById(travelPlanDayId, itineraryId);
			redirectAttributes.addFlashAttribute("successMessage", "行程已成功刪除！");

		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("errorMessage", "刪除失敗：" + e.getMessage());
		}
		if (date == null) {
			TravelItinerary itinerary = travelPlanService.getTravelItineraryById(itineraryId)
			    .orElseThrow(() -> new IllegalArgumentException("找不到行程梯次"));
			date = itinerary.getStartDate();
		}
		return "redirect:/admin/travelplans/" + planId + "/itinerary/" + itineraryId + "/days?date=" + date;
	}
	
	@PostMapping("/delete-multiple")
	@ResponseBody
	public ResponseEntity<?> deleteMultipleDays(
			@PathVariable Integer planId,
	        @PathVariable Integer itineraryId,
	        @RequestBody List<Integer> dayIds) {
	    try {
	        travelPlanDayService.deleteMultipleByIds(itineraryId, dayIds);
	        return ResponseEntity.ok().build();
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("刪除失敗：" + e.getMessage());
	    }
	}
	
	



}