package com.travel_plan.service;



import java.util.Optional;

import com.travel_plan.dto.TravelItineraryDTO;
import com.travel_plan.dto.TravelPlanCreationDTO;
import com.travel_plan.model.TravelItinerary;
import com.travel_plan.model.TravelPlan;

import jakarta.validation.Valid;


public interface TravelItineraryService {	


	


	Optional<TravelItineraryDTO> getTravelItineraryById(Integer itineraryId);


	TravelItineraryDTO convertToItineraryDto(TravelItinerary entity);


	

	Optional<TravelItinerary> getTravelItineraryEntityById(Integer itineraryId);


	TravelItinerary createTravelItineraryFromDto(@Valid Integer planId, TravelItineraryDTO dto);
	
	Optional<TravelItineraryDTO> getTravelItineraryByTravelPlanId(Integer travelPlanId);//?

}
