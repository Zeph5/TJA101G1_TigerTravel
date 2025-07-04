package com.travel_plan.service.Impl;

import org.springframework.stereotype.Service;

import com.travel_plan.dto.TravelPlanDayDTO;
import com.travel_plan.model.TravelItinerary;
import com.travel_plan.service.TravelPlanDayService;

import jakarta.validation.Valid;

@Service
public class TravelPlanDayServiceImpl implements TravelPlanDayService {

	// 這裡可以注入其他服務或存儲庫，例如 TravelItineraryService
	// private final TravelItineraryService travelItineraryService;

	// @Autowired
	// public TravelPlanDayServiceImpl(TravelItineraryService travelItineraryService) {
	//     this.travelItineraryService = travelItineraryService;
	// }

	@Override
	public void createTravelPlanDayFromDto(TravelItinerary travelItinerary, @Valid TravelPlanDayDTO dto) {
		// 這裡實現 DTO 到 TravelPlanDay 的轉換邏輯
		// 例如：將 TravelPlanDayDTO 轉換為 TravelPlanDay 實體並儲存到資料庫
		// 返回儲存後的 TravelPlanDay 實體
	}
	
}
