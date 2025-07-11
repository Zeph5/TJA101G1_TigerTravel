package com.ticket.controller;

import com.ticket.model.*;
import com.ticket.repository.*;
import com.ticket.service.TicketOrderService;
import com.member.security.MemberUserDetails;
import com.member.model.MemberRepository;
import com.member.model.memVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/ticket")
public class TicketOrderController {

    @Autowired
    private TicketOrderService ticketOrderService;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private MemberRepository memberRepository; // 你要確認這個 repository 有引入

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
            return "/ticket/ticketorder";
        }
        memVO member = loginUser.getMember();
        Integer orderId = ticketOrderService.createOrder(member.getMemberId(), ticketIds, quantities);
        return "redirect:/ticket/detail/" + orderId;
    }

    // step 3: 顯示訂單明細（已優化！）
    @GetMapping("/detail/{orderId}")
    public String orderDetail(@PathVariable Integer orderId, Model model) {
        TicketOrder order = ticketOrderService.getOrderDetail(orderId);
        if (order == null) {
            model.addAttribute("error", "查無此訂單");
            return "error/404";
        }

        // 查會員名稱
        String memberName = "未知會員";
        Optional<memVO> memberOpt = memberRepository.findById(order.getMemberId());
        if (memberOpt.isPresent()) {
            memVO member = memberOpt.get();
            memberName = member.getMemberName(); // 根據你的會員名稱欄位名稱調整
        }

        // 組訂單明細清單（含品項名稱與數量）
        List<Map<String, Object>> orderItems = new ArrayList<>();
        for (TicketOrderReceipt receipt : order.getTicketOrderReceipts()) {
            Optional<Ticket> ticketOpt = ticketRepository.findById(receipt.getTicketId());
            String ticketName = ticketOpt.map(Ticket::getTicketName).orElse("未知票券");
            Map<String, Object> item = new HashMap<>();
            item.put("name", ticketName);
            item.put("quantity", receipt.getTicketCount());
            orderItems.add(item);
        }

        // 塞進 Model 對應前端模板欄位
        model.addAttribute("orderItems", orderItems);
        model.addAttribute("totalAmount", order.getOrderPrice().intValue());
        model.addAttribute("memberName", memberName);

        // 時間格式轉字串（如你 template 頁要）
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String orderTimeStr = order.getOrderDatetime().format(formatter);
        model.addAttribute("orderTime", orderTimeStr);

        // 回傳訂單明細頁
        return "ticket/ticketorderreceipt";
    }
}
