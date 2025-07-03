package com.member.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.member.model.MemberRepository;
import com.member.model.memVO;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //會員註冊(新增)動作
    public memVO register(memVO member) {
        // 可加上帳號是否存在判斷、密碼加密等
    	if(memberRepository.findByMemberAccount(member.getMemberAccount()).isPresent()) {
    		throw new RuntimeException("帳號已經存在喔！");
    	}
        return memberRepository.save(member);
    }
    
    //查詢所有會員
    public List<memVO> findAllMembers() {
        return memberRepository.findAll();
    }

    //使用id來查詢會員
    public Optional<memVO> findById(Integer id){
    	return memberRepository.findById(id);
    }

    //用帳號來查詢會員(帳號登入用)
    public Optional<memVO> findByAccount(String memberAccount) {
        return memberRepository.findByMemberAccount(memberAccount);
    }

    
    //++新增 或 修改會員 (ID有值就更新，沒值就新增)
    public memVO save(memVO member) {
    	return memberRepository.save(member);
    }
    
    //XX刪除會員
    public void deleteById(Integer id) {
    	memberRepository.deleteById(id);
    }
    
}

