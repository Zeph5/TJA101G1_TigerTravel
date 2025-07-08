package com.member.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.member.model.FavoriteTicketVO;
import com.member.model.memVO;
import com.member.security.MemberUserDetails;
import com.member.service.favorite.FavoriteTicketService;

@Controller
@RequestMapping("/member")
public class FavoriteController {
	
	private FavoriteTicketService favoriteTicketSvc;
	
	public FavoriteController(FavoriteTicketService favoriteTicketSvc) {
		this.favoriteTicketSvc = favoriteTicketSvc;
	}

	@GetMapping("/favorites")
	public String showAllFavorites(Model model ,@AuthenticationPrincipal MemberUserDetails loginUser) {
		memVO member = loginUser.getMember();
		
		List<FavoriteTicketVO> ticketFavorites = favoriteTicketSvc.getFavoriteByMember(member);
		
		model.addAttribute("ticketFavorite", ticketFavorites);
		
		System.out.println("✅ 進入收藏頁面");
		
		return "member/favorites"; //對應 favorite.html
	}

}
