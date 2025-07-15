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
    public TravelItineraryController(TravelItineraryService travelItineraryService,
            TravelPlanService travelPlanService,TravelPlanDayService travelPlanDayService) {
        this.travelItineraryService = travelItineraryService;
        this.travelPlanService = travelPlanService;
        this.travelPlanDayService = travelPlanDayService; // 初始化每日行程服務
    }
    

    // 顯示新增旅行行程第二步的表單
    @GetMapping // 映射到 Controller 的根路徑，但必須接收 planId
    public String listItinerariesForTravelPlan(@PathVariable("planId") Integer planId, Model model) {
        // 確保 TravelPlan 存在並取得基本資訊
        TravelPlan travelPlan = travelPlanService.getTravelPlanEntityById(planId)
                .orElseThrow(() -> new IllegalArgumentException("找不到 ID 為 " + planId + " 的旅行計畫。"));

        // 從 Service 獲取特定 TravelPlan 下的所有 TravelItinerary 梯次
        // 注意：這裡應該呼叫類似 getItinerariesByTravelPlanId(planId) 的服務方法
        List<TravelItinerary> itineraries = travelItineraryService.getItinerariesByTravelPlanId(planId);
        System.out.println("目前查詢到的行程梯次數量為: " + itineraries.size()); // <--- 加這行
        model.addAttribute("itineraries", itineraries);

        model.addAttribute("travelPlanId", planId); // 傳遞 planId
        model.addAttribute("travelPlanTitle", travelPlan.getTravelTitle()); // 傳遞計畫名稱給前端顯示

        return "admin/travelplans/listItinerary"; // 返回顯示行程梯次列表的視圖
    }
    
    @GetMapping("/add")
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
                                @ModelAttribute("travelItineraryDTO") @Valid TravelItineraryDTO travelItineraryDto,
                                BindingResult result,
                                Model model,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        
        if (!planId.equals(travelItineraryDto.getTravelPlanId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "提交的計畫 ID 與 URL 不符，請檢查。");
            return "redirect:/admin/travelplans"; // 重定向回計畫列表或錯誤頁面
        }

        // 如果驗證失敗，則返回錯誤訊息並顯示錯誤
        if (result.hasErrors()) {
            // 為了重新渲染表單時能正確顯示計畫名稱
            travelPlanService.getTravelPlanEntityById(planId).ifPresent(plan -> {
                model.addAttribute("travelPlanTitle", plan.getTravelTitle());
            });
            model.addAttribute("travelPlanId", planId); // 再次傳遞 planId
            model.addAttribute("errorMessage", "資料驗證失敗，請檢查輸入。");
            return "admin/travelplans/updateItinerary"; // 返回當前頁面顯示錯誤
        }

        try {
            // 【修正】調用 TravelItineraryService 保存或更新 TravelItineraryDTO
            TravelItinerary savedItinerary = travelItineraryService.saveTravelItineraryFromDto(travelItineraryDto);

            // 設定 session 屬性
            session.setAttribute("currentTravelItineraryId", savedItinerary.getTravelItineraryId());

            // 添加成功訊息
            redirectAttributes.addFlashAttribute("successMessage", "行程梯次資訊保存成功！現在請編輯每日行程細節。");

            // 重定向到下一步驟的行程細節編輯頁面 (使用 TravelPlanDayController 的 overview 頁面)
            // 請確保 TravelPlanDayController 有 /admin/travelplans/{planId}/itinerary/{itineraryId}/overview 這個端點
            return "redirect:/admin/travelplans/" + planId + "/itinerary";

        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", "儲存行程梯次失敗: " + e.getMessage());
            // 為了重新渲染表單時能正確顯示計畫名稱
            travelPlanService.getTravelPlanEntityById(planId).ifPresent(plan -> {
                model.addAttribute("travelPlanTitle", plan.getTravelTitle());
            });
            model.addAttribute("travelPlanId", planId);
            return "admin/travelplans/updateItinerary"; // 返回當前頁面顯示錯誤
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "儲存行程梯次失敗: 發生未知錯誤。");
            return "redirect:/admin/travelplans/list"; // 或者其他適當的錯誤處理
        }
    }
    

    // 編輯現有梯次基本資訊的入口點
    // 路徑: GET /admin/travelplans/{planId}/itinerary/{itineraryId}/edit
    @GetMapping("/{itineraryId}/edit")
    public String showEditItineraryForm(@PathVariable("planId") Integer planId,
                                        @PathVariable("itineraryId") Integer itineraryId,
                                        Model model,
                                        HttpSession session) {

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
        return "admin/travelplans/updateItinerary";
    }
}