package com.travel_plan.service.Impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scenery.model.SceneryRepository;
import com.scenery.model.SceneryVO;
import com.travel_plan.dto.TravelItineraryDTO;
import com.travel_plan.dto.TravelPlanCreationDTO;
import com.travel_plan.dto.TravelPlanDayDTO;
import com.travel_plan.model.TravelItinerary;
import com.travel_plan.model.TravelPlan;
import com.travel_plan.model.TravelPlanDay;
import com.travel_plan.repository.TravelItineraryRepository;
import com.travel_plan.repository.TravelPlanDayRepository;
import com.travel_plan.repository.TravelPlanRepository;
import com.travel_plan.service.TravelItineraryService;

import jakarta.validation.Valid;

@Service
public class TravelItineraryServiceImpl implements TravelItineraryService {
	
	@Autowired
	private TravelItineraryRepository travelItineraryRepository;
	@Autowired
	private TravelPlanRepository travelPlanRepository;
	@Autowired
	private SceneryRepository sceneryRepository;
	@Autowired
	private TravelPlanDayRepository travelPlanDayRepository;

	
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
	public Optional<TravelItineraryDTO> getTravelItineraryByTravelPlanId(Integer travelPlanId) {
	    // 獲取所有相關的梯次，按創建時間倒序排序
	    List<TravelItinerary> itineraries = travelItineraryRepository.findByTravelPlan_TravelPlanIdOrderByPublishedDateDesc(travelPlanId);

	    // 如果找到任何梯次，則返回最新的一個 (即列表的第一個) 的 DTO
	    if (!itineraries.isEmpty()) {
	        return Optional.of(convertToItineraryDto(itineraries.get(0)));
	    } else {
	        // 如果沒有找到梯次，則返回空的 Optional
	        return Optional.empty();
	    }
	}
	 @Override
	    @Transactional
	    public TravelItinerary saveTravelItineraryFromDto(@Valid TravelItineraryDTO dto) {
		 if (dto.getStartDate().isAfter(dto.getEndDate())) {
		        throw new IllegalArgumentException("結束日期不能早於開始日期。");
		    }

		    TravelPlan travelPlan = travelPlanRepository.findById(dto.getTravelPlanId())
		            .orElseThrow(() -> new IllegalArgumentException("找不到關聯的旅行計畫，ID: " + dto.getTravelPlanId()));

		    TravelItinerary travelItinerary = new TravelItinerary();
		    travelItinerary.setTravelPlan(travelPlan);
		    travelItinerary.setMaxTourist(dto.getMaxTourist());
		    travelItinerary.setTotalPrice(dto.getTotalPrice());
		    travelItinerary.setStartDate(dto.getStartDate());
		    travelItinerary.setEndDate(dto.getEndDate());

		    TravelItinerary savedItinerary = travelItineraryRepository.save(travelItinerary);

		    // 儲存每日行程
		    saveTravelPlanDays(savedItinerary, dto.getDailyItineraries());

		    return savedItinerary;
		}
	@Override
	public List<TravelItinerary> getItinerariesByTravelPlanId(Integer planId) {
		 return travelItineraryRepository.findByTravelPlan_TravelPlanId(planId);
	}
	@Override
	public TravelItinerary updateTravelItineraryFromDto(@Valid TravelItineraryDTO dto) {
		 if (dto.getTravelItineraryId() == null) {
		        throw new IllegalArgumentException("更新操作必須提供行程梯次 ID。");
		    }

		    if (dto.getStartDate().isAfter(dto.getEndDate())) {
		        throw new IllegalArgumentException("結束日期不能早於開始日期。");
		    }

		    TravelItinerary itinerary = travelItineraryRepository.findById(dto.getTravelItineraryId())
		            .orElseThrow(() -> new IllegalArgumentException("找不到要更新的行程梯次，ID: " + dto.getTravelItineraryId()));

		    itinerary.setMaxTourist(dto.getMaxTourist());
		    itinerary.setTotalPrice(dto.getTotalPrice());
		    itinerary.setStartDate(dto.getStartDate());
		    itinerary.setEndDate(dto.getEndDate());

		    TravelItinerary updated = travelItineraryRepository.save(itinerary);

		    // 清除原有的每日行程資料
		    travelPlanDayRepository.deleteByTravelItinerary_TravelItineraryId(updated.getTravelItineraryId());

		    // 重新儲存每日行程
		    saveTravelPlanDays(updated, dto.getDailyItineraries());

		    return updated;
		}
	
		private void saveTravelPlanDays(TravelItinerary itinerary, List<TravelPlanDayDTO> dailyDtos) {
		    if (dailyDtos == null || dailyDtos.isEmpty()) return;

		    for (TravelPlanDayDTO dayDto : dailyDtos) {
		        TravelPlanDay travelPlanDay = new TravelPlanDay();
		        travelPlanDay.setTravelItinerary(itinerary);
		        travelPlanDay.setTraveltime(dayDto.getTraveltime());
		        travelPlanDay.setTravelSequenceNumber(dayDto.getTravelSequenceNumber());
		        travelPlanDay.setTravelDayNumber(dayDto.getTravelDayNumber());

		        if (dayDto.getSceneryId() != null) {
		            SceneryVO scenery = sceneryRepository.findById(dayDto.getSceneryId())
		                    .orElseThrow(() -> new IllegalArgumentException("找不到關聯的景點，ID: " + dayDto.getSceneryId()));
		            travelPlanDay.setScenery(scenery);
		        } else {
		            throw new IllegalArgumentException("每日行程景點ID不能為空。");
		        }

		        travelPlanDayRepository.save(travelPlanDay);
		    }
	
	}
	
	
	//01新增
	@Override
	public Optional<TravelItinerary> findById(Integer id) {
		return travelItineraryRepository.findById(id);
	}
	@Override 
	public TravelItinerary save(TravelItinerary itinerary) {
	    return travelItineraryRepository.save(itinerary); // 正確傳TravelItinerary
	}
	
}
	


