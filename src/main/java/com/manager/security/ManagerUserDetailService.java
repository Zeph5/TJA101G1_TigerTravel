package com.manager.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.manager.model.Manager;
import com.manager.repository.ManagerRepository;

@Service
public class ManagerUserDetailService implements UserDetailsService {

	@Autowired
	private ManagerRepository managerRepository;

	@Override
	public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
		Manager manager = managerRepository.findByAccount(account)
				.orElseThrow(() -> new UsernameNotFoundException("找不到使用者: " + account));
		return User.withUsername(manager.getAccount())
				.password(manager.getPassword())
				.roles("ADMIN") 
				.build();
	}
	
}
