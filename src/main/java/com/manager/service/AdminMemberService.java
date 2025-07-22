package com.manager.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.manager.model.DTO.MemberListDTO;
import com.member.model.memVO;

public interface AdminMemberService {

	

	MemberListDTO findMemberById(Integer id);

	void updateMember(Integer id, MemberListDTO updatedMember);

	void toggleMemberStatus(Integer id);

	MemberListDTO convertToDTO(memVO member);

	

	Page<MemberListDTO> searchAndFilter(String keyword, String status, Pageable pageable);



	
	

}
