package com.manager.model.DTO;

public class TouristListDTO {
	
	private Integer touristId;
	private String touristName;
	private String touristPersonalId;
	private String contactNumber;
	private Integer peopleCount;
	
	public TouristListDTO() {}
	public TouristListDTO(Integer touristId, String touristName, String touristPersonalId, String contactNumber, Integer peopleCount) {
		this.touristId = touristId;
		this.touristName = touristName;
		this.touristPersonalId = touristPersonalId;
		this.contactNumber = contactNumber;
		this.peopleCount = peopleCount;
	}
	public Integer getTouristId() {
		return touristId;
	}
	public void setTouristId(Integer touristId) {
		this.touristId = touristId;
	}
	public String getTouristName() {
		return touristName;
	}
	public void setTouristName(String touristName) {
		this.touristName = touristName;
	}
	public String getTouristPersonalId() {
		return touristPersonalId;
	}
	public void setTouristPersonalId(String touristPersonalId) {
		this.touristPersonalId = touristPersonalId;
	}
	public String getContactNumber() {
		return contactNumber;
	}
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}
	public Integer getPeopleCount() {
		return peopleCount;
	}
	public void setPeopleCount(Integer peopleCount) {
		this.peopleCount = peopleCount;
	}
	
}
