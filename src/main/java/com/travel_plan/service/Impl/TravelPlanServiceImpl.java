package com.travel_plan.service.Impl;


import com.travel_plan.dto.DailyItineraryFormDTO;
import com.travel_plan.dto.TravelItineraryDTO;
import com.travel_plan.dto.TravelPlanCreationDTO;
import com.travel_plan.dto.TravelPlanDayDTO;
import com.travel_plan.dto.TravelPlanPreviewDTO;
import com.travel_plan.model.TravelItinerary;
import com.travel_plan.model.TravelPlan;
import com.travel_plan.model.TravelPlanDay; // 新增，用於實作

import com.travel_plan.repository.TravelItineraryRepository; // 新增
import com.travel_plan.repository.TravelPlanDayRepository; // 新增
import com.travel_plan.repository.TravelPlanRepository; // 新增
import com.travel_plan.service.TravelPlanService;

import jakarta.validation.Valid;

import com.scenery.model.SceneryRepository; // 新增，假設有 SceneryRepository

import org.springframework.beans.BeanUtils; // 用於 DTO 與 Entity 轉換
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 用於事務管理
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator; // 用於排序
import java.util.List;
import java.util.Map; // 用於 Map
import java.util.Objects; // 用於 Objects.equals
import java.util.Optional;
import java.util.Set; // 用於 Set
import java.util.TreeMap;
import java.util.stream.Collectors; // 用於 Stream API

@Service
public class TravelPlanServiceImpl implements TravelPlanService {

    private final TravelPlanRepository travelPlanRepository;
    private final TravelItineraryRepository travelItineraryRepository;
    private final TravelPlanDayRepository travelPlanDayRepository;
    private final SceneryRepository sceneryRepository; // 假設你處理景點的 Repository

    // 配置圖片上傳路徑
    // TODO: 在實際專案中，這個路徑應該從配置文件中讀取，例如 application.properties 或 application.yml
    private final String UPLOAD_DIR = "src/main/resources/static/images/travelplan_banners/";

    @Autowired
    public TravelPlanServiceImpl(TravelPlanRepository travelPlanRepository,
                                 TravelItineraryRepository travelItineraryRepository,
                                 TravelPlanDayRepository travelPlanDayRepository,
                                 SceneryRepository sceneryRepository) {
        this.travelPlanRepository = travelPlanRepository;
        this.travelItineraryRepository = travelItineraryRepository;
        this.travelPlanDayRepository = travelPlanDayRepository;
        this.sceneryRepository = sceneryRepository;

        // 確保上傳目錄存在
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            // 在實際應用中應該使用日誌框架，例如 slf4j
            System.err.println("Error creating upload directory: " + e.getMessage());
            // 拋出 RuntimeException 或其他自定義異常，阻止應用程式啟動
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    // --- 輔助方法：處理圖片上傳 ---
    private String saveBannerImage(MultipartFile bannerImage) {
        if (bannerImage == null || bannerImage.isEmpty()) {
            return null; // 沒有圖片上傳
        }

        try {
            // 檢查檔案類型 (簡單檢查，更嚴格的檢查應檢查 Mime Type)
            String contentType = bannerImage.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Invalid file type. Only image files are allowed.");
            }

            // 檢查檔案大小 (例如：最大 5MB)
            long maxFileSize = 5 * 1024 * 1024; // 5 MB
            if (bannerImage.getSize() > maxFileSize) {
                throw new IllegalArgumentException("File size exceeds the maximum limit of 5MB.");
            }

            // 生成唯一檔名以避免衝突
            String fileName = System.currentTimeMillis() + "_" + bannerImage.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR, fileName);
            Files.copy(bannerImage.getInputStream(), filePath);

            // 返回相對路徑，供前端顯示 (例如 /images/travelplan_banners/your_image.jpg)
            return "/images/travelplan_banners/" + fileName;
        } catch (IOException e) {
            // 這裡應該記錄錯誤日誌
            throw new RuntimeException("Failed to save banner image: " + e.getMessage(), e);
        }
    }

    // --- 輔助方法：DTO 與 Entity 轉換 ---
    @Override
    public TravelPlanCreationDTO convertToCreationDto(TravelPlan entity) {
        TravelPlanCreationDTO dto = new TravelPlanCreationDTO();
        BeanUtils.copyProperties(entity, dto); // 複製同名屬性

        
       
        return dto;
    }

    private TravelPlan convertToEntity(TravelPlanCreationDTO dto) {
        TravelPlan entity = new TravelPlan();
        BeanUtils.copyProperties(dto, entity);
        // bannerImageUrl 需要在保存圖片後單獨設定
        return entity;
    }

    private TravelPlanDayDTO convertToTravelPlanDayDTO(TravelPlanDay entity) {
        TravelPlanDayDTO dto = new TravelPlanDayDTO();
        BeanUtils.copyProperties(entity, dto);
        // 如果需要景點名稱，從 Scenery Entity 中獲取
        if (entity.getScenery() != null) {
            dto.setSceneryName(entity.getScenery().getSceneryName());
        }
        return dto;
    }

    private TravelPlanDay convertToTravelPlanDayEntity(TravelPlanDayDTO dto) {
        TravelPlanDay entity = new TravelPlanDay();
        BeanUtils.copyProperties(dto, entity);
        // 設置 Scenery Entity (需要從資料庫查詢)
        if (dto.getSceneryId() != null) {
            sceneryRepository.findById(dto.getSceneryId()).ifPresent(entity::setScenery);
        }
        return entity;
    }

    // --- 介面方法實作 ---

    @Override
    public List<TravelPlan> getAllTravelPlans() {
        return travelPlanRepository.findAll();
    }

    @Override
    public Optional<TravelPlanCreationDTO> getTravelPlanById(Integer id) {
        return travelPlanRepository.findById(id)
                .map(this::convertToCreationDto);
    }

    @Override
    public Optional<TravelPlan> getTravelPlanEntityById(Integer planId) {
        return travelPlanRepository.findById(planId);
    }

    @Override
    @Transactional // 確保事務完整性
    public TravelPlan createTravelPlanFromDto(@Valid TravelPlanCreationDTO dto, MultipartFile bannerImage) {
        TravelPlan travelPlan = convertToEntity(dto);
        String imageUrl = saveBannerImage(bannerImage);
        
        return travelPlanRepository.save(travelPlan);
    }

    @Override
    @Transactional
    public TravelPlan updateTravelPlan(Integer travelPlanId, @Valid TravelPlanCreationDTO dto, MultipartFile bannerImage) {
        return travelPlanRepository.findById(travelPlanId).map(existingPlan -> {
            // 更新基本資訊
            existingPlan.setTravelTitle(dto.getTravelTitle());
            existingPlan.setTravelPlanDescription(dto.getTravelPlanDescription());
           

            // 處理圖片更新 (如果傳入新圖片)
            if (bannerImage != null && !bannerImage.isEmpty()) {
                String newImageUrl = saveBannerImage(bannerImage);
                // 可以考慮刪除舊圖片檔案
                existingPlan.setTravelPlanBannerUrl(newImageUrl);
            }
       
            return travelPlanRepository.save(existingPlan);
        }).orElseThrow(() -> new IllegalArgumentException("找不到要更新的旅行計畫，ID: " + travelPlanId));
    }


    // 根據開始和結束日期生成日期列表
    @Override
    public List<LocalDate> generateDatesBetween(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> dates = new ArrayList<>();
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            return dates;
        }
        long numOfDaysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        for (int i = 0; i < numOfDaysBetween; i++) {
            dates.add(startDate.plusDays(i));
        }
        return dates;
    }

    // 獲取或創建 TravelItinerary
    @Override
    @Transactional
    public TravelItinerary getOrCreateTravelItineraryForPlan(Integer planId) {
        return travelItineraryRepository.findByTravelPlan_TravelPlanId(planId)
                .orElseGet(() -> {
                    // 如果不存在，則創建一個新的
                    TravelPlan travelPlan = travelPlanRepository.findById(planId)
                            .orElseThrow(() -> new IllegalArgumentException("Travel Plan not found for ID: " + planId));
                    TravelItinerary newItinerary = new TravelItinerary();
                    newItinerary.setTravelPlan(travelPlan); // 關聯 TravelPlan
                    // 設置其他預設值，例如狀態
                    // newItinerary.setStatus("DRAFT");
                    return travelItineraryRepository.save(newItinerary);
                });
    }

    // 獲取 DailyItineraryFormDTO (載入特定日期的行程數據)
    @Override
    public DailyItineraryFormDTO getDailyItineraryFormDTO(Integer travelItineraryId, LocalDate date, Integer travelDayNumber) {
        List<TravelPlanDay> dailyEntities = travelPlanDayRepository.findByTravelItinerary_TravelItineraryIdAndTravelTime(travelItineraryId, date);

        List<TravelPlanDayDTO> dailyItemDTOs = dailyEntities.stream()
                .map(this::convertToTravelPlanDayDTO)
                // 確保排序，以便前端拖曳後再次載入時順序不會亂掉
                .sorted(Comparator.comparing(TravelPlanDayDTO::getTravelSequenceNumber))
                .collect(Collectors.toList());

        DailyItineraryFormDTO dto = new DailyItineraryFormDTO();
        dto.setDailyItems(dailyItemDTOs);
        dto.setTravelDayNumber(travelDayNumber); // 從控制器傳入或這裡計算

        return dto;
    }


 

    

    @Override
    public TravelPlanPreviewDTO getFullTravelPlanDetails(Integer planId) { // <-- 這裡必須是 TravelPlanPreviewDTO
        TravelPlan travelPlan = travelPlanRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Travel Plan not found with ID: " + planId));

        TravelPlanPreviewDTO dto = new TravelPlanPreviewDTO();
        // 複製 TravelPlan 基本資訊
        dto.setTravelPlanId(travelPlan.getTravelPlanId());
        dto.setTravelTitle(travelPlan.getTravelTitle());
        dto.setTravelPlanDescription(travelPlan.getTravelPlanDescription());
        dto.setTravelPlanBannerUrl(travelPlan.getTravelPlanBannerUrl());

        // 載入相關的 TravelItinerary
        Optional<TravelItinerary> itineraryOptional = travelItineraryRepository.findByTravelPlan_TravelPlanId(planId);

        itineraryOptional.ifPresent(itinerary -> {
            // 複製 TravelItinerary 資訊
            dto.setTravelItineraryId(itinerary.getTravelItineraryId());
            dto.setMaxTourist(itinerary.getMaxTourist());
            dto.setTotalPrice(itinerary.getTotalPrice());
            dto.setStartDate(itinerary.getStartDate());
            dto.setEndDate(itinerary.getEndDate());

            // 載入並整理所有 TravelPlanDay 實體
            List<TravelPlanDay> allDays = travelPlanDayRepository.findByTravelItinerary_TravelItineraryId(itinerary.getTravelItineraryId());

            // 按日期分組並排序
            Map<LocalDate, List<TravelPlanDayDTO>> groupedDailyItems = allDays.stream()
                    .map(this::convertToTravelPlanDayDTO)
                    .collect(Collectors.groupingBy(TravelPlanDayDTO::getTraveltime,
                            TreeMap::new,
                            Collectors.collectingAndThen(
                                    Collectors.toList(),
                                    list -> list.stream()
                                            .sorted(Comparator.comparing(TravelPlanDayDTO::getTravelSequenceNumber))
                                            .collect(Collectors.toList())
                            )
                    ));
            dto.setDailyItineraries(groupedDailyItems);
        });
        return dto;
    }

    @Override
	@Transactional // 確保整個儲存操作在一個事務中，保證資料一致性
	public void saveDailyItems(Integer itineraryId, LocalDate date, List<TravelPlanDayDTO> dailyItems) {
	    // 1. 獲取資料庫中該日期現有的行程項目
	    // 我們需要先查出目前資料庫裡，這個行程 (itineraryId) 在這一天 (date) 有哪些 DailyItem
	    List<TravelPlanDay> existingEntities = travelPlanDayRepository.findByTravelItinerary_TravelItineraryIdAndTravelTime(itineraryId, date);

	    // 將現有項目轉換為 Map，以 travelPlanDayId 為鍵，方便後續查找
	    Map<Integer, TravelPlanDay> existingMap = existingEntities.stream()
	            .collect(Collectors.toMap(TravelPlanDay::getTravelPlanDayId, item -> item));

	    // 用於追蹤哪些現有項目需要被刪除 (一開始假設所有現有項目都要刪，後面會移除那些被保留或更新的)
	    Set<Integer> toDeleteIds = existingMap.keySet().stream().collect(Collectors.toSet());

	    // 獲取 TravelItinerary 實體，因為 TravelPlanDay 需要關聯它
	    // 如果找不到對應的行程，就拋出異常
	    TravelItinerary travelItinerary = travelItineraryRepository.findById(itineraryId)
	            .orElseThrow(() -> new IllegalArgumentException("Travel Itinerary not found with ID: " + itineraryId));

	    List<TravelPlanDay> itemsToSave = new ArrayList<>(); // 用於收集需要保存（新增或更新）的實體

	    // 2. 處理傳入的 dailyItems (前端提交的數據)
	    // 遍歷前端傳來的新列表，判斷是新增還是更新
	    for (int i = 0; i < dailyItems.size(); i++) {
	        TravelPlanDayDTO dto = dailyItems.get(i);
	        dto.setTraveltime(date);
	        // 修正這裡的呼叫，使用正確的 calculateTravelDayNumber
	        dto.setTravelDayNumber(calculateTravelDayNumber(travelItinerary.getTravelItineraryId(), date));
	        dto.setTravelSequenceNumber(i + 1); // 根據列表的順序設定行程順序 (從 1 開始)
	        
	        TravelPlanDay entity;
	        if (dto.getTravelPlanDayId() != null && existingMap.containsKey(dto.getTravelPlanDayId())) {
	            // 這個項目已經存在於資料庫中，執行更新
	            entity = existingMap.get(dto.getTravelPlanDayId());
	            // 從待刪除列表中移除，表示這個項目會被更新而不是刪除
	            toDeleteIds.remove(dto.getTravelPlanDayId());

	            // 更新實體的屬性
	            entity.setTravelSequenceNumber(dto.getTravelSequenceNumber());
	            entity.setTravelTime(dto.getTraveltime());
	            entity.setTravelDayNumber(dto.getTravelDayNumber());

	            // 處理景點關聯的更新：只有當景點 ID 改變時才重新設定 Scenery 實體
	            Integer currentSceneryId = (entity.getScenery() != null) ? entity.getScenery().getSceneryId() : null;
	            if (!Objects.equals(currentSceneryId, dto.getSceneryId())) {
	                if (dto.getSceneryId() != null) {
	                    // 查找新的 Scenery 實體並設定
	                    sceneryRepository.findById(dto.getSceneryId()).ifPresent(entity::setScenery);
	                } else {
	                    entity.setScenery(null); // 如果景點 ID 為空，則清除關聯
	                }
	            }
	        } else {
	            // 這個項目是新的，執行新增
	            entity = convertToTravelPlanDayEntity(dto); // 透過輔助方法轉換 DTO 到 Entity
	            entity.setTravelItinerary(travelItinerary); // 設定關聯的 TravelItinerary
	        }
	        itemsToSave.add(entity); // 將處理後的實體加入到保存列表
	    }

	    // 3. 執行儲存操作 (新增和更新)
	    if (!itemsToSave.isEmpty()) {
	        travelPlanDayRepository.saveAll(itemsToSave); // 批量保存實體
	    }

	    // 4. 執行刪除操作
	    // toDeleteIds 中剩下的 ID，表示這些項目在資料庫中存在，但沒有在前端提交的列表中，需要刪除。
	    if (!toDeleteIds.isEmpty()) {
	        travelPlanDayRepository.deleteAllById(toDeleteIds); // 批量刪除實體
	    }
	}
    @Override
	public List<TravelPlanDayDTO> getDailyItemsForDate(Integer itineraryId, LocalDate date) {
	    // 從資料庫獲取該日期的所有 TravelPlanDay 實體
	    // 假設你的 TravelPlanDayRepository 有這個查詢方法
	    List<TravelPlanDay> dailyEntities = travelPlanDayRepository.findByTravelItinerary_TravelItineraryIdAndTravelTime(itineraryId, date);

	    // 將實體轉換為 DTO，並按照行程順序排序
	    List<TravelPlanDayDTO> dailyItemDTOs = dailyEntities.stream()
	            .map(this::convertToTravelPlanDayDTO) // 使用輔助方法轉換為 DTO
	            .sorted(Comparator.comparing(TravelPlanDayDTO::getTravelSequenceNumber)) // 確保按照順序號碼排序
	            .collect(Collectors.toList());

	    return dailyItemDTOs;
	}

  

	@Override
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
            // 2. 查找並關聯 TravelPlan 實體
            TravelPlan travelPlan = travelPlanRepository.findById(dto.getTravelPlanId())
                    .orElseThrow(() -> new IllegalArgumentException("找不到關聯的旅行計畫，ID: " + dto.getTravelPlanId()));
            travelItinerary.setTravelPlan(travelPlan); // 設定關聯
        }

        // 3. 複製 DTO 屬性到 Entity
        // 注意：BeanUtils.copyProperties 可能會覆蓋關聯的 TravelPlan，所以要小心使用或手動複製
        // 這裡我手動設定了，避免覆蓋 TravelPlan 關聯
        travelItinerary.setMaxTourist(dto.getMaxTourist());
        travelItinerary.setTotalPrice(dto.getTotalPrice());
        travelItinerary.setStartDate(dto.getStartDate());
        travelItinerary.setEndDate(dto.getEndDate());
        // 可以設定預設狀態或其他梯次相關屬性
        // travelItinerary.setStatus("ACTIVE"); // 例如

        // 4. 保存 TravelItinerary 實體
        return travelItineraryRepository.save(travelItinerary);
    }

	@Override
	public Integer calculateTravelDayNumber(Integer itineraryId, LocalDate currentDate) {
	    // 從資料庫獲取 TravelItinerary 實體來取得開始日期
	    TravelItinerary itinerary = travelItineraryRepository.findById(itineraryId)
	            .orElseThrow(() -> new IllegalArgumentException("找不到行程梯次，ID: " + itineraryId));

	    // 確保梯次有開始日期
	    if (itinerary.getStartDate() == null) {
	        throw new IllegalStateException("行程梯次 (ID: " + itineraryId + ") 沒有設定開始日期。");
	    }

	    // 驗證當前日期是否在行程日期範圍內
	    if (currentDate.isBefore(itinerary.getStartDate())) {
	        throw new IllegalArgumentException("當前日期 (" + currentDate + ") 早於行程開始日期 (" + itinerary.getStartDate() + ")。");
	    }

	    // 計算天數差，並加 1 得到當天是第幾天
	    long daysBetween = ChronoUnit.DAYS.between(itinerary.getStartDate(), currentDate);
	    return (int) daysBetween + 1;
	}

	@Override
	public Optional<TravelItinerary> getTravelItineraryById(Integer travelItineraryId) {
	    return travelItineraryRepository.findById(travelItineraryId);
	}
	@Override
	public Optional<TravelItinerary> getTravelItineraryForPlan(Integer travelPlanId) {
	    // 假設一個 TravelPlan 通常只有一個 TravelItinerary
	    // 如果有多個，您可能需要定義策略（例如：獲取最新的、或指定某一個）
	    return travelItineraryRepository.findByTravelPlan_TravelPlanId(travelPlanId);
	}
}
    
