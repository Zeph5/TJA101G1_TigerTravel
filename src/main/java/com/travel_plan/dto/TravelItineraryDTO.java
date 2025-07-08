package com.travel_plan.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class TravelItineraryDTO {
	
	private Integer travelItineraryId; // 旅行行程ID
	
	private List<TravelPlanDayDTO> itineraryItems;
	
	
	
	public List<TravelPlanDayDTO> getItineraryItems() {
		return itineraryItems;
	}
	public void setItineraryItems(List<TravelPlanDayDTO> itineraryItems) {
		this.itineraryItems = itineraryItems;
	}

	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate startDate; // 行程開始日期
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate endDate; // 行程結束日期
	
	
	
	
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
