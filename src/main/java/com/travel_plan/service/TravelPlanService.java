package com.travel_plan.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

	
	List<TravelPlanDayDTO> getDailyItemsForDate(Integer itineraryId, LocalDate date);
	

	TravelItinerary saveTravelItineraryFromDto(@Valid TravelItineraryDTO dto);
	

	Optional<TravelItinerary> getTravelItineraryById(Integer travelItineraryId);

	Optional<TravelItinerary> getTravelItineraryForPlan(Integer travelPlanId);

	List<LocalDate> generateItineraryDates(LocalDate startDate, LocalDate endDate);

	TravelPlanPreviewDTO getTravelPlanPreview(Integer itineraryId);

	int calculateTotalDays(LocalDate startDate, LocalDate endDate);

	TravelPlanPreviewDTO getTravelPlanPreview(Integer planId, Integer itineraryId);
	
	List<TravelPlan> searchByTitle(String keyword);

	Optional<TravelPlan> findById(Integer id); //01新增
	List<TravelItinerary> findItinerariesByPlanId(Integer planId);//01新增

	void deleteById(Integer id);

	void deleteAllByIds(List<Integer> planIds);

	Page<TravelPlan> getTravelPlans(PageRequest of);
	
}
