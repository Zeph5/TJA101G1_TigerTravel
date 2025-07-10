package com.ticket.controller;

import com.ticket.model.*;
import com.ticket.repository.*;
import com.ticket.service.TicketOrderService;
import com.member.security.MemberUserDetails;
import com.member.model.memVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/ticket")
public class TicketOrderController {

    @Autowired
    private TicketOrderService ticketOrderService;

    @Autowired
    private TicketRepository ticketRepository;

    // step 1: 顯示下單頁
    @PostMapping("/ticketorder")
    public String showOrderPage(@RequestParam("ticketIds") List<Integer> ticketIds, Model model) {
        List<Ticket> selectedTickets = ticketRepository.findAllById(ticketIds);
        model.addAttribute("selectedTickets", selectedTickets);
        return "ticket/ticketorder";
    }

 // step 2: 下單處理
    @PostMapping("/submit")
    public String submitOrder(
            @AuthenticationPrincipal MemberUserDetails loginUser,
            @RequestParam("ticketIds") List<Integer> ticketIds,
            @RequestParam("quantities") List<Integer> quantities,
            @RequestParam("cardNumber") String cardNumber,
            @RequestParam("expMonth") String expMonth,
            @RequestParam("expYear") String expYear,
            @RequestParam("cvv") String cvv,
            Model model
    ) {
        if (!cardNumber.matches("\\d{16}") || !expMonth.matches("\\d{2}") || !expYear.matches("\\d{2}") || !cvv.matches("\\d{3}")) {
            model.addAttribute("error", "信用卡資料格式不正確");
            return "/ticket/ticketorder"; // <- 要 return 正確頁面
        }
        memVO member = loginUser.getMember();
        Integer orderId = ticketOrderService.createOrder(member.getMemberId(), ticketIds, quantities);
        return "redirect:/ticket/detail/" + orderId; // <- 路徑要和 detail method 對齊
    }

  //step 3: 顯示訂單明細
    @GetMapping("/detail/{orderId}")
    public String orderDetail(@PathVariable Integer orderId, Model model) {
        model.addAttribute("order", ticketOrderService.getOrderDetail(orderId));
        return "ticket/ticketorderDetail"; // <-- 和 HTML 檔名對齊
    }
}

