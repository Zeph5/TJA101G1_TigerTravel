package com.manager.service.Impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;

import org.springframework.stereotype.Service;

import com.manager.model.DTO.MemberListDTO;
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
	public MemberListDTO convertToDTO(memVO member) {
		MemberListDTO dto = new MemberListDTO();
		BeanUtils.copyProperties(member, dto);
		return dto;
	}
	
	@Override
	public List<MemberListDTO> findAllMembers() {
		List<memVO> members = memberRepository.findAll();
		// 將 List 轉為 Stream，然後進行映射
		return members.stream().map(member -> {
			MemberListDTO dto = new MemberListDTO();
			BeanUtils.copyProperties(member, dto);
			return dto;
		}).toList();

	}

	@Override
	public MemberListDTO findMemberById(Integer id) {		
		memVO member = memberRepository.findById(id) // <--回傳Optional<memVO>
				.orElseThrow(() -> new RuntimeException("Member not found with id: " + id));
		return convertToDTO(member);		
	}

	@Override
	public void updateMember(Integer id, MemberListDTO updatedMember) {
		Optional<memVO> optionalMember = memberRepository.findById(id);
		if (optionalMember.isPresent()) {
			memVO existingMember = optionalMember.get();
			existingMember.setMemberName(updatedMember.getMemberName());
			existingMember.setMemberEmail(updatedMember.getMemberEmail());
			existingMember.setMemberPhone(updatedMember.getMemberPhone());
			memberRepository.save(existingMember); // 保存更新後的會員資料
		} else {
			throw new RuntimeException("Member not found with id: " + id);
		}		
	}

	@Override
	public void toggleMemberStatus(Integer id) {
		memberRepository.findById(id).ifPresent(member -> {
			byte currentStatus = member.getMemberStatus();
			member.setMemberStatus(currentStatus == 1 ?(byte) 0 :(byte) 1); // 切換會員狀態
			memberRepository.save(member); // 保存更新後的會員資料
		});
		
	}
}
