package com.manager.model.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ManagerLoginDTO {
	private Integer managerId;
	
	@NotBlank(message = "帳號不能為空")
	@Size(min = 4, max = 20, message = "帳號長度必須在4到20個字元之間")
	private String account;
	@NotBlank(message = "密碼不能為空")
	@Size(min = 6, max = 20, message = "密碼長度必須在6到20個字元之間")
	private String password;
	
	public ManagerLoginDTO() {
		
	}
	public Integer getManagerId() {
		return managerId;
	}
	public void setManagerId(Integer managerId) {
		this.managerId = managerId;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	@Override
	public String toString() {
		return "ManagerDTO [managerId=" + managerId + ", account=" + account + ", password=" + password + "]";
	}
	

}
