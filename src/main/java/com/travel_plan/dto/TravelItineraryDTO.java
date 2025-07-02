package com.travel_plan.dto;

import java.math.BigDecimal;
import java.time.LocalDate;


import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class TravelItineraryDTO {
	
	private Integer travelItineraryId; // 旅行行程ID
	
	@NotNull(message = "最大旅遊人數不能為空")
    @Min(value = 1, message = "最大旅遊人數必須大於0")
	private Integer maxTourist; // 最大旅客數
	@NotNull(message = "總價格不能為空")
    @Min(value = 0, message = "總價格必須大於或等於0")
	private BigDecimal totalPrice; // 總價
	@NotNull(message = "開始日期不能為空")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate startDate; // 開始日期
	@NotNull(message = "結束日期不能為空")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate endDate; // 結束日期
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
	@Override
	public String toString() {
		return "TravelItineraryDTO [travelItineraryId=" + travelItineraryId + ", maxTourist=" + maxTourist
				+ ", totalPrice=" + totalPrice + ", startDate=" + startDate + ", endDate=" + endDate + "]";
	}
	
	
	
}
