package com.travel_plan.dto;

import java.time.LocalDate;
import java.util.List;

public class DailyItineraryFormDTO {
	 private List<TravelPlanDayDTO> dailyItems;
	 private LocalDate currentEditDate; // 方便回傳給前端渲染
	 private Integer travelDayNumber;
	public List<TravelPlanDayDTO> getDailyItems() {
		return dailyItems;
	}
	public void setDailyItems(List<TravelPlanDayDTO> dailyItems) {
		this.dailyItems = dailyItems;
	}
	public LocalDate getCurrentEditDate() {
		return currentEditDate;
	}
	public void setCurrentEditDate(LocalDate currentEditDate) {
		this.currentEditDate = currentEditDate;
	}
	public Integer getTravelDayNumber() {
		return travelDayNumber;
	}
	public void setTravelDayNumber(Integer travelDayNumber) {
		this.travelDayNumber = travelDayNumber;
	} // 方便回傳給前端渲染
	@Override
	public String toString() {
		return "DailyItineraryFormDTO [dailyItems=" + dailyItems + ", currentEditDate=" + currentEditDate
				+ ", travelDayNumber=" + travelDayNumber + "]";
	}
	
}
