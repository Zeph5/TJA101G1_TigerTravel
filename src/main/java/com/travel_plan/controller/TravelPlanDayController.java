package com.travel_plan.controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
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

	// 透過 AJAX 獲取特定日期的每日行程資料
	// URL: GET
	// /admin/travelplans/{planId}/itinerary/{itineraryId}/days/{dateString}
	@GetMapping(value = "/{dateString}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<DailyItineraryFormDTO> getDailyItineraryByDate(@PathVariable Integer planId,
			@PathVariable Integer itineraryId, @PathVariable String dateString) {
		try {
			TravelItinerary travelItinerary = travelPlanService.getTravelItineraryById(itineraryId)
					.orElseThrow(() -> new IllegalArgumentException("Invalid itineraryId: " + itineraryId));
			if (!travelItinerary.getTravelPlan().getTravelPlanId().equals(planId)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}

			LocalDate date = LocalDate.parse(dateString);
			long dayDifference = ChronoUnit.DAYS.between(travelItinerary.getStartDate(), date);
			Integer travelDayNumber = (int) dayDifference + 1;

			DailyItineraryFormDTO dto = travelPlanService.getDailyItineraryFormDTO(itineraryId, date, travelDayNumber);
			return ResponseEntity.ok(dto);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().build();
		}
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

		return "admin/travelplans/updateTravelPlanDay"; // 返回您的模板名稱
	}

	// "新增"的表單提交
	// URL: POST /admin/travelplans/{planId}/itinerary/{itineraryId}/days/save
	@PostMapping("/save")
	public String saveDailyItineraryFormView(@PathVariable Integer planId, @PathVariable Integer itineraryId,
			@ModelAttribute DailyItineraryFormDTO dailyItineraryFormDTO, Model model,
			RedirectAttributes redirectAttributes) {
		System.out.println("🚨 travelPlanDayService 實例是：" + travelPlanDayService.getClass().getName());
		TravelItinerary itinerary = travelItineraryService.getTravelItineraryEntityById(itineraryId)
				.orElseThrow(() -> new IllegalArgumentException("找不到行程梯次"));
		model.addAttribute("startDate", itinerary.getStartDate());
		model.addAttribute("endDate", itinerary.getEndDate());

		try {
			LocalDate date = dailyItineraryFormDTO.getTraveltime(); // 請確定 DTO 有此欄位與 getter/setter
			LocalDate startDate = itinerary.getStartDate();
			LocalDate endDate = itinerary.getEndDate();
			
			List<Integer> inputSequences = dailyItineraryFormDTO.getDailyItems()
				    .stream()
				    .map(TravelPlanDayDTO::getTravelSequenceNumber)
				    .filter(Objects::nonNull)
				    .collect(Collectors.toList());
			// 檢查表單內行程順序是否有重複
			Set<Integer> inputSequenceSet = new HashSet<>(inputSequences);
			if (inputSequenceSet.size() < inputSequences.size()) {
			    Set<Integer> duplicatesInForm = inputSequences.stream()
			        .filter(i -> Collections.frequency(inputSequences, i) > 1)
			        .collect(Collectors.toSet());
			    redirectAttributes.addFlashAttribute("errorMessage", "表單內有重複的行程順序！");
			    redirectAttributes.addFlashAttribute("duplicateSequenceSet", duplicatesInForm);
			    return "redirect:/admin/travelplans/" + planId + "/itinerary/" + itineraryId + "/days?date=" + date;
			}
			
			// 檢查資料庫中是否有重複的行程順序
			List<Integer> existingSequences = travelPlanDayService.findSequenceNumbersByItineraryIdAndDate(itineraryId, date);
			boolean hasDuplicateWithDatabase = inputSequences.stream()
				    .anyMatch(existingSequences::contains);

				if (hasDuplicateWithDatabase) {
					Set<Integer> duplicatedSequenceNumbers = inputSequences.stream()
					        .filter(existingSequences::contains)
					        .collect(Collectors.toSet());
				    redirectAttributes.addFlashAttribute("errorMessage", "行程順序與該日已存在的行程重複！");
				    redirectAttributes.addFlashAttribute("duplicateSequenceSet", duplicatedSequenceNumbers);
				    return "redirect:/admin/travelplans/" + planId + "/itinerary/" + itineraryId + "/days?date=" + date;
				}
			
			// 檢查日期是否在行程期間內
			if (date.isBefore(startDate) || date.isAfter(endDate)) {
				redirectAttributes.addFlashAttribute("errorMessage", "旅行日期不在行程期間內！");
				return "redirect:/admin/travelplans/" + planId + "/itinerary/" + itineraryId + "/days/new";
			}
			int travelDayNumber = travelPlanDayService.calculateTravelDayNumber(itineraryId, date);
			for (TravelPlanDayDTO item : dailyItineraryFormDTO.getDailyItems()) {
				item.setTravelDayNumber(travelDayNumber);
			}
			
			travelPlanDayService.saveDailyItems(itineraryId, date, dailyItineraryFormDTO.getDailyItems());
			redirectAttributes.addFlashAttribute("successMessage", "當天行程已成功儲存！");
			
			
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			return "redirect:/admin/travelplans/" + planId + "/itinerary/" + itineraryId + "/days?date="
					+ dailyItineraryFormDTO.getTraveltime();
		} catch (Exception e) {
			e.printStackTrace(); // <== 把錯誤印出來
			redirectAttributes.addFlashAttribute("errorMessage", "儲存失敗：" + e.getMessage());
		}

		// 保存後重新導向回編輯頁，並帶上日期參數
		return "redirect:/admin/travelplans/" + planId + "/itinerary/" + itineraryId + "/days";
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
	public String createNewDailyItinerary(@PathVariable Integer planId, @PathVariable Integer itineraryId,
			Model model) {
		TravelItinerary itinerary = travelItineraryService.getTravelItineraryEntityById(itineraryId)
				.orElseThrow(() -> new IllegalArgumentException("找不到行程梯次"));
		model.addAttribute("startDate", itinerary.getStartDate());
		model.addAttribute("endDate", itinerary.getEndDate());

		List<LocalDate> itineraryDates = travelPlanService.generateItineraryDates(itinerary.getStartDate(),
				itinerary.getEndDate());
		LocalDate currentEditDate = itinerary.getStartDate(); // 預設為第一天
		int travelDayNumber = travelPlanDayService.calculateTravelDayNumber(itineraryId, currentEditDate);
		
		// 空表單
		DailyItineraryFormDTO dailyItineraryFormDTO = new DailyItineraryFormDTO();
		 TravelPlanDayDTO emptyItem = new TravelPlanDayDTO();
		    emptyItem.setTraveltime(currentEditDate);
		    dailyItineraryFormDTO.setDailyItems(List.of(emptyItem));
		
		List<SceneryVO> allSceneriesList = travelPlanDayService.findAllScenery();
		Map<Integer, String> allSceneriesMap = allSceneriesList.stream()
				.collect(Collectors.toMap(SceneryVO::getSceneryId, SceneryVO::getSceneryName));

		model.addAttribute("travelPlanId", planId);
		model.addAttribute("travelItineraryId", itineraryId);
		model.addAttribute("itineraryDates", itineraryDates);
		model.addAttribute("currentEditDate", currentEditDate);
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
			@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
			RedirectAttributes redirectAttributes) {

		try {
			travelPlanDayService.deleteTravelPlanDayById(travelPlanDayId, itineraryId);
			redirectAttributes.addFlashAttribute("successMessage", "行程已成功刪除！");

		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("errorMessage", "刪除失敗：" + e.getMessage());
		}
		return "redirect:/admin/travelplans/" + planId + "/itinerary/" + itineraryId + "/days?date=" + date;
	}

}