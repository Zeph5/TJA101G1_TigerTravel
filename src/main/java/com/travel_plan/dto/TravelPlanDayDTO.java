package com.travel_plan.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class TravelPlanDayDTO {
	
	private Integer travelPlanDayId; // 用於編輯模式   

    @NotNull(message = "景點ID不能為空")
    @Min(value = 1, message = "景點ID必須大於0")
    private Integer sceneryId; // 如果景點是從外部系統或固定列表選擇的 ID

    @NotNull(message = "天數不能為空")
    @Min(value = 1, message = "天數必須大於0")
    private Integer travelDayNumber;

    @NotNull(message = "行程順序不能為空")
    @Min(value = 1, message = "行程順序必須大於0")
    private Integer travelSequenceNumber;
    
    @NotNull(message = "旅行日期不能為空")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate traveltime;

	public Integer getTravelPlanDayId() {
		return travelPlanDayId;
	}

	public void setTravelPlanDayId(Integer travelPlanDayId) {
		this.travelPlanDayId = travelPlanDayId;
	}

	public Integer getSceneryId() {
		return sceneryId;
	}

	public void setSceneryId(Integer sceneryId) {
		this.sceneryId = sceneryId;
	}

	public Integer getTravelDayNumber() {
		return travelDayNumber;
	}

	public void setTravelDayNumber(Integer travelDayNumber) {
		this.travelDayNumber = travelDayNumber;
	}

	public Integer getTravelSequenceNumber() {
		return travelSequenceNumber;
	}

	public void setTravelSequenceNumber(Integer travelSequenceNumber) {
		this.travelSequenceNumber = travelSequenceNumber;
	}

	public LocalDate getTraveltime() {
		return traveltime;
	}

	public void setTraveltime(LocalDate traveltime) {
		this.traveltime = traveltime;
	}

	@Override
	public String toString() {
		return "TravelPlanDayDTO [travelPlanDayId=" + travelPlanDayId + ", sceneryId=" + sceneryId
				+ ", travelDayNumber=" + travelDayNumber + ", travelSequenceNumber=" + travelSequenceNumber
				+ ", traveltime=" + traveltime + "]";
	}
    
    
}
