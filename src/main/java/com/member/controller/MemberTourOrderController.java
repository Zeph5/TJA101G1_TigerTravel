package com.member.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.member.model.FavoriteTravelPlan;
import com.member.model.MemberTicketOrderRepository;
import com.member.model.TourOrderVO;
import com.member.model.TouristIdRepository;
import com.member.model.TouristIdVO;
import com.member.model.TouristRepository;
import com.member.model.TouristVO;
import com.member.model.memVO;
import com.member.model.memberTour.MemTravelPlanDayRepository;
import com.member.security.MemberUserDetails;
import com.member.service.MemberTicketOrderReceiptService;
import com.member.service.MemberTicketOrderService;
import com.member.service.TourOrderService;
import com.member.service.TouristService;
import com.member.service.favorite.FavoriteTravelPlanService;
import com.member.service.travel.MemberTravelPlanService;
import com.scenery.model.SceneryVO;
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
	private final FavoriteTravelPlanService favoriteTravelPlanSvc;
	private final TouristRepository touristRepo;
	private final TouristIdRepository touristIdRepo;
	
	public MemberTourOrderController(MemberTicketOrderService ticketOrderSvc,
										TourOrderService tourOrderSvc,
										MemberTicketOrderReceiptService receiptService,
										TicketRepository ticketRepo,
										MemTravelPlanDayRepository travelPlanDayRepo,
										TravelPlanService travelPlanSvc,
										MemberTravelPlanService memberTravelPlanSvc,
										TouristService touristSvc,
										TravelItineraryService travelItinerarySvc,
										FavoriteTravelPlanService favoriteTravelPlanSvc,
										TouristRepository touristRepo,
										TouristIdRepository touristIdRepo) {
		this.ticketOrderSvc = ticketOrderSvc;
		this.tourOrderSvc = tourOrderSvc;
		this.receiptService = receiptService;
		this.ticketRepo = ticketRepo;
		this.travelPlanDayRepo = travelPlanDayRepo;
		this.travelPlanSvc = travelPlanSvc;
		this.memberTravelPlanSvc = memberTravelPlanSvc;
		this.touristSvc = touristSvc;
		this.travelItinerarySvc = travelItinerarySvc;
		this.favoriteTravelPlanSvc = favoriteTravelPlanSvc;
		this.touristRepo = touristRepo;
		this.touristIdRepo = touristIdRepo;
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

		List<TouristIdVO> tourists = touristSvc.findByTourOrder(order);
		
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
		model.addAttribute("tourists", tourists);
		
		return "member/member-tour-order-detail";

	}
	
	@GetMapping("/travel-plans")
	public String showPagedPlans(@AuthenticationPrincipal MemberUserDetails loginUser,
	                             @RequestParam(defaultValue = "0") int page,
	                             @RequestParam(defaultValue = "12") int size,
	                             @RequestParam(value = "keyword", required = false) String keyword,
	                             Model model) {
	    memVO member = loginUser.getMember();
	    
	    System.out.println("=== MEMBER TRAVEL PLANS DEBUG ===");
	    System.out.println("Member: " + member.getMemberAccount());
	    System.out.println("Page: " + page + ", Size: " + size);
	    System.out.println("Search keyword: " + keyword);
	    
	    // 🔍 Enhanced search logic with better filtering
	    List<TravelPlan> allPlans;
	    if (keyword != null && !keyword.isBlank()) {
	        String searchKeyword = keyword.toLowerCase().trim();
	        System.out.println("Performing search with keyword: " + searchKeyword);
	        
	        allPlans = travelPlanSvc.getAllTravelPlans().stream()
	                .filter(plan -> {
	                    // Search in multiple fields for better results
	                    boolean titleMatch = plan.getTravelTitle() != null && 
	                                        plan.getTravelTitle().toLowerCase().contains(searchKeyword);
	                    boolean descMatch = plan.getTravelPlanDescription() != null && 
	                                       plan.getTravelPlanDescription().toLowerCase().contains(searchKeyword);
	                    
	                    return titleMatch || descMatch;
	                })
	                .collect(Collectors.toList());
	        
	        System.out.println("Search results: " + allPlans.size() + " plans found");
	    } else {
	        allPlans = travelPlanSvc.getAllTravelPlans();
	        System.out.println("No search keyword, showing all plans: " + allPlans.size());
	    }
	    
	    // Get user's favorite travel plans
	    List<FavoriteTravelPlan> favorites = favoriteTravelPlanSvc.getFavoritesByMember(member);
	    List<Integer> favoritedIds = favorites.stream()
	        .map(fav -> fav.getTravelPlan().getTravelPlanId())
	        .collect(Collectors.toList());
	    
	    System.out.println("User has " + favoritedIds.size() + " favorite travel plans");

	    // ✂ Pagination logic
	    int start = page * size;
	    int end = Math.min(start + size, allPlans.size());
	    List<TravelPlan> pagedPlans = start < allPlans.size() ? allPlans.subList(start, end) : new ArrayList<>();

	    // 💾 Add data to model for Thymeleaf
	    model.addAttribute("favoritedIds", favoritedIds);
	    model.addAttribute("plans", pagedPlans);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", (int) Math.ceil((double) allPlans.size() / size));
	    model.addAttribute("size", size);
	    model.addAttribute("keyword", keyword); // ➜ For form repopulation
	    model.addAttribute("totalResults", allPlans.size()); // ➜ Show search result count
	    
	    // Add search status for the template
	    model.addAttribute("isSearching", keyword != null && !keyword.isBlank());
	    
	    System.out.println("Returning " + pagedPlans.size() + " plans on page " + page);
	    return "member/member-travel-list";
	}

	// ✅ 新增一個專門處理旅程詳情的方法
	@GetMapping("/travel/detail/{id}")
	public String showMemberTravelDetail(@PathVariable("id") Integer id, 
	                                   @AuthenticationPrincipal MemberUserDetails loginUser,
	                                   Model model) {
	    try {
	        System.out.println("=== MEMBER TRAVEL DETAIL DEBUG ===");
	        System.out.println("Requested travel ID: " + id);
	        System.out.println("User: " + (loginUser != null ? loginUser.getUsername() : "null"));
	        
	        // 驗證 ID
	        if (id == null || id <= 0) {
	            System.err.println("Invalid travel ID: " + id);
	            model.addAttribute("error", "無效的旅程ID");
	            return "redirect:/member/travel-plans";
	        }

	        // 查詢旅程計劃
	        Optional<TravelPlan> travelPlanOpt = travelPlanSvc.findById(id);
	        if (travelPlanOpt.isEmpty()) {
	            System.err.println("Travel plan not found with ID: " + id);
	            model.addAttribute("error", "旅程不存在 (ID: " + id + ")");
	            return "redirect:/member/travel-plans";
	        }

	        TravelPlan plan = travelPlanOpt.get();
	        System.out.println("Found travel plan: " + plan.getTravelTitle());
	        
	        // 獲取行程詳情
	        Optional<TravelItinerary> itineraryOpt = travelPlanSvc.getTravelItineraryForPlan(id);
	        TravelItinerary itinerary = itineraryOpt.orElse(null);
	        
	        if (itinerary != null) {
	            // 查詢行程天數
	            List<TravelPlanDay> dayList = travelPlanDayRepo.findByTravelItinerary_TravelItineraryId(
	                itinerary.getTravelItineraryId());
	            dayList.sort(Comparator.comparing(TravelPlanDay::getTravelDayNumber));
	            
	            // 按天數分組
	            Map<Integer, List<TravelPlanDay>> groupedDays = new TreeMap<>();
	            for (TravelPlanDay day : dayList) {
	                groupedDays.computeIfAbsent(day.getTravelDayNumber(), k -> new ArrayList<>()).add(day);
	            }
	            
	            model.addAttribute("itinerary", itinerary);
	            model.addAttribute("groupedDays", groupedDays);
	            model.addAttribute("travelPlanDays", dayList);
	            
	            System.out.println("Found " + dayList.size() + " travel plan days");
	        } else {
	            model.addAttribute("itinerary", null);
	            model.addAttribute("groupedDays", new HashMap<>());
	            model.addAttribute("travelPlanDays", new ArrayList<>());
	        }
	        
	        // 檢查收藏狀態
	        if (loginUser != null) {
	            memVO member = loginUser.getMember();
	            List<FavoriteTravelPlan> favorites = favoriteTravelPlanSvc.getFavoritesByMember(member);
	            boolean isFavorite = favorites.stream()
	                .anyMatch(fav -> fav.getTravelPlan().getTravelPlanId().equals(id));
	            model.addAttribute("isFavorite", isFavorite);
	            model.addAttribute("currentUser", member);
	            model.addAttribute("isAuthenticated", true);
	        } else {
	            model.addAttribute("isFavorite", false);
	            model.addAttribute("isAuthenticated", false);
	        }
	        
	        // 加入模型
	        model.addAttribute("plan", plan);
	        model.addAttribute("travelPlan", plan); // 別名
	        
	        System.out.println("Returning template: member/member-travel-detail");
	        return "member/member-travel-detail";

	    } catch (Exception e) {
	        System.err.println("Error in showMemberTravelDetail: " + e.getMessage());
	        e.printStackTrace();
	        model.addAttribute("error", "載入旅程詳情時發生錯誤: " + e.getMessage());
	        return "redirect:/member/travel-plans";
	    }
	}

	//送出資訊之後
	@PostMapping("/tour-order/create")
	public String createTourOrder(@ModelAttribute TourOrderVO order,
	                              @RequestParam Map<String, String> params,
	                              @AuthenticationPrincipal MemberUserDetails loginUser,
	                              Model model) {
	    // ✅ 綁定登入會員
	    memVO member = loginUser.getMember();
	    order.setMember(member);

	    // ✅ 行程 ID 檢查
	    String travelItineraryStr = params.get("travelItineraryId");
	    if (travelItineraryStr == null || travelItineraryStr.isBlank()) {
	        model.addAttribute("error", "行程 ID 缺失");
	        model.addAttribute("inputBackup", params);
	        return "member/member-tour-order-form";
	    }

	    int peopleCount = Integer.parseInt(params.getOrDefault("peopleCount", "0"));
	    Integer itineraryId = Integer.parseInt(travelItineraryStr);
	    Optional<TravelItinerary> opt = travelItinerarySvc.findById(itineraryId);
	    if (opt.isEmpty()) {
	        model.addAttribute("error", "行程不存在");
	        model.addAttribute("inputBackup", params);
	        return "member/member-tour-order-form";
	    }
	    TravelItinerary itinerary = opt.get();

	    // ✅ 檢查人數限制
	    if (peopleCount + 1 > itinerary.getMaxTourist()) {
	        model.addAttribute("error", "人數超過行程可報名上限！");
	        model.addAttribute("inputBackup", params);
	        return "member/member-tour-order-form";
	    }

	    // ✅ 初始化旅客明細集合（避免 null）
	    Set<TouristIdVO> touristSet = new HashSet<>();

	    // ✅ 建立訂單基本資料（先不綁主報名人）
	    order.setTourOrderStatus("待付款");
	    order.setTourPrice(itinerary.getTotalPrice().intValue());
	    order.setPeopleCount(peopleCount + 1);
	    order.setTotalAmount(order.getTourPrice() * order.getPeopleCount());
	    order.setTotalAfterCoupon(order.getTotalAmount());
	    order.setTravelItinerary(itinerary);
	    order.setTourist(null); // 主報名人之後補上
	    order.setTourists(touristSet); // 先綁空集合，避免 null 錯誤

	    TourOrderVO savedOrder = tourOrderSvc.save(order);

	    // ✅ 儲存主報名人（要在訂單之後，因為要關聯 order_id）
	    TouristVO mainTourist = new TouristVO();
	    mainTourist.setTouristName(params.get("mainName"));
	    mainTourist.setTouristEmail(params.get("mainEmail"));
	    mainTourist.setPhone(params.get("mainPhone"));
	    mainTourist.setTouristPersonalId(params.get("mainPid"));
	    mainTourist.setContactNumber(params.get("mainPhone"));
	    mainTourist.setMemberAccount(member.getMemberAccount());
	    mainTourist.setCreateTime(LocalDateTime.now());
	    mainTourist.setTourOrder(savedOrder); // 關聯 tour_order_id
	    TouristVO savedMain = touristRepo.save(mainTourist);

	    // ✅ 補上訂單主報名人
	    savedOrder.setTourist(savedMain);
	    tourOrderSvc.save(savedOrder);

	    for (int i = 0; i < peopleCount; i++) {
	        TouristIdVO tid = new TouristIdVO();
	        tid.setTourOrder(savedOrder);
	        tid.setTouristName(params.get("touristName" + i));
	        tid.setTouristPersonalId(params.get("touristPersonalId" + i));
	        tid.setContactNumber(params.get("contactNumber" + i));
	        touristIdRepo.save(tid);
	        touristSet.add(tid);
	    }

	    tourOrderSvc.save(savedOrder); // 一次性 persist 明細

	    // ✅ 扣除報名名額
	    itinerary.setMaxTourist(itinerary.getMaxTourist() - savedOrder.getPeopleCount());
	    travelItinerarySvc.save(itinerary);

	    return "redirect:/member/simulate-payment?orderId=" + savedOrder.getTourOrderId();
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
	    model.addAttribute("order", new TourOrderVO());

	    return "member/member-tour-order-form";
	}
	
	//模擬付費 controller
	@PostMapping("/simulate-payment")
	public String simulatePayment(@RequestParam("orderId") Integer orderId,
	                              @RequestParam("cardNumber") String cardNumber,
	                              @RequestParam("cvv") String cvv,
	                              @RequestParam("expiryDate") String expiryDate,
	                              Model model) {

	    System.out.println("✅ 進入 simulatePayment POST");
	    System.out.println("➡️ 傳入卡號: " + cardNumber + ", CVV: " + cvv + ", 到期日: " + expiryDate);

	    // ✅ 格式驗證：卡號（開頭為3~6，共16位數）
	    if (!cardNumber.matches("^[3-6]\\d{15}$")) {
	        model.addAttribute("error", "卡號格式錯誤，請輸入16位數字開頭為3~6的卡號！");
	    } 
	    // ✅ 格式驗證：CVV（三位數）
	    else if (!cvv.matches("^\\d{3}$")) {
	        model.addAttribute("error", "CVV 格式錯誤，請輸入3位數！");
	    }
	    // ✅ 格式驗證：到期日（MM/YY）
	    else if (!expiryDate.matches("^(0[1-9]|1[0-2])/\\d{2}$")) {
	        model.addAttribute("error", "到期日格式錯誤，請輸入 MM/YY 格式！");
	    } 
	    else {
	        // 通過格式驗證，找訂單並更新狀態
	        Optional<TourOrderVO> optionalOrder = tourOrderSvc.findById(orderId);
	        if (optionalOrder.isPresent()) {
	            TourOrderVO order = optionalOrder.get();
	            order.setTourOrderStatus("已付款");

	            // 儲存後四碼與到期日（遮罩）
	            String lastFour = cardNumber.substring(cardNumber.length() - 4);
	            order.setCardLastFour(lastFour);
	            order.setCardExpiryDate(expiryDate);

	            tourOrderSvc.save(order);
	            return "redirect:/member/payment-success?orderId=" + orderId;
	        } else {
	            model.addAttribute("error", "找不到訂單！");
	        }
	    }

	    // ⛔ 失敗處理：回傳錯誤與訂單資訊
	    model.addAttribute("orderId", orderId);
	    tourOrderSvc.findById(orderId).ifPresent(order -> model.addAttribute("order", order));
	    return "member/simulate-payment";
	}

	@GetMapping("/simulate-payment")
	public String showSimulatePayment(@RequestParam("orderId") Integer orderId, Model model) {
	    
		Optional<TourOrderVO> optionalOrder = tourOrderSvc.findById(orderId);
		if (optionalOrder.isPresent()) {
			model.addAttribute("order", optionalOrder.get());
	        model.addAttribute("orderId", orderId);
		} else {
			model.addAttribute("error", "找不到訂單，請檢查！");
	        return "redirect:/member/orders"; // 錯導回列表
		}
		
		// 簡單傳 orderId 給 Thymeleaf（如果想加訂單細節，從 service 查 optionalOrder = tourOrderSvc.findById(orderId)，加到 model）
	    model.addAttribute("orderId", orderId);
	    return "member/simulate-payment"; // 返回你的支付 HTML
	}

	//付款成功頁面導向
	@GetMapping("/payment-success")
	public String showPaymentSuccess(@RequestParam("orderId") Integer orderId, Model model) {
		System.out.println("✅ 到達 payment-success，訂單ID：" + orderId);
		
	    Optional<TourOrderVO> optionalOrder = tourOrderSvc.findById(orderId);
	    if (optionalOrder.isEmpty()) {
	        model.addAttribute("error", "找不到訂單編號！");
	        return "error-page"; // 或自訂錯誤頁
	    }
	    
	    TourOrderVO order = optionalOrder.get();
	    model.addAttribute("order", order);
	    
	    System.out.println("💡 付款成功畫面：order=" + order);
	    System.out.println("💡 itinerary=" + order.getTravelItinerary());
	    System.out.println("💡 plan=" + order.getTravelItinerary().getTravelPlan());
	    
	    return "member/payment-success"; // 要建立對應的 HTML 頁面
	}
}