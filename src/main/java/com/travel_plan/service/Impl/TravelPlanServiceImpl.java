package com.travel_plan.service.Impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.travel_plan.dto.TravelPlanCreationDTO;
import com.travel_plan.model.TravelPlan;
import com.travel_plan.repository.TravelPlanRepository;
import com.travel_plan.service.TravelPlanService;


@Service
public class TravelPlanServiceImpl implements TravelPlanService {
	
	private final TravelPlanRepository travelPlanRepository;
	
	@Autowired
	public TravelPlanServiceImpl(TravelPlanRepository travelPlanRepository) {
		this.travelPlanRepository = travelPlanRepository;
	}
	
	@Override
	public TravelPlan createTravelPlanFromDto(TravelPlanCreationDTO dto) {
		// 這裡實現 DTO 到 Entity 的轉換邏輯
		// 例如：將 TravelPlanCreatDTO 轉換為 TravelPlan 實體並儲存到資料庫
		// 返回儲存後的 TravelPlan 實體
		return null; // 替換為實際的實現
	}

	@Override
	public List<TravelPlan> getAllTravelPlans() {
		// TODO Auto-generated method stub
		return travelPlanRepository.findAll(); // 假設有一個 travelPlanRepository 用於存取資料庫
	}

	@Override
	public Optional<TravelPlan> getTravelPlanById(Integer id) {
		
		return travelPlanRepository.findById(id);
	}

	

}
