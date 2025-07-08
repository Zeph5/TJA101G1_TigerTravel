package com.ticket.controller;

import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.member.model.memVO;
import com.member.security.MemberUserDetails;
import com.ticket.model.TicketOrderReceiptVO;
import com.ticket.model.TicketOrderVO;
import com.ticket.service.TicketOrderReceiptService;
import com.ticket.service.TicketOrderService;

@Controller
@RequestMapping("/member")
public class TicketOrderController {

    private final TicketOrderService orderService;
    private final TicketOrderReceiptService receiptService;

    public TicketOrderController(TicketOrderService orderService, TicketOrderReceiptService receiptService) {
        this.orderService = orderService;
        this.receiptService = receiptService;
    }

    @GetMapping("/ticket/orders")
    public String showMyTicketOrders(@AuthenticationPrincipal MemberUserDetails loginUser, Model model) {
        memVO member = loginUser.getMember();

        List<TicketOrderVO> orders = orderService.getOrdersByMember(member);

        // 🔽 宣告 receiptMap，存放每筆訂單的發票資料
        Map<Integer, List<TicketOrderReceiptVO>> receiptMap = new HashMap<>();

        for (TicketOrderVO order : orders) {
            List<TicketOrderReceiptVO> receipts = receiptService.getReceiptsByOrder(order);
            if (!receipts.isEmpty()) {
                // 🔽 把每張票的圖片轉成 base64 串
                for (TicketOrderReceiptVO receipt : receipts) {
                    byte[] imageBytes = receipt.getTicket().getTicketImage();
                    if (imageBytes != null && imageBytes.length > 0) {
                        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                        receipt.setTicketImageBase64(base64Image);
                    }
                }
                receiptMap.put(order.getTicketOrderId(), receipts); // ✅ 這邊使用 receiptMap 就不會錯
            }
        }

        model.addAttribute("now", LocalDate.now());
        model.addAttribute("ticketOrders", orders);
        model.addAttribute("receiptMap", receiptMap);

        return "member/ticketOrders";
    }

}