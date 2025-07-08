package com.member.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.member.model.FavoriteTourVO;
import com.member.model.memVO;
import com.member.security.MemberUserDetails;
import com.member.service.favorite.FavoriteTourService;

@Controller
@RequestMapping("/member")
public class FavoriteController {

	private final FavoriteTourService favoriteTourSvc;

	public FavoriteController(FavoriteTourService favoriteTourSvc) {
		this.favoriteTourSvc = favoriteTourSvc;
	}

	@GetMapping("/favorites")
	public String showAllFavorites(Model model, @AuthenticationPrincipal MemberUserDetails loginUser) {
		memVO member = loginUser.getMember();

		// 景點收藏
		List<FavoriteTourVO> sceneryFavorites = favoriteTourSvc.getFavoritesByMember(member);
		model.addAttribute("sceneryFavorites", sceneryFavorites);

		// 行程收藏
//		List<FavoriteTourVO> planFavorites = favoritePlanSvc.getFavoritesByMember(member);
//		model.addAttribute("planFavorites", planFavorites);

		System.out.println("✅ 進入收藏頁面");

		return "member/favorites";
	}
}
