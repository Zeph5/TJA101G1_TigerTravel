package com.member.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "member")
public class memVO implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Integer memberId;

	@NotBlank(message = "帳號不可空白")
	@Size(min = 6, max = 20, message = "帳號長度需為 6~20 字")
	@Column(name = "mem_account", nullable = false, unique = true)
	private String memberAccount;

	@NotBlank(message = "密碼不可空白")
	@Size(min = 6, max = 20, message = "密碼長度需為 6~20 字")
	@Column(name = "mem_password", nullable = false)
	private String memberPassword;

	@NotBlank(message = "姓名不可空白")
	@Size(max = 20, message = "姓名長度不可超過 20 字")
	@Column(name = "mem_name", nullable = false)
	private String memberName;

	@Pattern(regexp = "^09\\d{8}$", message = "請輸入正確的手機格式（09開頭，共10碼）")
	@Column(name = "mem_phone")
	private String memberPhone;

	@NotBlank(message = "請輸入詳細地址")
	@Size(max = 100, message = "地址長度不可超過 100 字")
	@Column(name = "mem_address")
	private String memberAddress;

	@NotBlank(message = "Email 不可空白")
	@Email(message = "Email 格式不正確")
	@Column(name = "mem_email", nullable = false)
	private String memberEmail;

	@Column(name = "mem_status")
	private Byte memberStatus;

	@Lob
	@Basic(fetch = FetchType.EAGER)
	private byte[] avatar;

	@Column(updatable = false)
	@CreationTimestamp
	private Timestamp createTime;

	// ===== 驗證與安全相關 =====
	private Boolean emailVerified = false;
	private String verifyToken;
	private LocalDateTime verifyTokenCreatedTime;

	private String resetToken;
	private LocalDateTime resetTokenCreatedTime; 

	public String getResetToken() {
		return resetToken;
	}

	public void setResetToken(String resetToken) {
		this.resetToken = resetToken;
	}

	public LocalDateTime getResetTokenCreatedTime() {
		return resetTokenCreatedTime;
	}

	public void setResetTokenCreatedTime(LocalDateTime resetTokenCreatedTime) {
		this.resetTokenCreatedTime = resetTokenCreatedTime;
	}

	public Boolean getEmailVerified() {
		return emailVerified;
	}

	public void setEmailVerified(Boolean emailVerified) {
		this.emailVerified = emailVerified;
	}

	public String getVerifyToken() {
		return verifyToken;
	}

	public void setVerifyToken(String verifyToken) {
		this.verifyToken = verifyToken;
	}

	// ===============================================================
	public memVO() {
	}

	public memVO(Integer memberId, String memberAccount, String memberName, String memberPassword, String memberEmail,
			String memberPhone, Byte memberStatus, String memberAddress, Boolean emailVerified, String verifyToken,
			byte[] avatar, Timestamp createTime, LocalDateTime verifyTokenCreatedTime) {

		super();
		this.memberId = memberId;
		this.memberAccount = memberAccount;
		this.memberName = memberName;
		this.memberPassword = memberPassword;
		this.memberEmail = memberEmail;
		this.memberPhone = memberPhone;
		this.memberStatus = memberStatus;
		this.memberAddress = memberAddress;
		this.emailVerified = emailVerified;
		this.verifyToken = verifyToken;
		this.avatar = avatar;
		this.createTime = createTime;
		this.verifyTokenCreatedTime = verifyTokenCreatedTime;
	}

	public String getMemberAddress() {
		return memberAddress;
	}

	public void setMemberAddress(String memberAddress) {
		this.memberAddress = memberAddress;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public String getMemberAccount() {
		return memberAccount;
	}

	public void setMemberAccount(String memberAccount) {
		this.memberAccount = memberAccount;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getMemberPassword() {
		return memberPassword;
	}

	public void setMemberPassword(String memberPassword) {
		this.memberPassword = memberPassword;
	}

	public String getMemberEmail() {
		return memberEmail;
	}

	public void setMemberEmail(String memberEmail) {
		this.memberEmail = memberEmail;
	}

	public String getMemberPhone() {
		return memberPhone;
	}

	public void setMemberPhone(String memberPhone) {
		this.memberPhone = memberPhone;
	}

	public Byte getMemberStatus() {
		return memberStatus;
	}

	public void setMemberStatus(Byte memberStatus) {
		this.memberStatus = memberStatus;
	}

	public byte[] getAvatar() {
		return avatar;
	}

	public void setAvatar(byte[] avatar) {
		this.avatar = avatar;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	
	public boolean isEmailVerified() {
	    return Boolean.TRUE.equals(this.emailVerified);
	}
	
	public LocalDateTime getVerifyTokenCreatedTime() {
		return verifyTokenCreatedTime;
	}

	public void setVerifyTokenCreatedTime(LocalDateTime verifyTokenCreatedTime) {
		this.verifyTokenCreatedTime = verifyTokenCreatedTime;
	}

}
