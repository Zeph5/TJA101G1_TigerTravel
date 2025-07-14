package com.travel_plan.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.travel_plan.model.TravelPlanDay;

@Repository
public interface TravelPlanDayRepository extends JpaRepository<TravelPlanDay, Integer> {

    // 查詢指定行程梯次與日期的每日行程
    

    // 根據行程梯次ID查詢所有每日行程
    List<TravelPlanDay> findByTravelItinerary_TravelItineraryId(Integer travelItineraryId);

    // 刪除指定行程梯次的所有每日行程
    void deleteByTravelItinerary_TravelItineraryId(Integer travelItineraryId);
    
    List<TravelPlanDay> findByTravelItinerary_TravelItineraryIdAndTraveltime(Integer travelItineraryId, LocalDate date);
}
