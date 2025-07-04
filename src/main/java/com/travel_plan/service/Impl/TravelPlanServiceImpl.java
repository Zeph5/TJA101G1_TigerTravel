package com.travel_plan.service.Impl;

import java.time.LocalDateTime; // 導入 LocalDateTime 類
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile; // 保留這個導入

import com.travel_plan.dto.TravelPlanCreationDTO;
import com.travel_plan.model.TravelPlan; // 確保導入 TravelPlan 實體
import com.travel_plan.repository.TravelPlanRepository;
import com.travel_plan.service.TravelPlanService;

import jakarta.transaction.Transactional; // 確保導入 @Transactional

// 這裡不需要 @Valid，因為 Service 層不直接進行驗證，驗證在 Controller 已經處理了。
// 即使您Service的方法參數上寫了@Valid，通常也是Controller在調用Service前就已經完成了驗證。


@Service
public class TravelPlanServiceImpl implements TravelPlanService {

	private final TravelPlanRepository travelPlanRepository;

	@Autowired
	public TravelPlanServiceImpl(TravelPlanRepository travelPlanRepository) {
		this.travelPlanRepository = travelPlanRepository;
	}

	@Override
	public List<TravelPlan> getAllTravelPlans() {
		return travelPlanRepository.findAll();
	}

	@Override
	public Optional<TravelPlan> getTravelPlanById(Integer id) {
		return travelPlanRepository.findById(id);
	}

	// 【修正】只保留一個 createTravelPlanFromDto 方法，並實現其邏輯
	@Override
	@Transactional // 確保創建操作是一個事務
	public TravelPlan createTravelPlanFromDto(TravelPlanCreationDTO dto, MultipartFile bannerImage) {
		// 1. 創建一個新的 TravelPlan 實例
		TravelPlan travelPlan = new TravelPlan();

		// 2. 將 DTO 的數據複製到 TravelPlan 實體中
		travelPlan.setTravelTitle(dto.getTraveltitle());
		travelPlan.setTravelPlanDescription(dto.getTravelplandescription());
		travelPlan.setPublishedDate(LocalDateTime.now()); // 設定發布日期
		travelPlan.setLastModifiedDate(LocalDateTime.now()); // 設定最後修改日期

		// 3. 處理圖片（暫不實際儲存檔案，但可以接收參數）
		if (bannerImage != null && !bannerImage.isEmpty()) {
			// ex: 如果您想將圖片檔案名或臨時路徑儲存到資料庫
            // travelPlan.setTravelPlanBannerUrl("temp_path_or_filename.jpg");
            // 但因為您說暫不處理圖片，這裡可以先不做任何檔案操作，
            // 讓 travelPlan.getTravelPlanBannerUrl() 保持 null 或空
            System.out.println("收到圖片文件，但暫時未實作儲存邏輯：" + bannerImage.getOriginalFilename());
		} else {
            travelPlan.setTravelPlanBannerUrl(null); // 如果沒有上傳圖片，設置為 null
        }


		// 4. 保存 TravelPlan 實體到資料庫並返回
		return travelPlanRepository.save(travelPlan);
	}

	// 【修正】實現 updateTravelPlan 方法
	@Override
	@Transactional // 確保更新操作是一個事務
	public TravelPlan updateTravelPlan(Integer travelPlanId, TravelPlanCreationDTO dto,
			MultipartFile bannerImage) {

		// 1. 根據 ID 查找現有的 TravelPlan 實體
		return travelPlanRepository.findById(travelPlanId).map(existingPlan -> {
			// 2. 更新現有實體的屬性
			existingPlan.setTravelTitle(dto.getTraveltitle());
			existingPlan.setTravelPlanDescription(dto.getTravelplandescription());
			existingPlan.setLastModifiedDate(LocalDateTime.now()); // 更新最後修改日期

            // 3. 處理圖片更新（暫不實際儲存檔案）
            if (bannerImage != null && !bannerImage.isEmpty()) {
                // TODO: 在此處添加圖片檔案更新的邏輯 (例如刪除舊圖片，保存新圖片)
                // 並將圖片的 URL/路徑更新到 existingPlan.setTravelPlanBannerUrl()
                System.out.println("收到圖片文件更新請求，但暫未實作儲存邏輯：" + bannerImage.getOriginalFilename());
            } else {
                // 如果沒有新圖片上傳，通常保持現有圖片 URL 不變
                // 除非您在 DTO 中有明確標誌表示要移除舊圖片
            }

			// 4. 保存更新後的實體
			return travelPlanRepository.save(existingPlan);
		}).orElseThrow(() -> new IllegalArgumentException("找不到 ID 為 " + travelPlanId + " 的旅行計畫，無法更新。"));
	}

	// 【修正】實現 convertToDto 方法
	@Override
	public TravelPlanCreationDTO convertToDto(TravelPlan entity) {
		TravelPlanCreationDTO dto = new TravelPlanCreationDTO();
		dto.setTravelPlanId(entity.getTravelPlanId());
		dto.setTraveltitle(entity.getTravelTitle());
		dto.setTravelplandescription(entity.getTravelPlanDescription());
		// 如果您的 TravelPlanCreationDTO 有 existingBannerImageUrl 欄位，可以在這裡設定
		// 例如：dto.setExistingBannerImageUrl(entity.getTravelPlanBannerUrl());
		return dto;
	}

	// 【刪除這個重複且錯誤的方法，因為上面已經有一個帶 MultipartFile 的版本了】
	// @Override
	// public TravelPlan createTravelPlanFromDto(@Valid TravelPlanCreationDTO dto) {
	//     // return travelPlanRepository.save(travelPlan); // travelPlan 未定義
	//     return null; // 或者直接刪除此方法
	// }
}