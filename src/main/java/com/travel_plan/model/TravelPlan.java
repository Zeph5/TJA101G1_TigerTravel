package com.travel_plan.model;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;


@Entity
@EntityListeners(AuditingEntityListener.class)
public class TravelPlan {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "travel_plan_id", nullable = false, unique = true)
	private Integer travelPlanId;
	
    // 從 travel_title 映射到 travelTitle
	@Column(name = "travel_title", nullable = false, length = 100)
	private String travelTitle; 
	
    
	@Column(name = "travel_plan_description", nullable = false, length = 500)
	private String travelPlanDescription; 
	
    
	@Column(name = "travel_plan_banner", nullable = false, length = 255)
	private String travelPlanBannerUrl; 
	
	@Column(name = "published_date", nullable = false)
	@CreatedDate 
	private LocalDateTime publishedDate; 
	
	@Column(name = "last_modified_date", nullable = false)
	@LastModifiedDate 
	private LocalDateTime lastModifiedDate; 
	
	
	public TravelPlan() {
		// 預設建構子
	}
	
	public Integer getTravelPlanId() {
		return travelPlanId;
	}
	public void setTravelPlanId(Integer travelPlanId) {
		this.travelPlanId = travelPlanId;
	}
    
    
	public String getTravelTitle() {
		return travelTitle;
	}
	public void setTravelTitle(String travelTitle) { 
		this.travelTitle = travelTitle;
	}
	public String getTravelPlanDescription() { 
		return travelPlanDescription;
	}
	public void setTravelPlanDescription(String travelPlanDescription) { 
		this.travelPlanDescription = travelPlanDescription;
	}
	public String getTravelPlanBannerUrl() { 
		return travelPlanBannerUrl;
	}
	public void setTravelPlanBannerUrl(String travelPlanBannerUrl) {
		this.travelPlanBannerUrl = travelPlanBannerUrl;
	}
	public LocalDateTime getPublishedDate() {
		return publishedDate;
	}
	public void setPublishedDate(LocalDateTime publishedDate) {
		this.publishedDate = publishedDate;
	}
	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}
	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	

	@Override
	public String toString() {
		return "TravelPlan [travelPlanId=" + travelPlanId + ", travelTitle=" + travelTitle + ", travelPlanDescription="
				+ travelPlanDescription + ", travelPlanBannerUrl=" + travelPlanBannerUrl + ", publishedDate="
				+ publishedDate + ", lastModifiedDate=" + lastModifiedDate + "]";
	}

	public Object getBannerImageUrl() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}