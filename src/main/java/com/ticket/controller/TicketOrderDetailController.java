package com.ticket.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ticket.model.TicketOrderReceiptVO;
import com.ticket.model.TicketOrderVO;
import com.ticket.service.TicketOrderReceiptService;
import com.ticket.service.TicketOrderService;

@Controller
@RequestMapping("/member/order")
public class TicketOrderDetailController {

    private final TicketOrderService orderService;
    private final TicketOrderReceiptService receiptService;

    public TicketOrderDetailController(TicketOrderService orderService,
                                       TicketOrderReceiptService receiptService) {
        this.orderService = orderService;
        this.receiptService = receiptService;
    }

    @GetMapping("/{orderId}")
    public String showOrderDetail(@PathVariable("orderId") Integer orderId, Model model) {
        Optional<TicketOrderVO> orderOpt = orderService.getOrderById(orderId);

        if (orderOpt.isPresent()) {
            TicketOrderVO order = orderOpt.get();

            // ✅ 查詢該訂單的所有票券明細
            List<TicketOrderReceiptVO> receipts = receiptService.getReceiptsByOrder(order);

            model.addAttribute("order", order);
            model.addAttribute("receipts", receipts);

            return "member/ticketOrderDetail"; // 頁面要存在
        }

        model.addAttribute("message", "查無此訂單");
        return "member/ticketOrderDetail";
    }
}


