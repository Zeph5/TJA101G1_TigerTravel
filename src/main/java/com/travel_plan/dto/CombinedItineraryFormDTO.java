package com.travel_plan.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class CombinedItineraryFormDTO {

    // 從 TravelItinerary 獲取的基本資訊
    private Integer travelItineraryId; // 編輯時會帶有ID，新增時為null
    @NotNull(message = "最大旅客數不能為空")
    private Integer maxTourist;
    private BigDecimal totalPrice; // 可以為空，或設定預設值

    // 每日行程的資訊，使用 Map 方便按日期存取
    // 鍵是 LocalDate，值是該天的 DailyItineraryFormDTO
    @Valid // 啟用對 DailyItineraryFormDTO 內部屬性的驗證
    private Map<LocalDate, DailyItineraryFormDTO> dailyItineraryMap;

    // 可能會需要一個 List<DailyItineraryFormDTO> dailyItineraryList;
    // 這取決於你的前端如何收集每日行程的數據。
    // 如果前端以列表形式提交，則這裡用 List。如果按日期提交，則用 Map。

    // Getters and Setters
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

    public Map<LocalDate, DailyItineraryFormDTO> getDailyItineraryMap() {
        return dailyItineraryMap;
    }

    public void setDailyItineraryMap(Map<LocalDate, DailyItineraryFormDTO> dailyItineraryMap) {
        this.dailyItineraryMap = dailyItineraryMap;
    }
}