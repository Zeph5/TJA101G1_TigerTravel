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
	public Optional<TravelItineraryDTO> getTravelItineraryByTravelPlanId(Integer travelPlanId) {
	    return travelItineraryRepository.findByTravelPlan_TravelPlanId(travelPlanId)
	            .map(this::convertToItineraryDto);
	}
	@Override
	@Transactional
	public TravelItinerary saveTravelItineraryFromDto(@Valid TravelItineraryDTO dto) {
	    if (dto.getStartDate().isAfter(dto.getEndDate())) {
	        throw new IllegalArgumentException("結束日期不能早於開始日期。");
	    }

	    TravelItinerary travelItinerary;
	    if (dto.getTravelItineraryId() != null) {
	        // 編輯現有梯次：從資料庫查詢，如果找不到則拋出異常
	        travelItinerary = travelItineraryRepository.findById(dto.getTravelItineraryId())
	                .orElseThrow(() -> new IllegalArgumentException("找不到要更新的行程梯次，ID: " + dto.getTravelItineraryId()));
	    } else {
	        // 新增梯次：創建一個新的 TravelItinerary 實體
	        travelItinerary = new TravelItinerary();
	        // 關聯 TravelPlan 實體：必須確保 dto 中有 travelPlanId
	        TravelPlan travelPlan = travelPlanRepository.findById(dto.getTravelPlanId()) // 假設 TravelItineraryDTO 包含 travelPlanId
	                .orElseThrow(() -> new IllegalArgumentException("找不到關聯的旅行計畫，ID: " + dto.getTravelPlanId()));
	        travelItinerary.setTravelPlan(travelPlan); // 設定關聯
	    }

	    // 複製 DTO 屬性到 Entity
	    // 注意：BeanUtils.copyProperties 會複製所有同名屬性，包括可能的 ID，所以需要小心使用。
	    // 在這裡，由於我們已經通過 findById 或 new 來初始化 entity，只需更新非 ID 屬性。
	    // 可以手動複製或確保 BeanUtils 不會覆蓋重要關聯。
	    travelItinerary.setMaxTourist(dto.getMaxTourist());
	    travelItinerary.setTotalPrice(dto.getTotalPrice());
	    travelItinerary.setStartDate(dto.getStartDate());
	    travelItinerary.setEndDate(dto.getEndDate());
	    // 如果有其他梯次屬性，請在此處設定

	    // 保存 TravelItinerary 實體
	    return travelItineraryRepository.save(travelItinerary);
	}
}
	


