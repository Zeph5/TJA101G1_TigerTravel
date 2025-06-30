package com.member.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.member.model.MemberRepository;
import com.member.model.memVO;


/*是整個 Spring Security 登入流程中最重要的一個核心元件之一
「當使用者在登入畫面輸入帳號時，Spring Security會來這裡詢問你：這個帳號有沒有存在?密碼是什麼?是否可以登入?」*/

//Service 讓Spring把這個類別自動註冊成一個可以被注入(@Autowired)的Bean
@Service						//實作介面：我要告訴你怎麼查帳號
public class MemberUserDetailsService implements UserDetailsService {

	@Autowired 
	private MemberRepository memberRepository;
	
	
	@Override //Security 在登入時會自動呼叫這個方法，把使用者輸入的帳號當成username傳進來
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<memVO> optionalMember = memberRepository.findByMemberAccount(username);
		
		if(optionalMember.isEmpty()) {
			throw new UsernameNotFoundException("帳號不存在：" + username);
		}
		
		memVO member = optionalMember.get();
		
		System.out.println("查詢帳號：" + username);
		System.out.println("查詢結果：" + optionalMember);

		
		return new MemberUserDetails(member); //包裝成 Spring Security 能用的格式
	}

}
