package com.ticket.controller;

import com.ticket.model.Ticket;
import com.ticket.service.TicketService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @GetMapping("/ticketlist")
    public String listTickets(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {

        // 全部票券（分頁）
        Page<Ticket> ticketPage = ticketService.getTickets(PageRequest.of(page, size));
        model.addAttribute("ticketPage", ticketPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);

        // 查詢結果
        if (keyword != null && !keyword.trim().isEmpty()) {
            List<Ticket> searchList = ticketService.findByName(keyword);
            model.addAttribute("searchList", searchList);
        } else {
            model.addAttribute("searchList", null); // 沒查詢時不顯示查詢區
        }
        model.addAttribute("keyword", keyword);

        return "ticket/ticketlist";
    }
}
