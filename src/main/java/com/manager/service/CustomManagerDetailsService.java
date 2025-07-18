package com.manager.service;


import com.manager.model.Manager;
import com.manager.repository.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomManagerDetailsService implements UserDetailsService {

    @Autowired
    private ManagerRepository managerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Manager manager = managerRepository.findByAccount(username)
                .orElseThrow(() -> new UsernameNotFoundException("找不到帳號：" + username));
        return new CustomManagerDetails(manager);
    }
}
