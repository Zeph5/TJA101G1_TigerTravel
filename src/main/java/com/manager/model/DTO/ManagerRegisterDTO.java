package com.manager.model.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ManagerRegisterDTO {
	
	
	private Integer id;
	@NotBlank(message = "密碼不可為空")
	@Size(min = 6, max = 20, message = "密碼長度必須在6到20個字元之間")
	private String password;
	@NotBlank(message = "帳號不可為空")
	@Size(min = 4, max = 20, message = "帳號長度必須在4到20個字元之間")
	private String account;
	@NotBlank(message = "姓名不可為空")
	private String name;
	@NotBlank(message = "電子郵件不可為空")
	@Email(message = "請輸入有效的電子郵件地址")
	private String email;
	@NotBlank(message = "請再次確認密碼")
	private String confirmPassword; //確認密碼
	@NotBlank(message = "驗證碼不可為空")
	private String verificationCode; //驗證碼
	public Integer getid() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getConfirmPassword() {
		return confirmPassword;
	}
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	public String getVerificationCode() {
		return verificationCode;
	}
	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}
	@Override
	public String toString() {
		return "ManagerRegisterDTO [Id=" + id + ", password=" + password + ", account=" + account + ", name=" + name
				+ ", email=" + email + ", confirmPassword=" + confirmPassword + ", verificationCode=" + verificationCode
				+ "]";
	}
	
	
}
