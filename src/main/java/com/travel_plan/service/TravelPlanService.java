package com.travel_plan.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.travel_plan.dto.DailyItineraryFormDTO;
import com.travel_plan.dto.TravelPlanCreationDTO;
import com.travel_plan.dto.TravelPlanDayDTO;
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
		

	TravelPlanCreationDTO getFullTravelPlanDetails(Integer planId);


	Integer calculateTravelDayNumber(LocalDate planStartDate, LocalDate currentDate);


	void saveDailyItinerary(Integer itineraryId, LocalDate date, List<TravelPlanDayDTO> dailyItems);

	void saveDailyItems(Integer itineraryId, LocalDate date, List<TravelPlanDayDTO> dailyItems);
	
	List<TravelPlanDayDTO> getDailyItemsForDate(Integer itineraryId, LocalDate date);

	TravelPlan findById(Integer travelPlanId);
	
}
