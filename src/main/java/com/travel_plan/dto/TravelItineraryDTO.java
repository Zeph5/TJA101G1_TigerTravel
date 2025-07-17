package com.travel_plan.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class TravelItineraryDTO {
	
	private List<TravelPlanDayDTO> dailyItineraries;
	
	@NotNull(message = "旅行行程ID不能為空")
	private Integer travelPlanId; // 旅行計畫ID

	private Integer travelItineraryId; // 旅行行程ID


	public Integer getTravelPlanId() {
		return travelPlanId;
	}

	public void setTravelPlanId(Integer travelPlanId) {
		this.travelPlanId = travelPlanId;
	}



	@NotNull(message = "最大旅遊人數不能為空")
	@Min(value = 1, message = "最大旅遊人數必須大於0")
	private Integer maxTourist; // 最大旅客數
	@NotNull(message = "總價格不能為空")
	@Min(value = 0, message = "總價格必須大於或等於0")
	private BigDecimal totalPrice; // 總價
	
    @NotNull(message = "旅行計畫開始日期不能為空")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @FutureOrPresent(message = "開始日期不能是過去的日期")
    private LocalDate startDate; // 旅行計畫開始日期
    
    @NotNull(message = "旅行計畫結束日期不能為空")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Future(message = "結束日期必須是未來的日期")
    private LocalDate endDate; // 旅行計畫結束日期
	
    
    
	public List<TravelPlanDayDTO> getDailyItineraries() {
		return dailyItineraries;
	}

	public void setDailyItineraries(List<TravelPlanDayDTO> dailyItineraries) {
		this.dailyItineraries = dailyItineraries;
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

	public Integer getTravelItineraryId() {
		return travelItineraryId;
	}

	public void setTravelItineraryId(Integer travelItineraryId) {
		this.travelItineraryId = travelItineraryId;
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
		return "TravelItineraryDTO [travelItineraryId=" + travelItineraryId + ", startDate=" + startDate + ", endDate="
				+ endDate + "]";
	}

}