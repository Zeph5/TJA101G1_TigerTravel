package com.travel_plan.service;

import com.travel_plan.dto.TravelPlanDayDTO;
import com.travel_plan.model.TravelItinerary;

import jakarta.validation.Valid;

public interface TravelPlanDayService {

	void createTravelPlanDayFromDto(TravelItinerary travelItinerary, @Valid TravelPlanDayDTO dto);

}
