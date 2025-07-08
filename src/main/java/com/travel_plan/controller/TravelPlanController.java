package com.travel_plan.controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
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

import com.travel_plan.dto.DailyItineraryFormDTO;
import com.travel_plan.dto.TravelItineraryDTO;
import com.travel_plan.dto.TravelPlanCreationDTO;
import com.travel_plan.dto.TravelPlanDayDTO;
import com.travel_plan.dto.TravelPlanPreviewDTO;
import com.travel_plan.model.TravelItinerary;
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
		return "redirect:/admin/travelplans/" + savedPlan.getTravelPlanId() + "/itineraries/add";
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
	@GetMapping("/{planId}/itinerary/{itineraryId}/days/{date}")
	public ResponseEntity<DailyItineraryFormDTO> getDailyItinerary(@PathVariable Integer planId,
	                                                               @PathVariable Integer itineraryId,
	                                                               @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
	      // 根據 planId, itineraryId 和 date 從資料庫獲取每日行程項目
	       List<TravelPlanDayDTO> dailyItems = travelPlanService.getDailyItemsForDate(itineraryId, date);
	       DailyItineraryFormDTO dto = new DailyItineraryFormDTO();
	       dto.setDailyItems(dailyItems);
	       // 修正這裡，直接呼叫 Service 中的方法，傳入 itineraryId 和 date
	       dto.setTravelDayNumber(travelPlanService.calculateTravelDayNumber(itineraryId, date)); // <-- 修正這裡
	       return ResponseEntity.ok(dto);
	  }
	

	@PostMapping("/{planId}/itinerary/{itineraryId}/days/save")
	  public String saveDailyItinerary(@PathVariable Integer planId,
	                                   @PathVariable Integer itineraryId,
	                                   @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
	                                   @ModelAttribute("dailyItineraryFormDTO") DailyItineraryFormDTO dailyItineraryFormDTO,
	                                   RedirectAttributes redirectAttributes) {
	      try {
	          // 儲存 dailyItineraryFormDTO.getDailyItems() 到資料庫
	    	  travelPlanService.saveDailyItems(itineraryId, date, dailyItineraryFormDTO.getDailyItems());
	          redirectAttributes.addFlashAttribute("successMessage", "當天行程儲存成功！");
	      } catch (Exception e) {
	          redirectAttributes.addFlashAttribute("errorMessage", "儲存當天行程時發生錯誤：" + e.getMessage());
	      }
	      // 儲存後重定向回當前編輯的日期頁面
	      return "redirect:/admin/travelplans/" + planId + "/itinerary/" + itineraryId + "/days/" + date;
	  }
	@GetMapping("/{planId}/preview")
	public String previewTravelPlan(@PathVariable("planId") Integer planId, Model model) {
	    // 獲取完整的旅行計畫數據 (包含所有行程天數和景點)
	    // 這需要 TravelPlanService 提供一個方法來獲取完整的 DTO
	    // 將這裡的類型從 TravelPlanCreationDTO 改為 TravelPlanPreviewDTO
	    TravelPlanPreviewDTO travelPlanPreviewDTO = travelPlanService.getFullTravelPlanDetails(planId); // <-- 修正這裡的類型

	    // 將 Model Attribute 的名稱改為 "travelPlanPreview"，與前端模板預期的名稱一致
	    model.addAttribute("travelPlanPreview", travelPlanPreviewDTO); // <-- 修正這裡的名稱

	    // 計算總天數，從 TravelPlanPreviewDTO 中獲取梯次日期
	    if (travelPlanPreviewDTO.getStartDate() != null && travelPlanPreviewDTO.getEndDate() != null) {
	        long totalDays = ChronoUnit.DAYS.between(travelPlanPreviewDTO.getStartDate(), travelPlanPreviewDTO.getEndDate()) + 1;
	        model.addAttribute("totalTravelDays", totalDays); // 將總天數傳遞給 Model
	    } else {
	        model.addAttribute("totalTravelDays", 0);
	    }

	    return "admin/travelplans/preview_full_plan"; // 一個新的 Thymeleaf 模板來顯示預覽
	}
	@GetMapping("/{planId}/itineraries/add")
	public String showAddItineraryForm(@PathVariable Integer planId, Model model, HttpSession session) {
	    // 從 session 取得 travelPlanId，如果直接從 PathVariable 拿到也行
	    // Integer currentPlanId = (Integer) session.getAttribute("currentTravelPlanId");
	    // if (currentPlanId == null || !currentPlanId.equals(planId)) {
	    //     // 處理錯誤，或者重新導向到第一個表單
	    //     return "redirect:/admin/travelplans/new";
	    // }

	    TravelItineraryDTO dto = new TravelItineraryDTO();
	    dto.setTravelPlanId(planId); // 將 TravelPlan ID 關聯到梯次 DTO
	    model.addAttribute("travelItineraryDTO", dto);

	    // 為了讓使用者知道他們正在為哪個計畫新增梯次，可以傳遞計畫名稱
	    travelPlanService.getTravelPlanEntityById(planId).ifPresent(plan -> {
	        model.addAttribute("travelPlanTitle", plan.getTravelTitle());
	    });

	    return "admin/travelplans/form_step2_itinerary_details"; // 這個是您新設計的第二個表單模板
	}
	
	@PostMapping("/itineraries/save")
	public String saveItinerary(@Valid @ModelAttribute("travelItineraryDTO") TravelItineraryDTO dto,
	                            BindingResult result,
	                            RedirectAttributes redirectAttributes,
	                            HttpSession session,
	                            Model model) {
	    if (result.hasErrors()) {
	        model.addAttribute("travelItineraryDTO", dto);
	        // 重新傳遞 travelPlanTitle，避免模板顯示錯誤
	        travelPlanService.getTravelPlanEntityById(dto.getTravelPlanId()).ifPresent(plan -> {
	            model.addAttribute("travelPlanTitle", plan.getTravelTitle());
	        });
	        model.addAttribute("errorMessage", "梯次資料驗證失敗，請檢查輸入。");
	        return "admin/travelplans/form_step2_itinerary_details"; // 返回第二個表單頁面
	    }

	    try {
	        TravelItinerary savedItinerary = travelPlanService.saveTravelItineraryFromDto(dto); // 假設 Service 有此方法
	        redirectAttributes.addFlashAttribute("successMessage", "行程梯次資訊保存成功！現在請編輯每日行程細節。");

	        // 保存梯次成功後，重導向到每日行程編輯頁面
	        // 您可能需要根據實際的 itineraryId 和日期來決定重導向 URL
	        // 這裡我假設預設跳轉到第一天的編輯頁面
	        session.setAttribute("currentTravelItineraryId", savedItinerary.getTravelItineraryId());
	        LocalDate firstDay = savedItinerary.getStartDate(); // 獲取梯次的第一天
	        return "redirect:/admin/travelplans/" + savedItinerary.getTravelPlan().getTravelPlanId() +
	               "/itinerary/" + savedItinerary.getTravelItineraryId() +
	               "/days/" + firstDay.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE);

	    } catch (IllegalArgumentException e) {
	        result.rejectValue(null, "error.itinerary", e.getMessage()); // 廣泛錯誤訊息
	        model.addAttribute("errorMessage", e.getMessage());
	        // 重新傳遞 travelPlanTitle
	        travelPlanService.getTravelPlanEntityById(dto.getTravelPlanId()).ifPresent(plan -> {
	            model.addAttribute("travelPlanTitle", plan.getTravelTitle());
	        });
	        return "admin/travelplans/form_step2_itinerary_details";
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("errorMessage", "保存行程梯次失敗: " + e.getMessage());
	        return "redirect:/admin/travelplans/list"; // 或其他適當的錯誤處理
	    }
	}	
	}

