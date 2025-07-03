package com.travel_plan.service;



import java.util.Optional;

import com.travel_plan.dto.TravelItineraryDTO;
import com.travel_plan.model.TravelItinerary;
import com.travel_plan.model.TravelPlan;

import jakarta.validation.Valid;


public interface TravelItineraryService {

	void createTravelItineraryFromDto(@Valid Integer planId, TravelItineraryDTO dto);

	Optional<TravelItinerary> getTravelItineraryByTravelPlanId(Integer travelPlanId);

	Optional<TravelItinerary> getTravelItineraryById(Integer itineraryId);

	TravelItineraryDTO convertToDto(TravelItinerary entity);
	
	TravelItinerary convertToEntity(TravelItineraryDTO dto, TravelPlan travelPlan);

}
