package com.travel_plan.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.travel_plan.dto.TravelPlanDayDTO;
import com.travel_plan.model.TravelPlanDay;

@Repository
public interface TravelPlanDayRepository extends JpaRepository<TravelPlanDay, Integer> {  
    

    // 根據行程梯次ID查詢所有每日行程
    List<TravelPlanDay> findByTravelItinerary_TravelItineraryId(Integer travelItineraryId);
    
    // 根據行程梯次ID和日期查詢每日行程且排序
    List<TravelPlanDay> findByTravelItinerary_TravelItineraryIdAndTraveltimeOrderByTravelSequenceNumberAsc(Integer itineraryId, LocalDate date);

    // 刪除指定行程梯次的所有每日行程
    void deleteByTravelItinerary_TravelItineraryId(Integer travelItineraryId);
    
    List<TravelPlanDay> findByTravelItinerary_TravelItineraryIdAndTraveltime(Integer travelItineraryId, LocalDate date);

 // ✅ Repository 方法
    List<TravelPlanDay> findByTravelItinerary_TravelItineraryIdOrderByTravelSequenceNumber(Integer itineraryId);

}
