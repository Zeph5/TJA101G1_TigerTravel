package com.manager.model;


import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Manager {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name = "manager_name",nullable = false, length = 50)
	private String name;
	@Column(name = "manager_email", nullable = false, unique = true, length = 100)
	private String email;
	@Column(name = "manager_password", nullable = false, length = 100)
	private String password;
	@Column(name = "manager_account", nullable = false, unique = true, length = 50)
	private String account;
	@Column(name = "manager_create_date", nullable = false,updatable = false)
	private LocalDateTime createDate;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public LocalDateTime getCreateDate() {
		return createDate;
	}
	public void setCreateDate(LocalDateTime createDate) {
		this.createDate = createDate;
	}
	
	@jakarta.persistence.PrePersist  
//	執行 save() 新增資料時，JPA 會自動呼叫 onCreate() 方法
//	將當前時間寫入 createDate，就不需要在 Service 層手動設定了。
	protected void Oncreate() {
		this.createDate = LocalDateTime.now();
	}
	@Override
	public String toString() {
		return "Manager [id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + ", account="
				+ account + ", createDate=" + createDate + "]";
	}
	
	

}
