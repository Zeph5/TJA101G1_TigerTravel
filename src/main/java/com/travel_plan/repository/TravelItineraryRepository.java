package com.travel_plan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.travel_plan.model.TravelItinerary; // 確保這是您的 JPA Entity
import java.util.Optional;

@Repository
public interface TravelItineraryRepository extends JpaRepository<TravelItinerary, Integer> {

	Optional<TravelItinerary> findByTravelPlan_TravelPlanId(Integer travelPlanId);
    
}