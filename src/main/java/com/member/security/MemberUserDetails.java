package com.member.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.member.model.memVO;

//這裡是包裝會員資料給Spring Security使用

public class MemberUserDetails implements UserDetails {
	
	//將原本的會員物件存起來，讓UserDetails可以存取會員資料
	private final memVO member;//保證安全性
	
	//將會員資料傳進來，MemberDetailsService會使用到
	public MemberUserDetails(memVO member) {
		this.member = member;
	}
	
	//提供一個方法可以拿回原本的會員資料(例如登入成功後，你想顯示會員名稱可以用到這個)
	public memVO getMember() {
		return member;
	}

	//Spring會先預設問你：「這個使用者有什麼權限？什麼角色?」。若沒設定角色權限，就先給空的列表。
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.emptyList();//沒有角色權限控制的話就先空的
	}
	
	
	//回傳會員密碼，Spring Security會用這個來驗整使用者輸入的密碼是否正確
	@Override
	public String getPassword() {
		return member.getMemberPassword();
	}

	//等同上面密碼的功能一樣(像是findByAccount)
	@Override
	public String getUsername() {
		return member.getMemberAccount();
	}

	//這會告訴使用者這帳號是不是「還能用」
	@Override public boolean isAccountNonExpired() {return true;} //檢查帳號是否過期
	@Override public boolean isAccountNonLocked() {return true;} //檢查有沒有被鎖定
	@Override public boolean isCredentialsNonExpired() {return true;} //密碼是否過期?
	@Override public boolean isEnabled() {return true;} //帳號有啟用嗎?
	
}
