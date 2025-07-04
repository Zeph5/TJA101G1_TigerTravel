package com.travel_plan.service;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.travel_plan.dto.TravelPlanCreationDTO;
import com.travel_plan.model.TravelPlan;

import jakarta.validation.Valid;

public interface TravelPlanService {

	

	TravelPlanCreationDTO convertToCreationDto(TravelPlan entity);


	


	Optional<TravelPlanCreationDTO> getTravelPlanById(Integer id);


	List<TravelPlan> getAllTravelPlans();


	TravelPlan updateTravelPlan(Integer travelPlanId, @Valid TravelPlanCreationDTO dto, MultipartFile bannerImage);


	TravelPlan createTravelPlanFromDto(@Valid TravelPlanCreationDTO dto, MultipartFile bannerImage);





	Optional<TravelPlan> getTravelPlanEntityById(Integer planId);
	
}
