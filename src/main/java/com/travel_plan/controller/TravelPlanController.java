package com.travel_plan.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ssl.SslProperties.Bundles.Watch.File;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import com.travel_plan.repository.TravelPlanRepository;
import com.travel_plan.service.ImageService;
import com.travel_plan.service.TravelPlanService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import net.coobird.thumbnailator.Thumbnails;

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
	private final TravelPlanRepository travelPlanRepository;
	private final ImageService imageService;

	@Autowired
	public TravelPlanController(TravelPlanService travelPlanService,
			TravelPlanRepository travelPlanRepository,
			ImageService imageService) {
		this.travelPlanService = travelPlanService;
		this.travelPlanRepository = travelPlanRepository;
		this.imageService = imageService;
	}

	// 這是 顯示所有旅行計畫列表 的頁面入口。
	@GetMapping
	public String listTravelPlans(Model model, 
	                              @RequestParam(defaultValue = "0") int page,
	                              @RequestParam(defaultValue = "9") int size) {
	    Page<TravelPlan> travelPlanPage = travelPlanService.getTravelPlans(PageRequest.of(page, size));

	    model.addAttribute("travelPlanPage", travelPlanPage); // 分頁物件
	    model.addAttribute("currentPage", page);              // 當前頁數
	    model.addAttribute("pageSize", size);                 // 每頁大小
	    model.addAttribute("travelPlans", travelPlanPage.getContent()); // ✅ 只取這一頁的內容

	    return "admin/travelplans/list";
	}


	@GetMapping("/new") // 在 list.html 頁面點擊「新增計畫」按鈕/連結	
	public String showNewTPForm(Model model) {
		model.addAttribute("travelPlanCreationDto", new TravelPlanCreationDTO()); // 假設有一個 TravelPlan 類別
		return "admin/travelplans/form_step1_plan_details";
	}
	
	
	// 處理新增/編輯旅行計畫第一步的表單提交
	@PostMapping("/save")
	public String createTravelPlan(@Valid @ModelAttribute("travelPlanCreationDto") TravelPlanCreationDTO dto,
	                               BindingResult result,
	                               @RequestParam(value = "bannerImage", required = false) MultipartFile bannerImage,
	                               RedirectAttributes redirectAttributes,
	                               HttpSession session,
	                               Model model) {

	    // 表單驗證失敗時直接回表單
	    if (result.hasErrors()) {
	        model.addAttribute("travelPlanCreationDto", dto);
	        model.addAttribute("errorMessage", "資料驗證失敗，請檢查輸入。");
	        return "admin/travelplans/form_step1_plan_details";
	    }

	    // 圖片必填驗證（新建時）
	    if ((bannerImage == null || bannerImage.isEmpty())
	        && (dto.getTravelPlanId() == null || dto.getTravelPlanId() == 0)
	        && (dto.getTravelPlanBannerUrl() == null || dto.getTravelPlanBannerUrl().isBlank())) {
	        result.rejectValue("bannerImage", "error.bannerImage", "請上傳圖片");
	        model.addAttribute("errorMessage", "請上傳圖片");
	        return "admin/travelplans/form_step1_plan_details";
	    }

	    TravelPlan savedPlan;

	    if (dto.getTravelPlanId() != null && dto.getTravelPlanId() != 0) {
	        savedPlan = travelPlanService.updateTravelPlan(dto.getTravelPlanId(), dto, bannerImage);
	        redirectAttributes.addFlashAttribute("successMessage", "計畫已更新，請繼續編輯行程細節");
	    } else {
	        savedPlan = travelPlanService.createTravelPlanFromDto(dto, bannerImage);
	        redirectAttributes.addFlashAttribute("successMessage", "計畫新增成功，請繼續編輯行程細節");
	    }

	    session.setAttribute("currentTravelPlanId", savedPlan.getTravelPlanId());
	    session.removeAttribute("currentTravelItineraryId");
	    
	    return "redirect:/admin/travelplans";
	}


	// 顯示編輯旅行計畫的表單
	@GetMapping("/{id}/edit")
	public String editTravelPlan(@PathVariable("id") Integer id, Model model, HttpSession session) {

		// 確保計畫 ID 存在並取得實體
		// 如果不存在，則拋出異常或返回錯誤頁面
		TravelPlan existingPlan = travelPlanService.getTravelPlanEntityById(id)
				.orElseThrow(() -> new IllegalArgumentException("TravelPlan not found"));

		// 將 Entity 轉換為 DTO 填充表單 (如果需要編輯圖片，也要將當前 URL 傳遞給前端顯示)
		TravelPlanCreationDTO dto = travelPlanService.convertToCreationDto(existingPlan);

		model.addAttribute("travelPlanCreationDto", dto); // 將 DTO 傳遞到前端表單
		model.addAttribute("travelPlanBannerUrl", existingPlan.getBannerImageUrl()); // 傳遞當前計畫的橫幅圖片 URL
		session.setAttribute("currentTravelPlanId", id); // 將當前計畫 ID 儲存到 Session 中，供後續使用
		session.removeAttribute("currentTravelItineraryId"); // 清除可能存在的行程 ID，因為編輯計畫時不需要行程 ID

		model.addAttribute("message", "正在編輯現有旅行資訊");

		return "admin/travelplans/form_step1_plan_details";
	}
	@PostMapping("/{id}/delete")
	public String deleteTravelPlan(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
	    try {
	        travelPlanService.deleteById(id);  // 你需要在 service 裡加這個方法
	        redirectAttributes.addFlashAttribute("successMessage", "刪除成功！");
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("errorMessage", "刪除失敗：" + e.getMessage());
	    }
	    return "redirect:/admin/travelplans";
	}
	@PostMapping("/batch-delete")
	public String batchDeleteTravelPlans(@RequestParam("planIds") List<Integer> planIds,
	                                     RedirectAttributes redirectAttributes) {
	    if (planIds == null || planIds.isEmpty()) {
	        redirectAttributes.addFlashAttribute("errorMessage", "請先選取要刪除的旅行計畫");
	        return "redirect:/admin/travelplans";
	    }

	    try {
	        travelPlanService.deleteAllByIds(planIds);  // 你需要加一個這個 service 方法
	        redirectAttributes.addFlashAttribute("successMessage", "成功刪除 " + planIds.size() + " 筆旅行計畫！");
	    } catch (Exception e) {
	        redirectAttributes.addFlashAttribute("errorMessage", "刪除失敗：" + e.getMessage());
	    }

	    return "redirect:/admin/travelplans";
	}

	@PostMapping("/upload")
	public String handleUpload(@ModelAttribute TravelPlanCreationDTO dto) {
	    MultipartFile file = dto.getBannerImage();

	    if (file != null && !file.isEmpty()) {
	        try {
	        	String uploadDir = "C:/TJA101-WebApp/images/";
	            String newFileName = UUID.randomUUID().toString(); // 避免重複檔名
	            String imageUrl = imageService.saveAndResizeImage(file, uploadDir, newFileName);

	            // 設定圖片 URL 存到資料庫
	            TravelPlan plan = new TravelPlan();
	            plan.setTravelTitle(dto.getTravelTitle());
	            plan.setTravelPlanDescription(dto.getTravelPlanDescription());
	            plan.setTravelPlanBannerUrl(imageUrl);
	            travelPlanRepository.save(plan);

	        } catch (Exception e) {
	            e.printStackTrace();
	            return "upload-failed";
	        }
	    }

	    return "redirect:/admin/travelplans";
	}
	
}

