package com.ticket.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ticket.model.Ticket;
import com.ticket.service.TicketService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class TicketController {

	@Autowired
	private TicketService ticketService;

	@GetMapping("/ticketlist")
	public String listTickets(Model model, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String keyword) {

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

	@GetMapping("/ticket/image/{id}")
	@ResponseBody // 這樣可以直接回傳 byte[]
	public byte[] getTicketImage(@PathVariable Integer id, HttpServletResponse response) {
		Optional<Ticket> ticketOpt = ticketService.findById(id);
		return ticketOpt.filter(ticket -> ticket.getTicketImage() != null).map((Ticket ticket) -> {
			response.setContentType(MediaType.IMAGE_JPEG_VALUE);
			return ticket.getTicketImage();
		}).orElseGet(() -> {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return null;
		});
	}

	@GetMapping("/ticketcreate")
	public String showCreateTicketForm(Model model) {
		model.addAttribute("ticket", new Ticket()); // 可用於預設值
		return "ticket/ticketcreate";
	}

	/** 處理新增票券表單 */
	@PostMapping("/ticketcreate")
	public String createTicket(@RequestParam("ticketName") String ticketName,
			@RequestParam("ticketDescription") String ticketDescription,
			@RequestParam("ticketPrice") BigDecimal ticketPrice, @RequestParam("ticketStock") Integer ticketStock,
			@RequestParam("ticketStatus") Integer ticketStatus,
			@RequestParam(value = "ticketImage", required = false) MultipartFile ticketImage,
			RedirectAttributes redirectAttributes) {
		try {
			Ticket ticket = new Ticket();
			ticket.setTicketName(ticketName);
			ticket.setTicketDescription(ticketDescription);
			ticket.setTicketPrice(ticketPrice);
			ticket.setTicketStock(ticketStock);
			ticket.setTicketStatus(ticketStatus);
			ticket.setCreateTime(LocalDateTime.now());

			if (ticketImage != null && !ticketImage.isEmpty()) {
				ticket.setTicketImage(ticketImage.getBytes());
			} else {
				ticket.setTicketImage(null);
			}

			ticketService.save(ticket);
			redirectAttributes.addFlashAttribute("successMessage", "票券新增成功！");
			return "redirect:/ticketlist";
		} catch (IOException e) {
			redirectAttributes.addFlashAttribute("errorMessage", "圖片上傳失敗：" + e.getMessage());
			return "redirect:/ticket/create";
		}

	}
}
