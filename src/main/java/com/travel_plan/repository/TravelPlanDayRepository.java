package com.travel_plan.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.travel_plan.model.TravelPlanDay;

public interface TravelPlanDayRepository extends JpaRepository<TravelPlanDay, Integer> {
	
	// 這裡可以添加自定義查詢方法，例如根據行程 ID 和日期查找行程天數
	


	List<TravelPlanDay> findByTravelItinerary_TravelItineraryIdAndTravelTime(Integer travelItineraryId, LocalDate date);



	List<TravelPlanDay> findByTravelItinerary_TravelItineraryId(Integer travelItineraryId);
	
	// 其他自定義查詢方法可以根據需要添加

}
