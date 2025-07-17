package com.member.model.memberTour;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.travel_plan.model.TravelPlan;
import com.travel_plan.model.TravelPlanDay;

@Repository
public interface MemTravelPlanDayRepository extends JpaRepository<TravelPlanDay, Integer> {
	List<TravelPlanDay> findByTravelItinerary_TravelItineraryId(Integer itineraryId);
	
	List<TravelPlanDay> findByTravelPlan_TravelPlanIdOrderByTravelDayNumber(Integer travelPlanId);
	
	@Query("SELECT d FROM TravelPlanDay d JOIN FETCH d.scenery s JOIN FETCH s.sceneryImages WHERE d.travelItinerary.travelItineraryId = :itineraryId ORDER BY d.travelDayNumber")
	List<TravelPlanDay> findByTravelItinerary_TravelItineraryIdOrderByTravelDayNumber(@Param("itineraryId") Integer itineraryId);
}
