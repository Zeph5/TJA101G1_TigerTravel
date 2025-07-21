package com.manager.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.manager.model.DTO.MemberListDTO;
import com.manager.service.AdminMemberService;

@Controller
@RequestMapping("/admin/members")
public class AdminMemberController {
	private final AdminMemberService adminMemberService;

	public AdminMemberController(AdminMemberService adminMemberService) {
		this.adminMemberService = adminMemberService;
	}

	@GetMapping("/list")
	public String showPage(Model model,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "9") int size,
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) String status,
			@RequestParam(required = false) String sort) {
		Pageable pageable = PageRequest.of(page, size,"oldest".equals(sort) ? Sort.by("createTime").ascending() : Sort.by("createTime").descending());
		Page<MemberListDTO> memberPage = adminMemberService.searchAndFilter(keyword, status, pageable);
				
		model.addAttribute("memberPage", memberPage);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", memberPage.getTotalPages());
		model.addAttribute("keyword", keyword);
		model.addAttribute("status", status);
		model.addAttribute("sort", sort);
		model.addAttribute("members", memberPage.getContent());
		return "admin/member/member_List"; // 返回會員列表頁面
	}


	@GetMapping("/{id}")
	public String showMemberDetail(@PathVariable Integer id, Model model) {
		MemberListDTO member = adminMemberService.findMemberById(id);			
		model.addAttribute("member", member);
		return "admin/member/member_Detail"; // 返回會員詳細頁面
	}
//----------------------------編輯------------------------------------

	@GetMapping("/edit/{id}")
	public String showEditForm(@PathVariable Integer id, Model model) {
		MemberListDTO member = adminMemberService.findMemberById(id);				
		model.addAttribute("member", member);
		return "admin/member/member_Edit"; // 返回編輯會員頁面
	}

	@PostMapping("/edit/{id}")
	public String updateMember(@PathVariable Integer id, @ModelAttribute("member") MemberListDTO updatedMember) {
		adminMemberService.updateMember(id, updatedMember);
		return "redirect:/admin/members/" + id;
	}
	@PostMapping("/disable/{id}")
	public String disableMember(@PathVariable Integer id) {
		adminMemberService.toggleMemberStatus(id);
		return "redirect:/admin/members/" + id; // 重定向到會員列表頁面
	}
}




