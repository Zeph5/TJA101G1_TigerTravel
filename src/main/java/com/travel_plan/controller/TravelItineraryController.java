//package com.travel_plan.controller;
//
////顯示新增/編輯旅行行程第二步的表單 (/admin/travelplans/{planId}/itinerary/new / {planId}/itinerary/{itineraryId}/edit)。
////
////處理新增/編輯旅行行程第二步的表單提交 (/admin/travelplans/{planId}/itinerary)。
////
////處理旅行行程的刪除等操作。
//
//@Controller
//@RequestMapping("/admin/travelplans/{planId}/itinerary") // 繼承 planId 的路徑
//@SessionAttributes({"currentTravelItineraryId"})
//public class TravelItineraryController {
//
//    @Autowired
//    private TravelItineraryService travelItineraryService; // 只注入 TravelItineraryService
//    @Autowired
//    private TravelPlanService travelPlanService; // 需要 TravelPlan 實體來建立關聯
//
//    // C. 第二步：編輯行程 (人數、價格等 - TravelItinerary)
//    // URL: /admin/travelplans/{planId}/itinerary/new (GET for form)
//    @GetMapping("/new")
//    public String showCreateItineraryForm(@PathVariable("planId") Integer planId, Model model, HttpSession session) {
//        // 驗證 planId 是否與 session 中保存的 currentTravelPlanId 一致
//        if (!planId.equals(session.getAttribute("currentTravelPlanId"))) {
//            return "redirect:/admin/travelplans/new"; // 重定向回第一步或錯誤頁面
//        }
//        model.addAttribute("travelItineraryDto", new TravelItineraryDto()); // <-- 使用 DTO
//        model.addAttribute("travelPlanId", planId); // 傳遞 PlanId 到表單
//        return "admin/travelplans/form_step2_itinerary_details";
//    }
//
//    // URL: /admin/travelplans/{planId}/itinerary (POST to save step 2)
//    @PostMapping
//    public String saveTravelItineraryStep2(@PathVariable("planId") Integer planId,
//                                          @Valid @ModelAttribute("travelItineraryDto") TravelItineraryDto dto, // <-- 使用 DTO
//                                          BindingResult bindingResult,
//                                          RedirectAttributes redirectAttributes,
//                                          HttpSession session) {
//        if (bindingResult.hasErrors()) {
//            model.addAttribute("travelPlanId", planId); // 錯誤時也要傳回 planId
//            return "admin/travelplans/form_step2_itinerary_details";
//        }
//
//        // 確保 TravelPlan 存在並取得實體
//        TravelPlan travelPlan = travelPlanService.getTravelPlanById(planId)
//                                    .orElseThrow(() -> new IllegalArgumentException("TravelPlan not found for ID: " + planId));
//
//        // <-- 呼叫 Service 處理 DTO 到 Entity 的轉換和保存 -->
//        TravelItinerary savedItinerary = travelItineraryService.createTravelItineraryFromDto(travelPlan, dto);
//
//        session.setAttribute("currentTravelItineraryId", savedItinerary.getTravelItineraryId());
//
//        redirectAttributes.addFlashAttribute("successMessage", "行程細節儲存成功，請繼續編輯每日行程。");
//        return "redirect:/admin/travelplans/" + planId + "/itinerary/" + savedItinerary.getTravelItineraryId() + "/days/new"; // 導向第三步
//    }
//    // ... (其他與 TravelItinerary 相關的方法)
//}