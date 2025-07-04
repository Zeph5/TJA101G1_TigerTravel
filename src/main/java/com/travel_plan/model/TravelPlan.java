package com.travel_plan.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class TravelPlan {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "travel_plan_id", nullable = false, unique = true)
	private Integer travelPlanId;
	
    // 從 travel_title 映射到 travelTitle
	@Column(name = "travel_title", nullable = false, length = 100)
	private String travelTitle; // <-- 改為 travelTitle
	
    // 從 travel_plan_description 映射到 travelPlanDescription
	@Column(name = "travel_plan_description", nullable = false, length = 500)
	private String travelPlanDescription; // <-- 改為 travelPlanDescription
	
    // 從 travel_plan_banner 映射到 travelPlanBannerUrl
	@Column(name = "travel_plan_banner", nullable = false, length = 255)
	private String travelPlanBannerUrl; // <-- 改為 travelPlanBannerUrl
	
	@Column(name = "published_date", nullable = false)
	@CreatedDate // 假設您使用了 @CreatedDate 來自動填充這個欄位
	private LocalDateTime publishedDate; // 這個已經是駝峰式，且與您上次改的 getter 匹配
	
	@Column(name = "last_modified_date", nullable = false)
	@LastModifiedDate 
	private LocalDateTime lastModifiedDate; // 這個也已經是駝峰式，且與您上次改的 getter 匹配
	
	@Column(name = "start_date", nullable = false)
	private LocalDate startDate; // 旅行計畫開始日期
	@Column(name = "end_date", nullable = false)
	private LocalDate endDate; // 旅行計畫結束日期
	
	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public TravelPlan() {
		// 預設建構子
	}
	
	public Integer getTravelPlanId() {
		return travelPlanId;
	}
	public void setTravelPlanId(Integer travelPlanId) {
		this.travelPlanId = travelPlanId;
	}
    
    // 更改 getter/setter 名稱以匹配新的屬性名稱
	public String getTravelTitle() { // <-- 改為 getTravelTitle
		return travelTitle;
	}
	public void setTravelTitle(String travelTitle) { // <-- 改為 setTravelTitle
		this.travelTitle = travelTitle;
	}
	public String getTravelPlanDescription() { // <-- 改為 getTravelPlanDescription
		return travelPlanDescription;
	}
	public void setTravelPlanDescription(String travelPlanDescription) { // <-- 改為 setTravelPlanDescription
		this.travelPlanDescription = travelPlanDescription;
	}
	public String getTravelPlanBannerUrl() { // <-- 改為 getTravelPlanBannerUrl
		return travelPlanBannerUrl;
	}
	public void setTravelPlanBannerUrl(String travelPlanBannerUrl) { // <-- 改為 setTravelPlanBannerUrl
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
				+ publishedDate + ", lastModifiedDate=" + lastModifiedDate + ", startDate=" + startDate + ", endDate="
				+ endDate + "]";
	}

	public Object getBannerImageUrl() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}