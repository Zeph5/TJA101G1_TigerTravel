	package com.manager.service;
	
	import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.manager.model.Manager;
import com.manager.model.DTO.ManagerLoginDTO;
	import com.manager.model.DTO.ManagerRegisterDTO;
import com.manager.repository.ManagerRepository;

	
	
	@Service
	public class ManagerService implements InManagerService {
		
		@Autowired
		private ManagerRepository managerRepository;
		
		@Override
		public String login(ManagerLoginDTO loginDTO) {
			return null;
					
		}
	
		@Override
		public String register(ManagerRegisterDTO registerDTO) {
			if (managerRepository.findByAccount(registerDTO.getAccount()).isPresent()) {
				throw new IllegalArgumentException("帳號已存在");
			}
			if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
	            throw new IllegalArgumentException("密碼與確認密碼不符");
	        }
			Manager managerRegister = new Manager();
			BeanUtils.copyProperties(registerDTO, managerRegister);
//			managerRegister.setAccount(registerDTO.getAccount());
//			managerRegister.setPassword(registerDTO.getPassword());
//			managerRegister.setName(registerDTO.getName());
//			managerRegister.setEmail(registerDTO.getEmail());
			
			managerRepository.save(managerRegister);
			return "帳號已創建";
		}
		
		
	
		
	
	}
