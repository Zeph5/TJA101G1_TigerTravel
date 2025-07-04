package com.travel_plan.service.Impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.travel_plan.dto.TravelItineraryDTO;
import com.travel_plan.model.TravelItinerary;
import com.travel_plan.model.TravelPlan;
import com.travel_plan.service.TravelItineraryService;

import jakarta.validation.Valid;

@Service
public class TravelItineraryServiceImpl implements TravelItineraryService {

	@Override
	public void createTravelItineraryFromDto(@Valid Integer planId, TravelItineraryDTO dto) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Optional<TravelItinerary> getTravelItineraryByTravelPlanId(Integer travelPlanId) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public Optional<TravelItinerary> getTravelItineraryById(Integer itineraryId) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public TravelItineraryDTO convertToDto(TravelItinerary entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TravelItinerary convertToEntity(TravelItineraryDTO dto, TravelPlan travelPlan) {
		// TODO Auto-generated method stub
		return null;
	}}


