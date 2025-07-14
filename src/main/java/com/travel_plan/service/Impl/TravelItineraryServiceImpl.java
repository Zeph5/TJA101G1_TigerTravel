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

	        TravelItinerary travelItinerary;
	        if (dto.getTravelItineraryId() != null) {
	            // 編輯現有梯次
	            travelItinerary = travelItineraryRepository.findById(dto.getTravelItineraryId())
	                    .orElseThrow(() -> new IllegalArgumentException("找不到要更新的行程梯次，ID: " + dto.getTravelItineraryId()));
	        } else {
	            // 新增梯次
	            travelItinerary = new TravelItinerary();
	            TravelPlan travelPlan = travelPlanRepository.findById(dto.getTravelPlanId())
	                    .orElseThrow(() -> new IllegalArgumentException("找不到關聯的旅行計畫，ID: " + dto.getTravelPlanId()));
	            travelItinerary.setTravelPlan(travelPlan);
	        }

	        // 複製 DTO 屬性到 Entity
	        travelItinerary.setMaxTourist(dto.getMaxTourist());
	        travelItinerary.setTotalPrice(dto.getTotalPrice());
	        travelItinerary.setStartDate(dto.getStartDate());
	        travelItinerary.setEndDate(dto.getEndDate());
	        // publishedDate 和 lastModifiedDate 由 Auditing 自動處理，無需手動複製

	        // 保存 TravelItinerary 實體（如果它是一個新實體，ID 會在這裡生成）
	        TravelItinerary savedItinerary = travelItineraryRepository.save(travelItinerary);

	        // ===========================================
	        // *** 關鍵步驟：儲存每日行程 ***
	        // ===========================================
	        // 1. 如果是更新操作，先刪除舊的每日行程，再重新添加。
	        //    這是一種常見的處理方式，確保數據同步且不重複。
	        //    如果是新建梯次，這裡不會刪除任何東西。
	        if (savedItinerary.getTravelItineraryId() != null) {
	            travelPlanDayRepository.deleteByTravelItinerary_TravelItineraryId(savedItinerary.getTravelItineraryId());
	        }


	        // 2. 遍歷 DTO 中的每日行程列表，並儲存每個 TravelPlanDay
	        //    使用 dto.getDailyItineraries()，而不是 itineraryItems
	        if (dto.getDailyItineraries() != null && !dto.getDailyItineraries().isEmpty()) {
	            for (TravelPlanDayDTO dayDto : dto.getDailyItineraries()) {
	                TravelPlanDay travelPlanDay = new TravelPlanDay();
	                // 設置關聯的 TravelItinerary 實體
	                travelPlanDay.setTravelItinerary(savedItinerary); // 與剛才保存的 TravelItinerary 關聯

	                // 複製 DTO 屬性到 TravelPlanDay 實體
	                travelPlanDay.setTraveltime(dayDto.getTraveltime());
	                travelPlanDay.setTravelSequenceNumber(dayDto.getTravelSequenceNumber());
	                travelPlanDay.setTravelDayNumber(dayDto.getTravelDayNumber()); // 確保複製天數

	                // 關聯 Scenery (景點) 實體：根據 sceneryId 查詢景點
	                if (dayDto.getSceneryId() != null) {
	                    SceneryVO scenery = sceneryRepository.findById(dayDto.getSceneryId())
	                            .orElseThrow(() -> new IllegalArgumentException("找不到關聯的景點，ID: " + dayDto.getSceneryId()));
	                    travelPlanDay.setScenery(scenery);
	                } else {
	                    // 如果 sceneryId 為空，根據業務邏輯看是允許還是拋異常
	                    throw new IllegalArgumentException("每日行程景點ID不能為空。");
	                }

	                travelPlanDayRepository.save(travelPlanDay);
	            }
	        }
	        return savedItinerary; // 返回保存好的梯次
	    }
	@Override
	public List<TravelItinerary> getItinerariesByTravelPlanId(Integer planId) {
		 return travelItineraryRepository.findByTravelPlan_TravelPlanId(planId);
	}
	
}
	


