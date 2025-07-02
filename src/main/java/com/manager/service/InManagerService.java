package com.manager.service;

import com.manager.model.Manager;
import com.manager.model.DTO.ManagerLoginDTO;
import com.manager.model.DTO.ManagerRegisterDTO;

import jakarta.validation.Valid;

public interface InManagerService {

	String login(ManagerLoginDTO loginDTO);

	String register(ManagerRegisterDTO registerDTO);

	

	

}
