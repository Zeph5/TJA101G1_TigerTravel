package com.member.model;

import java.io.Serializable;
import java.sql.Timestamp;
import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;

@Entity // 告訴Spring這是一個對應資料表的物件
@Table(name = "member") // 指定對應的資料表名稱(必須要和我的資料庫一致)
public class memVO implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer memberId;

	@Column(name = "mem_account", nullable = false, unique = true) // 不允許為null,必須為唯一
	private String memberAccount;

	@Column(name = "mem_name", nullable = false) // 不允許為null
	private String memberName;

	@Column(name = "mem_password", nullable = false) // 不允許為null
	private String memberPassword;

	@Column(name = "mem_email")
	private String memberEmail;

	@Column(name = "mem_phone")
	private String memberPhone;

	@Column(name = "mem_status")
	private String memberStatus;

	@Column(name = "mem_address")
	private String memberAddress;

	@Lob
	@Basic(fetch = FetchType.EAGER)
	// 這樣在Hibernate查詢會員時，就會自動讀出圖片資
//	@Transient
	private byte[] avatar;

	@Column(updatable = false)
	@CreationTimestamp
	private Timestamp createTime;

	//=====驗證功能=======
	private Boolean emailVerified = false; // 是否以驗證信箱
	private String verifyToken; // 驗證用的唯一 token

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
			String memberPhone, String memberStatus, String memberAddress, Boolean emailVerified, String verifyToken,
			byte[] avatar, Timestamp createTime) {

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

	public String getMemberStatus() {
		return memberStatus;
	}

	public void setMemberStatus(String memberStatus) {
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

}
