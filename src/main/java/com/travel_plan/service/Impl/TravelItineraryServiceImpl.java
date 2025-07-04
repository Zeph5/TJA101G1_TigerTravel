package com.travel_plan.service.Impl;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.travel_plan.dto.TravelItineraryDTO;
import com.travel_plan.dto.TravelPlanCreationDTO;
import com.travel_plan.model.TravelItinerary;
import com.travel_plan.model.TravelPlan;
import com.travel_plan.repository.TravelItineraryRepository;
import com.travel_plan.repository.TravelPlanRepository;
import com.travel_plan.service.TravelItineraryService;

import jakarta.validation.Valid;

@Service
public class TravelItineraryServiceImpl implements TravelItineraryService {
	
	@Autowired
	private TravelItineraryRepository travelItineraryRepository;
	@Autowired
	private TravelPlanRepository travelPlanRepository;

	
	@Override
	public Optional<TravelItineraryDTO> getTravelItineraryById(Integer itineraryId) {
		return travelItineraryRepository.findById(itineraryId)
				.map(this::convertToItineraryDto);
		
	}
	@Override
	public TravelItineraryDTO convertToItineraryDto(TravelItinerary entity) {
		if (entity == null) {
			return null;
		}
		TravelItineraryDTO dto = new TravelItineraryDTO();
		BeanUtils.copyProperties(entity, dto);
		return dto;
	}
	
	
	@Override
	public Optional<TravelItinerary> getTravelItineraryEntityById(Integer itineraryId) {
		return travelItineraryRepository.findById(itineraryId);
	}
	@Override
	@Transactional
	public TravelItinerary createTravelItineraryFromDto(@Valid Integer planId, TravelItineraryDTO dto) {
		if (dto == null) {
			throw new IllegalArgumentException("TravelItineraryDTO 不能為 null。");
		}
		
		TravelItinerary entity = new TravelItinerary();
		BeanUtils.copyProperties(dto, entity);
		
		TravelPlan travelPlan = travelPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("找不到 ID 為 " + planId + " 的旅行計畫。"));
		entity.setTravelPlan(travelPlan);

		// 儲存 TravelItinerary 實體到資料庫
		TravelItinerary savedEntity = travelItineraryRepository.save(entity);
		return savedEntity;}
	
	
	
	@Override
	public Optional<TravelItineraryDTO> getTravelItineraryByTravelPlanId(Integer travelPlanId) {
	    return travelItineraryRepository.findByTravelPlan_TravelPlanId(travelPlanId)
	            .map(this::convertToItineraryDto);
	}
}
	


