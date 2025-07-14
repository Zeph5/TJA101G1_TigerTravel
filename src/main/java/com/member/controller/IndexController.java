package com.member.controller;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.member.model.MemberRepository;
import com.member.model.memVO;
import com.member.security.MemberUserDetails;
import com.member.service.MailService;
import com.member.service.MemberService;
import com.scenery.model.SceneryService;
import com.scenery.model.SceneryVO;
import com.ticket.model.Ticket;
import com.ticket.repository.TicketRepository;
import com.ticket.service.TicketService;

@Controller
public class IndexController {

	@Autowired
	private MemberService memberService;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private MailService mailService;
	@Autowired
	private SceneryService sceneryService;
	@Autowired
	private TicketRepository ticketRepository;

	@GetMapping({"/", "/index"})
	public String showIndex(Model model) {
		List<Ticket> allTickets = ticketRepository.findAll();
		List<Map<String, Object>> tickeyList = allTickets.stream()
				.limit(10) //只取前10筆
				.map(ticket ->{
					Map<String, Object> map = new HashMap<>();
					map.put("ticketName", ticket.getTicketName());
					map.put("ticketPrice", ticket.getTicketPrice());
					map.put("ticketDescription", ticket.getTicketDescription());
					
					if(ticket.getTicketImage() != null) {
						String base64 = Base64.getEncoder().encodeToString(ticket.getTicketImage());
						map.put("ticketImageBase64", base64);
					}else {
						map.put("ticketImageBase64", null);
					}
					return map;
				})
				.toList();
	    model.addAttribute("ticketList", tickeyList);
	    return "index";
	}

}
