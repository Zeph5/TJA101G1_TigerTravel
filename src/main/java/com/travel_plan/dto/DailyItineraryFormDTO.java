package com.travel_plan.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

public class DailyItineraryFormDTO {
	 private List<TravelPlanDayDTO> dailyItems;
	 @JsonFormat(pattern = "yyyy-MM-dd")
	 private LocalDate currentEditDate; // 方便回傳給前端渲染
	 private Integer travelDayNumber;
	 // 方便回傳給前端渲染
	 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	 private LocalDate traveltime;
	 
	 public DailyItineraryFormDTO() {
	        this.dailyItems = new ArrayList<>(); // 關鍵修改：初始化為空的 ArrayList
	    }
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
	
	

	public LocalDate getTraveltime() {
		return traveltime;
	}
	public void setTraveltime(LocalDate traveltime) {
		this.traveltime = traveltime;
	}
	@Override
	public String toString() {
		return "DailyItineraryFormDTO [dailyItems=" + dailyItems + ", currentEditDate=" + currentEditDate
				+ ", travelDayNumber=" + travelDayNumber + "]";
	}
	
}
