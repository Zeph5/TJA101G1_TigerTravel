package com.travel_plan.service;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.travel_plan.dto.TravelPlanCreationDTO;
import com.travel_plan.model.TravelPlan;

import jakarta.validation.Valid;

public interface TravelPlanService {

	

	List<TravelPlan> getAllTravelPlans();

	Optional<TravelPlan> getTravelPlanById(Integer id);

	static TravelPlan getTravelPlanWithItinerary(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	TravelPlanCreationDTO convertToDto(TravelPlan existingPlan);

	TravelPlan createTravelPlanFromDto(@Valid TravelPlanCreationDTO dto, MultipartFile bannerImage);

	TravelPlan updateTravelPlan(Integer travelPlanId, @Valid TravelPlanCreationDTO dto, MultipartFile bannerImage);

	
}
