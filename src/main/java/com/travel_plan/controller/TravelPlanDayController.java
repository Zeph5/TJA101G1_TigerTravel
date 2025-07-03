//package com.travel_plan.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.support.SessionStatus;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import com.travel_plan.dto.TravelPlanDayDTO;
//import com.travel_plan.model.TravelItinerary;
//
//import com.travel_plan.service.TravelItineraryService;
//import com.travel_plan.service.TravelPlanDayService;
//
//
//import jakarta.servlet.http.HttpSession;
//import jakarta.validation.Valid;
//
////顯示新增/編輯每日行程第三步的表單 (/admin/travelplans/{planId}/itinerary/{itineraryId}/days/new / {planId}/itinerary/{itineraryId}/days/{dayId}/edit)。
////
////處理新增/編輯每日行程第三步的表單提交 (/admin/travelplans/{planId}/itinerary/{itineraryId}/days)。
////
////處理每日行程的刪除等操作。
//
//@Controller
//@RequestMapping("/admin/travelplans/{planId}/itinerary/{itineraryId}/days") // 繼承 planId 和 itineraryId 的路徑
//public class TravelPlanDayController {
//
//    @Autowired
//    private TravelPlanDayService travelPlanDayService; // 只注入 TravelPlanDayService
//    @Autowired
//    private TravelItineraryService travelItineraryService; // 需要 TravelItinerary 實體來建立關聯
//    
//     
//
//    // D. 第三步：編輯每日行程 (TravelPlanDay)
//    // URL: /admin/travelplans/{planId}/itinerary/{itineraryId}/days/new (GET for form)
//    
//    @GetMapping("/new")
//    public String showCreatePlanDayForm(@PathVariable("planId") Integer planId,
//                                        @PathVariable("itineraryId") Integer itineraryId,
//                                        Model model, HttpSession session) {
//        // 驗證 ID 是否一致 (略)
//        model.addAttribute("travelPlanDayDto", new TravelPlanDayDTO()); // <-- 使用 DTO (單個)
//        // 這裡可以準備一個 List<TravelPlanDayDto> 來編輯多個 daily plans
//        // model.addAttribute("travelPlanDayDtos", new ArrayList<TravelPlanDayDto>()); // 如果是列表形式
//
//        model.addAttribute("planId", planId);
//        model.addAttribute("itineraryId", itineraryId);
//        return "admin/travelplans/form_step3_day_details";
//    }
//
//    // URL: /admin/travelplans/{planId}/itinerary/{itineraryId}/days (POST to save step 3)
//    @PostMapping
//    public String saveTravelPlanDayStep3(@PathVariable("planId") Integer planId,
//                                         @PathVariable("itineraryId") Integer itineraryId,
//                                         @Valid @ModelAttribute("travelPlanDayDto") TravelPlanDayDTO dto, // <-- 使用 DTO
//                                         BindingResult bindingResult,
//                                         RedirectAttributes redirectAttributes,
//                                         SessionStatus status, // 結束多步驟流程，清理 SessionAttributes
//                                         HttpSession session
//                                         ,Model model) { // 移除 HttpSession 裡的 attributes
//        if (bindingResult.hasErrors()) {
//            model.addAttribute("planId", planId);
//            model.addAttribute("itineraryId", itineraryId);
//            return "admin/travelplans/form_step3_day_details";
//        }
//
//        // 確保 TravelItinerary 存在並取得實體
//        TravelItinerary travelItinerary = travelItineraryService.getTravelItineraryById(itineraryId)
//                                            .orElseThrow(() -> new IllegalArgumentException("TravelItinerary not found for ID: " + itineraryId));
//
//        // <-- 呼叫 Service 處理 DTO 到 Entity 的轉換和保存 -->
//        travelPlanDayService.createTravelPlanDayFromDto(travelItinerary, dto); // 或 List<TravelPlanDayDto>
//
//        // 多步驟表單結束，清除 Session 狀態和 Session attributes
//        status.setComplete(); // 清除 @SessionAttributes
//        session.removeAttribute("currentTravelPlanId"); // 清除 HttpSession 裡手動加的
//        session.removeAttribute("currentTravelItineraryId");
//
//        redirectAttributes.addFlashAttribute("successMessage", "每日行程儲存成功！旅行計畫已完成編輯。");
//        return "redirect:/admin/travelplans"; // 導向總列表
//    }
//    // ... (其他與 TravelPlanDay 相關的方法)
//}