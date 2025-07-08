package com.travel_plan.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TravelPlanPreviewDTO {
	private Integer travelPlanId;
    private String travelTitle;
    private String travelPlanDescription;
    private String travelPlanBannerUrl;
    // 梯次資訊
    private Integer travelItineraryId;
    private Integer maxTourist;
    private BigDecimal totalPrice;
    private LocalDate startDate;
    private LocalDate endDate;
    // 每日行程 (按日期分組)
    private Map<LocalDate, List<TravelPlanDayDTO>> dailyItineraries = new TreeMap<>(); // TreeMap 保持日期排序
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
	public Integer getTravelItineraryId() {
		return travelItineraryId;
	}
	public void setTravelItineraryId(Integer travelItineraryId) {
		this.travelItineraryId = travelItineraryId;
	}
	public Integer getMaxTourist() {
		return maxTourist;
	}
	public void setMaxTourist(Integer maxTourist) {
		this.maxTourist = maxTourist;
	}
	public BigDecimal getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}
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
	public Map<LocalDate, List<TravelPlanDayDTO>> getDailyItineraries() {
		return dailyItineraries;
	}
	public void setDailyItineraries(Map<LocalDate, List<TravelPlanDayDTO>> dailyItineraries) {
		this.dailyItineraries = dailyItineraries;
	}
	@Override
	public String toString() {
		return "TravelPlanPreviewDTO [travelPlanId=" + travelPlanId + ", travelTitle=" + travelTitle
				+ ", travelPlanDescription=" + travelPlanDescription + ", travelPlanBannerUrl=" + travelPlanBannerUrl
				+ ", travelItineraryId=" + travelItineraryId + ", maxTourist=" + maxTourist + ", totalPrice="
				+ totalPrice + ", startDate=" + startDate + ", endDate=" + endDate + ", dailyItineraries="
				+ dailyItineraries + "]";
	}
    
}
