package com.manager.service.Impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;

import org.springframework.stereotype.Service;

import com.manager.model.DTO.memberListDTO;
import com.manager.service.AdminMemberService;
import com.member.model.MemberRepository;
import com.member.model.memVO;

@Service
public class AdminMemberServiceImpl implements AdminMemberService {

	public final MemberRepository memberRepository;

	public AdminMemberServiceImpl(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@Override
	public List<memberListDTO> findAllMembers() {
		List<memVO> members = memberRepository.findAll();
		// 將 List 轉為 Stream，然後進行映射
		return members.stream().map(member -> {
			memberListDTO dto = new memberListDTO();
			BeanUtils.copyProperties(member, dto);
			return dto;
		}).toList();

	}

	@Override
	public Optional<memberListDTO> findMemberById(Integer id) {

		return memberRepository.findById(id) // <-- 這裡返回的就是 Optional<memVO>
				.map(member -> { // Optional 自己就有 map() 方法
					memberListDTO dto = new memberListDTO();
					BeanUtils.copyProperties(member, dto);
					return dto; // 返回 Optional<memberListDTO>
				});
	}
}
