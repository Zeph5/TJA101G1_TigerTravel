package com.manager.service.Impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.manager.model.Manager;

import com.manager.model.DTO.MemberListDTO;
import com.manager.repository.ManagerRepository;
import com.manager.service.AdminMemberService;
import com.member.model.MemberRepository;
import com.member.model.memVO;

import jakarta.persistence.criteria.Predicate;

@Service
public class AdminMemberServiceImpl implements AdminMemberService {

	private final MemberRepository memberRepository;
	private final ManagerRepository managerRepository;

	public AdminMemberServiceImpl(MemberRepository memberRepository, ManagerRepository managerRepository) {
		this.memberRepository = memberRepository;
		this.managerRepository = managerRepository;
	}
	
	@Override
	public MemberListDTO convertToDTO(memVO member) {
		MemberListDTO dto = new MemberListDTO();
		BeanUtils.copyProperties(member, dto);
		return dto;
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

	@Override
	public Page<MemberListDTO> searchAndFilter(String keyword, String status, Pageable pageable) {
		Specification<memVO> spec = (root, query, cb) -> {
	        List<Predicate> predicates = new ArrayList<>();
	        // 模糊搜尋：帳號、姓名、電話
	        if (keyword != null && !keyword.isBlank()) {
	            String likePattern = "%" + keyword.trim() + "%";
	            Predicate accountLike = cb.like(root.get("memberAccount"), likePattern);
	            Predicate nameLike = cb.like(root.get("memberName"), likePattern);
	            Predicate phoneLike = cb.like(root.get("memberPhone"), likePattern);
	            predicates.add(cb.or(accountLike, nameLike, phoneLike));
	        }

	        // 狀態篩選
	        if (status != null && !status.isBlank()){try {
	            Byte statusValue = Byte.parseByte(status.trim());
	            predicates.add(cb.equal(root.get("memberStatus"), statusValue));
	        } catch (NumberFormatException e) {
	            System.out.println("⚠️ 無法解析 status 值為 Byte：" + status);
	        }}

	        return cb.and(predicates.toArray(new Predicate[0]));
	    };

	    Page<memVO> memberPage = memberRepository.findAll(spec, pageable);

	    // 實體轉 DTO
	    return memberPage.map(member -> {
	        MemberListDTO dto = new MemberListDTO();
	        BeanUtils.copyProperties(member, dto);
	        return dto;
	    });
	}

	


}
