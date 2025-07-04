package com.travel_plan.service.Impl;

import java.time.LocalDateTime; // 導入 LocalDateTime 類
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile; // 保留這個導入

import com.travel_plan.dto.TravelPlanCreationDTO;
import com.travel_plan.model.TravelPlan; // 確保導入 TravelPlan 實體
import com.travel_plan.repository.TravelPlanRepository;
import com.travel_plan.service.TravelPlanService;

import jakarta.transaction.Transactional; // 確保導入 @Transactional
import jakarta.validation.Valid;

// 這裡不需要 @Valid，因為 Service 層不直接進行驗證，驗證在 Controller 已經處理了。
// 即使您Service的方法參數上寫了@Valid，通常也是Controller在調用Service前就已經完成了驗證。


@Service
public class TravelPlanServiceImpl implements TravelPlanService {

	private final TravelPlanRepository travelPlanRepository;

	@Autowired
	public TravelPlanServiceImpl(TravelPlanRepository travelPlanRepository) {
		this.travelPlanRepository = travelPlanRepository;
	}

	 // 將 TravelPlan Entity 轉換為 TravelPlanCreationDTO
		@Override
		public TravelPlanCreationDTO convertToCreationDto(TravelPlan entity) {
			if (entity == null) {
				return null;
			}
			TravelPlanCreationDTO dto = new TravelPlanCreationDTO();
			BeanUtils.copyProperties(entity, dto);
			return dto;
		}

//		資料庫中根據 id 找出一筆 TravelPlan，然後把它轉換成 TravelPlanCreationDTO，並用 Optional 包起來返回。
		@Override
		public Optional<TravelPlanCreationDTO> getTravelPlanById(Integer id) {
			return travelPlanRepository.findById(id)
					.map(this::convertToCreationDto);
		}

		@Override
		public List<TravelPlan> getAllTravelPlans() {
			return travelPlanRepository.findAll();
		}

		@Override
		@Transactional
		public TravelPlan updateTravelPlan(Integer travelPlanId,  TravelPlanCreationDTO dto,
				MultipartFile bannerImage) {
			// 首先查找現有的 TravelPlan
			TravelPlan existingPlan = travelPlanRepository.findById(travelPlanId)
					.orElseThrow(() -> new IllegalArgumentException("Travel Plan not found with id: " + travelPlanId));
			
			existingPlan.setTravelTitle(dto.getTravelTitle());
			existingPlan.setTravelPlanDescription(dto.getTravelPlanDescription());
			
			
			
			
//			TODO: 圖片處理邏輯
//			if (bannerImage != null && !bannerImage.isEmpty()) {
//		        // 儲存圖片邏輯
//		        String imageUrl = saveImage(bannerImage); // 你自己定義的圖片儲存邏輯
//		        existingPlan.setTravelPlanBannerUrl(imageUrl);
//		    }
			return travelPlanRepository.save(existingPlan);			
			
			
		}

		@Override
		@Transactional
		public TravelPlan createTravelPlanFromDto(@Valid TravelPlanCreationDTO dto, MultipartFile bannerImage) {
			if (dto == null) {
				return null;
			}
			TravelPlan entity = new TravelPlan();
			BeanUtils.copyProperties(dto, entity);
			if (entity.getStartDate() == null) {
		        throw new IllegalArgumentException("旅行計畫開始日期為空，無法儲存。");
		    }
		    if (entity.getEndDate() == null) {
		        throw new IllegalArgumentException("旅行計畫結束日期為空，無法儲存。");
		    }
		    if (entity.getStartDate().isAfter(entity.getEndDate())) {
		        throw new IllegalArgumentException("旅行計畫結束日期不能早於開始日期。");
		    }
			TravelPlan savedEntity = travelPlanRepository.save(entity);
			return savedEntity;
		}

		@Override
		public Optional<TravelPlan> getTravelPlanEntityById(Integer planId) {
			return travelPlanRepository.findById(planId);
					
		}

	
}