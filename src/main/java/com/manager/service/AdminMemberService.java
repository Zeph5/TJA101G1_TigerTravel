package com.manager.service;

import java.util.List;
import java.util.Optional;

import com.manager.model.DTO.memberListDTO;

public interface AdminMemberService {

	List<memberListDTO> findAllMembers();

	Optional<memberListDTO> findMemberById(Integer id);
	

}
