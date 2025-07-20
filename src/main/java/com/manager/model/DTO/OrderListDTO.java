package com.manager.model.DTO;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;

import com.member.model.TouristIdVO;
import com.member.model.memVO;
import com.travel_plan.model.TravelItinerary;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

public class OrderListDTO {
	private Integer tourOrderId;
	private String memberName;
	private Integer travelItineraryId;
	private Integer totalAmount;
	private String tourOrderStatus;
	private LocalDateTime createTime;
	private Integer peopleCount;
	

	public Integer getPeopleCount() {
		return peopleCount;
	}

	public void setPeopleCount(Integer peopleCount) {
		this.peopleCount = peopleCount;
	}

	// Getter & Setter
	public Integer getTourOrderId() {
		return tourOrderId;
	}

	public void setTourOrderId(Integer tourOrderId) {
		this.tourOrderId = tourOrderId;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public Integer getTravelItineraryId() {
		return travelItineraryId;
	}

	public void setTravelItineraryId(Integer travelItineraryId) {
		this.travelItineraryId = travelItineraryId;
	}

	public Integer getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Integer totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getTourOrderStatus() {
		return tourOrderStatus;
	}

	public void setTourOrderStatus(String tourOrderStatus) {
		this.tourOrderStatus = tourOrderStatus;
	}

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	}

	public OrderListDTO(Integer tourOrderId, String memberName, Integer travelItineraryId,
            Integer totalAmount, String tourOrderStatus, LocalDateTime createTime,
            Integer peopleCount) {
this.tourOrderId = tourOrderId;
this.memberName = memberName;
this.travelItineraryId = travelItineraryId;
this.totalAmount = totalAmount;
this.tourOrderStatus = tourOrderStatus;
this.createTime = createTime;
this.peopleCount = peopleCount;
}
	public OrderListDTO() {
		// 預設建構子
	}
}
