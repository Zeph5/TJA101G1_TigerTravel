package com.manager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


import com.manager.model.ResponseMessage;
import com.manager.model.DTO.ManagerLoginDTO;
import com.manager.model.DTO.ManagerRegisterDTO;
import com.manager.service.InManagerService;


import jakarta.validation.Valid;

@RestController
public class ManagerController {
	@Autowired
	private InManagerService inManagerService;
	
	@PostMapping("/manager/login")
	public ResponseEntity<ResponseMessage<String>> login(@Valid @RequestBody ManagerLoginDTO loginDTO) {
		try {
			String managerLogin = InManagerService.login(loginDTO);
			return ResponseEntity.ok(ResponseMessage.success("200",managerLogin));
		}catch(IllegalArgumentException e) {
			return ResponseEntity
					.status(HttpStatus.UNAUTHORIZED)
					.body(ResponseMessage.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
		}
	}
	@PostMapping("/manager/register")
	public ResponseEntity<ResponseMessage<String>> register(@Valid @RequestBody ManagerRegisterDTO registerDTO) {
		try {
			String managerRegister = InManagerService.register(registerDTO);
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(ResponseMessage.success("註冊成功","帳號已創建"));
		} catch (IllegalArgumentException e) {
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body(ResponseMessage.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
		}
	
	}

}
