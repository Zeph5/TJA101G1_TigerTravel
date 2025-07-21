package com.ticket.controller;

import com.ticket.model.Ticket;
import com.ticket.repository.TicketRepository;
import com.ticket.service.TicketService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Controller
public class AdminTicketController {

    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private TicketService ticketService;

    // 管理票券清單
    @GetMapping("/admin/ticket/list")
    public String list(Model model,@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "9") int size) {
    	// 全部票券（分頁）
		Page<Ticket> ticketPage = ticketService.getTickets(PageRequest.of(page, size));
		model.addAttribute("ticketPage", ticketPage);
		model.addAttribute("currentPage", page);
		model.addAttribute("pageSize", size);
		
        return "admin/mticketlist";
    }

    // 刪除票券
    @PostMapping("/admin/ticket/delete/{id}")
    public String deleteTicket(@PathVariable Integer id) {
        ticketRepository.deleteById(id);
        return "redirect:/admin/ticket/list";
    }

    // 編輯票券
    @PostMapping("/admin/ticket/edit")
    public String editTicket(
            @RequestParam("ticketId") Integer ticketId,
            @RequestParam("ticketName") String ticketName,
            @RequestParam("ticketPrice") String ticketPrice,
            @RequestParam("ticketStock") Integer ticketStock,
            @RequestParam("ticketDescription") String ticketDescription,
            @RequestParam("ticketStatus") Integer ticketStatus,
            @RequestParam(value = "ticketImage", required = false) MultipartFile ticketImage
    ) throws IOException {
        Optional<Ticket> ticketOpt = ticketRepository.findById(ticketId);
        if (ticketOpt.isPresent()) {
            Ticket ticket = ticketOpt.get();
            ticket.setTicketName(ticketName);
            ticket.setTicketPrice(new java.math.BigDecimal(ticketPrice));
            ticket.setTicketStock(ticketStock);
            ticket.setTicketDescription(ticketDescription);
            ticket.setTicketStatus(ticketStatus);
            if (ticketImage != null && !ticketImage.isEmpty()) {
                ticket.setTicketImage(ticketImage.getBytes());
            }
            ticketRepository.save(ticket);
        }
        return "redirect:/admin/ticket/list";
    }
}
