package com.member.controller;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;



import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.member.model.MemberTicketOrderRepository;
import com.member.model.TourOrderVO;
import com.member.model.TouristVO;
import com.member.model.memVO;
import com.member.model.memberTour.MemTravelPlanDayRepository;
import com.member.security.MemberUserDetails;
import com.member.service.MemberTicketOrderReceiptService;
import com.member.service.MemberTicketOrderService;
import com.member.service.TourOrderService;
import com.member.service.TouristService;
import com.member.service.travel.MemberTravelPlanService;
import com.ticket.model.Ticket;
import com.ticket.model.TicketOrder;
import com.ticket.model.TicketOrderReceipt;
import com.ticket.repository.TicketRepository;
import com.travel_plan.model.TravelItinerary;
import com.travel_plan.model.TravelPlan;
import com.travel_plan.model.TravelPlanDay;
import com.travel_plan.service.TravelItineraryService;
import com.travel_plan.service.TravelPlanService;

@Controller
@RequestMapping("/member")
public class MemberTourOrderController {
	
	private final MemberTicketOrderService ticketOrderSvc;
	private final TourOrderService tourOrderSvc;
	private final MemberTicketOrderReceiptService receiptService;
	private final TicketRepository ticketRepo;
	private final MemTravelPlanDayRepository travelPlanDayRepo;
	private final TravelPlanService travelPlanSvc;
	private final MemberTravelPlanService memberTravelPlanSvc;
	private final TouristService touristSvc;
	private final TravelItineraryService travelItinerarySvc;
	
	public MemberTourOrderController(MemberTicketOrderService ticketOrderSvc,
										TourOrderService tourOrderSvc,
										MemberTicketOrderReceiptService receiptService,
										TicketRepository ticketRepo,
										MemTravelPlanDayRepository travelPlanDayRepo,
										TravelPlanService travelPlanSvc,
										MemberTravelPlanService memberTravelPlanSvc,
										TouristService touristSvc,
										TravelItineraryService travelItinerarySvc) {
		this.ticketOrderSvc = ticketOrderSvc;
		this.tourOrderSvc = tourOrderSvc;
		this.receiptService = receiptService;
		this.ticketRepo = ticketRepo;
		this.travelPlanDayRepo = travelPlanDayRepo;
		this.travelPlanSvc = travelPlanSvc;
		this.memberTravelPlanSvc = memberTravelPlanSvc;
		this.touristSvc = touristSvc;
		this.travelItinerarySvc = travelItinerarySvc;
	}
	
	
	//顯示ticket 與 tour訂單
	@GetMapping("/orders")
	public String showAllOrders(@AuthenticationPrincipal MemberUserDetails loginUser, Model model) {
	    memVO member = loginUser.getMember();
	    Integer memberId = member.getMemberId();

	    List<TicketOrder> ticketOrders = ticketOrderSvc.getOrdersByMemberId(memberId);
	    List<TourOrderVO> tourOrders = tourOrderSvc.findByMember(member);

	    // ✅ 補上 map 宣告
	    Map<Integer, List<TicketOrderReceipt>> receiptMap = new HashMap<>();
	    Map<Integer, Ticket> ticketMap = new HashMap<>();
	    Map<Integer, String> receiptImageMap = new HashMap<>();

	    for (TicketOrder order : ticketOrders) {
	        System.out.println("🧾 orderId: " + order.getTicketOrderId());
	        List<TicketOrderReceipt> receipts = receiptService.getReceiptsByOrder(order);

	        for (TicketOrderReceipt receipt : receipts) {
	            Integer rid = receipt.getTicketOrderReceiptId();
	            Integer tid = receipt.getTicketId();

	            System.out.println(" - receiptId: " + rid + ", ticketId: " + tid);

	            if (tid == null) {
	                System.out.println("   ⚠️ ticketId 為 null，無法查票券");
	                continue;
	            }

	            ticketRepo.findById(tid).ifPresentOrElse(ticket -> {
	                System.out.println("   ✅ 票券名稱: " + ticket.getTicketName());

	                ticketMap.put(rid, ticket); // ✅ 重點：用 receiptId 當 key

	                // ✅ 圖片也一併加入
	                byte[] img = ticket.getTicketImage();
	                if (img != null && img.length > 0) {
	                    String base64 = Base64.getEncoder().encodeToString(img);
	                    receiptImageMap.put(rid, base64); // ✅ 一樣用 receiptId 當 key
	                    
	                }

	            }, () -> {
	                System.out.println("   ❌ 找不到 ticketId: " + tid);
	            });
	        }

	        receiptMap.put(order.getTicketOrderId(), receipts);
	        
	    }


	    // ✅ Lazy load 初始化，避免 Thymeleaf render 時炸掉
	    for (TourOrderVO tour : tourOrders) {
	        if (tour.getTravelItinerary() != null && tour.getTravelItinerary().getTravelPlan() != null) {
	            tour.getTravelItinerary().getTravelPlan().getTravelTitle();
	        }
	    }
	    System.out.println("🔍 共有 " + ticketOrders.size() + " 筆票券訂單");
	    System.out.println("🎯 ticketMap keys: " + ticketMap.keySet());

	    System.out.println("📦 ticketMap 全部內容：");
	    ticketMap.forEach((k, v) -> {
	        System.out.println(" - receiptId: " + k + ", ticketName: " + v.getTicketName());
	    });
	    

	    // ✅ 將所有資料送出給前端 Thymeleaf
	    model.addAttribute("ticketOrders", ticketOrders);
	    model.addAttribute("tourOrders", tourOrders);
	    model.addAttribute("receiptMap", receiptMap);
	    model.addAttribute("ticketMap", ticketMap);
	    model.addAttribute("receiptImageMap", receiptImageMap);

	    System.out.println("✅ 已成功準備 orders.html 畫面！");
	    return "member/orders";
	}
	
	//點選tour的詳細訂單
	@GetMapping("/tour-order/detail/{id}")
	public String showTourOrderDetail(@PathVariable("id") Integer orderId, 
									@AuthenticationPrincipal MemberUserDetails loginUser, 
									Model model) {
		memVO member = loginUser.getMember();
		
		//查詢訂單 (包含 member 檢查，避免亂查別人的)
		Optional<TourOrderVO> optional = tourOrderSvc.findById(orderId);
		if(optional.isEmpty() || !optional.get().getMember().getMemberId().equals(member.getMemberId())) {
			return "redirect:/member/orders"; //無權限 或 找不到就導回訂單列表
		}
		
		TourOrderVO order = optional.get();
		
		
		TravelPlan plan = order.getTravelItinerary().getTravelPlan();
		plan.getTravelTitle();
		Integer itineraryId = order.getTravelItinerary().getTravelItineraryId();

		List<TravelPlanDay> dayList = travelPlanDayRepo.findByTravelItinerary_TravelItineraryId(itineraryId);
		dayList.sort(Comparator.comparing(TravelPlanDay::getTravelDayNumber));
		
		System.out.println("📅 多天行程數量：" + dayList.size());
		for (TravelPlanDay day : dayList) {
		    System.out.println("Day " + day.getTravelDayNumber() + ": " + day.getTraveltime());
		}


		model.addAttribute("planTitle", order.getTravelItinerary().getTravelPlan().getTravelTitle());
		model.addAttribute("order", order);
		model.addAttribute("travelPlanDays", dayList);
		
		return "member/member-tour-order-detail";

	}
	
	@GetMapping("/travel/list")
	public String showPagedPlans(@RequestParam(defaultValue = "0") int page,
	                             @RequestParam(defaultValue = "12") int size,
	                             @RequestParam(value = "keyword", required = false) String keyword,
	                             Model model) {

	    // 🔍 判斷是否有搜尋關鍵字
	    List<TravelPlan> allPlans;
	    if (keyword != null && !keyword.isBlank()) {
	        allPlans = travelPlanSvc.getAllTravelPlans().stream()
	                .filter(plan -> plan.getTravelTitle() != null &&
	                                plan.getTravelTitle().toLowerCase().contains(keyword.toLowerCase()))
	                .toList();
	    } else {
	        allPlans = travelPlanSvc.getAllTravelPlans();
	    }

	    // ✂ 分頁邏輯
	    int start = page * size;
	    int end = Math.min(start + size, allPlans.size());
	    List<TravelPlan> pagedPlans = allPlans.subList(start, end);

	    // 💾 塞進 model 給 Thymeleaf 用
	    model.addAttribute("plans", pagedPlans);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", (int) Math.ceil((double) allPlans.size() / size));
	    model.addAttribute("size", size);
	    model.addAttribute("keyword", keyword); // ➜ 頁面回填關鍵字

	    return "member/member-travel-list";
	}

	@GetMapping("/travel/detail/{id}")
	public String showTravelPlanDetail(@PathVariable("id") Integer planId, Model model) {
	    Optional<TravelPlan> optionalPlan = travelPlanSvc.getTravelPlanEntityById(planId);
	    if (optionalPlan.isEmpty()) {
	        return "redirect:/member/travel/list";
	    }

	    TravelPlan plan = optionalPlan.get();
	    model.addAttribute("plan", plan);
	    
	    // 第一個宣告，查Itinerary並判斷
	    Optional<TravelItinerary> optionalItinerary = travelPlanSvc.getTravelItineraryForPlan(planId);
	    if (optionalItinerary.isEmpty()) {
	        model.addAttribute("message", "無可用行程！從CSV看ID= " + planId + " 沒對應。"); // 加訊息給頁面
	        return "member/member-travel-detail";
	    }
	    TravelItinerary itinerary = optionalItinerary.get();
	    System.out.println("詳情頁ID: " + itinerary.getTravelItineraryId()); // 加這行debug，確認有數字如2
	    model.addAttribute("itinerary", optionalItinerary.get());

	    // 直接查 TravelPlanDay by planId，排序
	    List<TravelPlanDay> dayList = travelPlanDayRepo.findByTravelPlan_TravelPlanIdOrderByTravelDayNumber(planId);

	    if (dayList.isEmpty()) {
	        System.out.println("No DayList for planId: " + planId);
	        model.addAttribute("dayList", List.of());
	        model.addAttribute("message", "目前無可用的每日行程");
	    } else {
	        System.out.println("📅 多天行程數量：" + dayList.size());
	        for (TravelPlanDay day : dayList) {
	            System.out.println("Day " + day.getTravelDayNumber() + ": " + day.getTraveltime());
	        }
	        model.addAttribute("dayList", dayList);
	    }

	 // 在if (optionalItinerary.isEmpty())後
	    model.addAttribute("itinerary", optionalItinerary.get());
	    System.out.println("傳ID到頁面: " + optionalItinerary.get().getTravelItineraryId()); // 加這行debug logs
	    
	    // 刪掉重複宣告，直接用第一個optionalItinerary（如果需要orElse(null)，但上面已get()傳了）
	    // model.addAttribute("itinerary", optionalItinerary.orElse(null)); // 如果想覆蓋成null時用，但上面已處理

	    return "member/member-travel-detail";
	}
	
	//送出資訊之後
	@PostMapping("/tour-order/create")
	public String createTourOrder(@ModelAttribute TourOrderVO order,
	                              @RequestParam("peopleCount") int peopleCount,
	                              @AuthenticationPrincipal MemberUserDetails loginUser,
	                              Model model,
	                              @RequestParam Map<String, String> params) {

	    memVO member = loginUser.getMember();
	    order.setMember(member);
	    order.setPeopleCount(peopleCount);
	    order.setTourOrderStatus("待付款"); 

	    // 驗證Itinerary（從Service找，基於CSV/DB）
	    Optional<TravelItinerary> optItinerary = travelItinerarySvc.findById(order.getTravelItineraryId());
	    if (optItinerary.isEmpty() || peopleCount > optItinerary.get().getMaxTourist()) {
	        model.addAttribute("error", "行程不存在或人數超過上限！");
	        return "member/member-tour-order-form"; // 導回表單
	    }
	    TravelItinerary itinerary = optItinerary.get();

	    // 計算（移到驗證後，避免重複和未定義）
	    order.setTourPrice(itinerary.getTotalPrice().intValue()); // 假設單價
	    order.setTotalAmount(order.getTourPrice() * peopleCount);
	    order.setTotalAfterCoupon(order.getTotalAmount()); // 無券時等總額，避null
	    order.setTravelItinerary(itinerary); // 加這行存關聯entity
	    order.setTravelItineraryId(itinerary.getTravelItineraryId()); // 已 OK

	    // 保存訂單
	    TourOrderVO savedOrder = tourOrderSvc.save(order);

	    // 保存旅客
	    for (int i = 0; i < peopleCount; i++) {
	        TouristVO tourist = new TouristVO();
	        tourist.setTourOrder(savedOrder);
	        tourist.setTouristName(params.get("touristName" + i));
	        tourist.setTouristPersonalId(params.get("touristPersonalId" + i));
	        tourist.setContactNumber(params.get("contactNumber" + i));
	        touristSvc.save(tourist);
	    }

	    model.addAttribute("orderId", savedOrder.getTourOrderId());
	    // 更新Itinerary max_tourist（選加）
	    itinerary.setMaxTourist(itinerary.getMaxTourist() - peopleCount);
	    travelItinerarySvc.save(itinerary);

	    return "redirect:/member/orders";
	}

	@GetMapping("/tour-order/create")
	public String showTourOrderForm(@RequestParam(value = "planId", required = false) Integer planId, Model model){
	    if (planId == null) {
	        model.addAttribute("error", "缺少計劃編號，無法下訂！");
	        return "error-page";
	    }

	    Optional<TravelItinerary> optionalItinerary = travelPlanSvc.getTravelItineraryForPlan(planId); // 跟showTravelPlanDetail一樣查
	    TravelItinerary itinerary = optionalItinerary.orElse(null);

	    if (itinerary == null) {
	        model.addAttribute("error", "找不到行程資訊，從planId=" + planId + "查無對應");
	        return "error-page";
	    }
	    model.addAttribute("itinerary", itinerary);

	    return "member/member-tour-order-form";
	}
	
	
	//模擬付費 controller
	@PostMapping("/simulate-payment")
	public String simulatePayment(@RequestParam("orderId") Integer orderId,
	                              @RequestParam("cardNumber") String cardNumber,
	                              @RequestParam("cvv") String cvv,
	                              @RequestParam("expiryDate") String expiryDate,
	                              Model model) {

	    // 模擬驗證規格（固定測試值）
	    if ("4111111111111111".equals(cardNumber) && "123".equals(cvv) && "12/25".equals(expiryDate)) {
	        Optional<TourOrderVO> optionalOrder = tourOrderSvc.findById(orderId);
	        if (optionalOrder.isPresent()) {
	            TourOrderVO order = optionalOrder.get();
	            order.setTourOrderStatus("已付款");
	            tourOrderSvc.save(order);
	            return "redirect:/member/orders"; // 導回訂單列表看已付款
	        }
	    }

	    model.addAttribute("error", "信用卡規格錯誤，請用測試值重試！");
	    model.addAttribute("orderId", orderId); // 傳回重試
	    return "member/simulate-payment"; // 錯回付費頁
	}







}
