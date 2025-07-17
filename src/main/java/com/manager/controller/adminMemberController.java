package com.manager.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.manager.model.DTO.memberListDTO;
import com.manager.service.AdminMemberService;

@Controller
@RequestMapping("/admin/members")
public class adminMemberController {
	private final AdminMemberService adminMemberService;

	public adminMemberController(AdminMemberService adminMemberService) {
		this.adminMemberService = adminMemberService;
	}

	@GetMapping("/list")
	public String showPage(Model model) {
		List<memberListDTO> memberList = adminMemberService.findAllMembers();
		model.addAttribute("members", memberList);
		return "admin/member/member_List"; // 返回會員列表頁面
	}

	@GetMapping("/{Id}")
	public String showMemberDetail(@PathVariable Integer Id, Model model) {
		Optional<memberListDTO> member = adminMemberService.findMemberById(Id); // java8
		if (member.isPresent()) {
			model.addAttribute("member", member.get());
			return "admin/member/member_Detail"; // 返回會員詳細頁面
		} else {
			model.addAttribute("error", "Member not found");
			return "admin/member/member_List"; // 返回會員列表頁面
		}
	}
//----------------------------編輯------------------------------------

	@GetMapping("/{Id}/edit")
	public String showEditForm(@PathVariable Integer Id, Model model) {
		Optional<memberListDTO> member = adminMemberService.findMemberById(Id);
		if (member.isPresent()) {
			model.addAttribute("member", member.get());
			return "admin/member/member_Edit"; // 返回編輯會員頁面
		} else {
			model.addAttribute("error", "Member not found");
			return "admin/member/member_List"; // 返回會員列表頁面
		}
	}

//	@PostMapping("/{Id}/edit")
//	public String updateMember(@PathVariable Integer id, @ModelAttribute("member") memberListDTO updatedMember) {
//		adminMemberService.updateMember(id, updatedMember);
//		return "redirect:/admin/members/" + id;
//	}
}
