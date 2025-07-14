package com.travel_plan.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.travel_plan.model.TravelItinerary;
import com.travel_plan.model.TravelPlanDay;

public class TravelPlanPreviewDTO {

    private Integer travelPlanId;
    private String travelTitle;
    private String travelPlanDescription;
    private String travelPlanBannerUrl;

    private Integer travelItineraryId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer maxTourist;
    private BigDecimal totalPrice;

    private Map<LocalDate, List<TravelPlanDayDTO>> dailyItineraries;

    // === Getter/Setter ===
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

    

    public Map<LocalDate, List<TravelPlanDayDTO>> getDailyItineraries() {
		return dailyItineraries;
	}

	public void setDailyItineraries(Map<LocalDate, List<TravelPlanDayDTO>> dailyItineraries) {
		this.dailyItineraries = dailyItineraries;
	}

	// Optional: 方法供 Thymeleaf 調用計算第幾天
    public int calculateTravelDayNumber(Integer itineraryId, LocalDate date) {
        return (int) (date.toEpochDay() - startDate.toEpochDay()) + 1;
    }
}
