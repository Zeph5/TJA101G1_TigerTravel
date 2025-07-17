package com.member.controller;

import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.member.model.MemberTicketOrderReceiptRepository;
import com.member.model.memVO;
import com.member.security.MemberUserDetails;
import com.ticket.model.TicketOrderReceipt;
import com.ticket.repository.TicketRepository;
import com.ticket.model.Ticket;
import com.ticket.model.TicketOrder;
import com.member.service.MemberTicketOrderReceiptService;
import com.member.service.MemberTicketOrderService;

@Controller
@RequestMapping("/member/detail")
public class MemberTicketOrderController {

    private final MemberTicketOrderService orderService;
    private final MemberTicketOrderReceiptService receiptService;
    private final MemberTicketOrderReceiptRepository memberTicketOrderRepo;
    private final TicketRepository ticketRepo;

    public MemberTicketOrderController(MemberTicketOrderService orderService, 
    							MemberTicketOrderReceiptService receiptService,
    							MemberTicketOrderReceiptRepository memberTicketOrderRepo,
    							TicketRepository ticketRepo) {
        this.orderService = orderService;
        this.receiptService = receiptService;
        this.memberTicketOrderRepo = memberTicketOrderRepo;
        this.ticketRepo = ticketRepo;
    }
    
    @GetMapping("/order/{orderId}")
    public String showOrderDetails(@AuthenticationPrincipal MemberUserDetails loginUser,
    								@PathVariable Integer orderId, Model model) {
        Optional<TicketOrder> optionalOrder = orderService.getOrderById(orderId);

        if (!optionalOrder.isPresent()) {
            model.addAttribute("errorMsg", "找不到該筆訂單");
            return "error"; // 可替換為自訂錯誤頁面
        }

        TicketOrder order = optionalOrder.get();
        List<TicketOrderReceipt> receipts = receiptService.getReceiptsByOrder(order);

        Map<Integer, Ticket> ticketMap = new HashMap<>();
        Map<Integer, String> ticketImageMap = new HashMap<>();
        Map<Ticket, Integer> ticketCountMap = new LinkedHashMap<>();


        for (TicketOrderReceipt receipt : receipts) {
            Integer ticketId = receipt.getTicketId();

            if (ticketId != null) {
                ticketRepo.findById(ticketId).ifPresent(ticket -> {
                	
                	ticketCountMap.put(ticket, ticketCountMap.getOrDefault(ticket, 0) + 1);
                    ticketMap.put(receipt.getTicketOrderReceiptId(), ticket);
                    byte[] img = ticket.getTicketImage();
                    
                    if (img != null) {
                        String base64 = Base64.getEncoder().encodeToString(img);
                        ticketImageMap.put(receipt.getTicketOrderReceiptId(), base64);
                    }
                    
                });
            }
        }
        
        System.out.println("✅ loginUser = " + loginUser);

        model.addAttribute("ticketCountMap", ticketCountMap);
        model.addAttribute("order", order);
        model.addAttribute("receipts", receipts);
        model.addAttribute("ticketMap", ticketMap);
        model.addAttribute("ticketImageMap", ticketImageMap);
        

        return "member/member-ticket-order-detail"; // ✅ 跟你的 html 名稱一致
        
    }
    

}
