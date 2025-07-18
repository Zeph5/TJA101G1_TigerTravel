package com.manager.service;

import java.util.List;
import java.util.Optional;

import com.manager.model.DTO.MemberListDTO;
import com.member.model.memVO;

public interface AdminMemberService {

	List<MemberListDTO> findAllMembers();

	MemberListDTO findMemberById(Integer id);

	void updateMember(Integer id, MemberListDTO updatedMember);

	void toggleMemberStatus(Integer id);

	MemberListDTO convertToDTO(memVO member);
	

}
