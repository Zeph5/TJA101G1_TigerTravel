package com.travel_plan.service;

import java.util.List;
import java.util.Optional;

import com.travel_plan.dto.TravelPlanCreationDTO;
import com.travel_plan.model.TravelPlan;

import jakarta.validation.Valid;

public interface TravelPlanService {

	TravelPlan createTravelPlanFromDto(@Valid TravelPlanCreationDTO dto);

	List<TravelPlan> getAllTravelPlans();

	Optional<TravelPlan> getTravelPlanById(Integer id);

}
