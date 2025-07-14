package com.travel_plan.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.travel_plan.dto.TravelPlanDayDTO;
import com.travel_plan.model.TravelItinerary;
import com.travel_plan.model.TravelPlanDay;

import jakarta.validation.Valid;

public interface TravelPlanDayService {

	void createTravelPlanDayFromDto(TravelItinerary travelItinerary, @Valid TravelPlanDayDTO dto);

	List<TravelPlanDay> getDaysByItineraryId(Integer itineraryId);

	Optional<TravelPlanDayDTO> getTravelPlanDayDTOById(Integer id);

	void saveDailyItems(Integer itineraryId, LocalDate date, List<TravelPlanDayDTO> dailyItems);

	Integer calculateTravelDayNumber(Integer itineraryId, LocalDate currentDate);

	void updateTravelPlanDay(TravelPlanDayDTO travelPlanDayDTO, Integer itineraryId);	

}
