package com.manager.model.DTO;

import java.sql.Timestamp;

public class memberListDTO {
	private Integer memberId;
	private String memberAccount;
	private String memberName;
	private String memberEmail;
	private String memberPhone;
	private Byte memberStatus;
	private Timestamp createTime;
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
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	@Override
	public String toString() {
		return "memberListDTO [memberId=" + memberId + ", memberAccount=" + memberAccount + ", memberName=" + memberName
				+ ", memberEmail=" + memberEmail + ", memberPhone=" + memberPhone + ", memberStatus=" + memberStatus
				+ ", createTime=" + createTime + "]";
	}
	
	

	// constructor, getters, setters...
}