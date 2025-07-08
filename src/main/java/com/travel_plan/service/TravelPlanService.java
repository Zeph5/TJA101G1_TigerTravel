package com.travel_plan.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.travel_plan.dto.DailyItineraryFormDTO;
import com.travel_plan.dto.TravelItineraryDTO;
import com.travel_plan.dto.TravelPlanCreationDTO;
import com.travel_plan.dto.TravelPlanDayDTO;
import com.travel_plan.dto.TravelPlanPreviewDTO;
import com.travel_plan.model.TravelItinerary;
import com.travel_plan.model.TravelPlan;

import jakarta.validation.Valid;



public interface TravelPlanService {

	

	TravelPlanCreationDTO convertToCreationDto(TravelPlan entity);

	Optional<TravelPlanCreationDTO> getTravelPlanById(Integer id);


	List<TravelPlan> getAllTravelPlans();


	TravelPlan updateTravelPlan(Integer travelPlanId, @Valid TravelPlanCreationDTO dto, MultipartFile bannerImage);


	TravelPlan createTravelPlanFromDto(@Valid TravelPlanCreationDTO dto, MultipartFile bannerImage);


	Optional<TravelPlan> getTravelPlanEntityById(Integer planId);


	List<LocalDate> generateDatesBetween(LocalDate startDate, LocalDate endDate);


	 TravelItinerary getOrCreateTravelItineraryForPlan(Integer planId);


	DailyItineraryFormDTO getDailyItineraryFormDTO(Integer travelItineraryId, LocalDate firstDate,
			Integer travelDayNumber);
		

	public TravelPlanPreviewDTO getFullTravelPlanDetails(Integer planId);


	


	

	void saveDailyItems(Integer itineraryId, LocalDate date, List<TravelPlanDayDTO> dailyItems);
	
	List<TravelPlanDayDTO> getDailyItemsForDate(Integer itineraryId, LocalDate date);

	

	TravelItinerary saveTravelItineraryFromDto(@Valid TravelItineraryDTO dto);

	Integer calculateTravelDayNumber(Integer itineraryId, LocalDate currentDate);

	Optional<TravelItinerary> getTravelItineraryById(Integer travelItineraryId);

	Optional<TravelItinerary> getTravelItineraryForPlan(Integer travelPlanId);
	
}
