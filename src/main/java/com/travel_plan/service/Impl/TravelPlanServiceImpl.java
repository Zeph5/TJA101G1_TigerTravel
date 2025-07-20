package com.travel_plan.service.Impl;

import com.scenery.model.SceneryRepository;
import com.travel_plan.dto.*;
import com.travel_plan.model.*;
import com.travel_plan.repository.*;
import com.travel_plan.service.TravelPlanService;

import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TravelPlanServiceImpl implements TravelPlanService {

    private final TravelPlanRepository travelPlanRepository;
    private final TravelItineraryRepository travelItineraryRepository;
    private final TravelPlanDayRepository travelPlanDayRepository;
    private final SceneryRepository sceneryRepository;

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

        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    // 圖片儲存輔助
    private String saveBannerImage(MultipartFile bannerImage) {
        if (bannerImage == null || bannerImage.isEmpty()) {
            return null;
        }
        try {
            String contentType = bannerImage.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Invalid file type. Only images allowed.");
            }
            long maxFileSize = 5 * 1024 * 1024;
            if (bannerImage.getSize() > maxFileSize) {
                throw new IllegalArgumentException("File too large (max 5MB).");
            }
            String fileName = System.currentTimeMillis() + "_" + bannerImage.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR, fileName);
            Files.copy(bannerImage.getInputStream(), filePath);
            return "/images/travelplan_banners/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save banner image: " + e.getMessage(), e);
        }
    }

    // Entity <-> DTO 轉換
    @Override
    public TravelPlanCreationDTO convertToCreationDto(TravelPlan entity) {
        TravelPlanCreationDTO dto = new TravelPlanCreationDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private TravelPlan convertToEntity(TravelPlanCreationDTO dto) {
        TravelPlan entity = new TravelPlan();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }

    private TravelPlanDayDTO convertToTravelPlanDayDTO(TravelPlanDay entity) {
        TravelPlanDayDTO dto = new TravelPlanDayDTO();
        BeanUtils.copyProperties(entity, dto);
        if (entity.getScenery() != null) {
            dto.setSceneryName(entity.getScenery().getSceneryName());
            dto.setSceneryId(entity.getScenery().getSceneryId());
        }
        return dto;
    }

    
    // 主要邏輯實作

    @Override
    public List<TravelPlan> getAllTravelPlans() {
        return travelPlanRepository.findAll();
    }

    @Override
    public Optional<TravelPlanCreationDTO> getTravelPlanById(Integer id) {
        return travelPlanRepository.findById(id).map(this::convertToCreationDto);
    }

    @Override
    public Optional<TravelPlan> getTravelPlanEntityById(Integer planId) {
        return travelPlanRepository.findById(planId);
    }

    @Override
    @Transactional
    public TravelPlan createTravelPlanFromDto(@Valid TravelPlanCreationDTO dto, MultipartFile bannerImage) {
        TravelPlan travelPlan = convertToEntity(dto);
        String imageUrl = saveBannerImage(bannerImage);
        if (imageUrl != null) {
            travelPlan.setTravelPlanBannerUrl(imageUrl);
        }
        return travelPlanRepository.save(travelPlan);
    }

    @Override
    @Transactional
    public TravelPlan updateTravelPlan(Integer travelPlanId, @Valid TravelPlanCreationDTO dto, MultipartFile bannerImage) {
        return travelPlanRepository.findById(travelPlanId).map(existingPlan -> {
            existingPlan.setTravelTitle(dto.getTravelTitle());
            existingPlan.setTravelPlanDescription(dto.getTravelPlanDescription());

            if (bannerImage != null && !bannerImage.isEmpty()) {
                String newImageUrl = saveBannerImage(bannerImage);
                existingPlan.setTravelPlanBannerUrl(newImageUrl);
            }
            return travelPlanRepository.save(existingPlan);
        }).orElseThrow(() -> new IllegalArgumentException("找不到要更新的旅行計畫，ID: " + travelPlanId));
    }

    @Override
    public List<LocalDate> generateDatesBetween(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> dates = new ArrayList<>();
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            return dates;
        }
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        for (int i = 0; i < days; i++) {
            dates.add(startDate.plusDays(i));
        }
        return dates;
    }

    @Override
    @Transactional
    public TravelItinerary getOrCreateTravelItineraryForPlan(Integer planId) {
        List<TravelItinerary> existing = travelItineraryRepository.findByTravelPlan_TravelPlanIdOrderByPublishedDateDesc(planId);
        if (!existing.isEmpty()) return existing.get(0);

        TravelPlan travelPlan = travelPlanRepository.findById(planId)
            .orElseThrow(() -> new IllegalArgumentException("Travel Plan not found for ID: " + planId));
        TravelItinerary newItinerary = new TravelItinerary();
        newItinerary.setTravelPlan(travelPlan);
        return travelItineraryRepository.save(newItinerary);
    }

    @Override
    public DailyItineraryFormDTO getDailyItineraryFormDTO(Integer travelItineraryId, LocalDate date, Integer travelDayNumber) {
        List<TravelPlanDay> dailyEntities = travelPlanDayRepository.findByTravelItinerary_TravelItineraryIdAndTraveltime(travelItineraryId, date);

        List<TravelPlanDayDTO> dailyItemDTOs = dailyEntities.stream()
                .map(this::convertToTravelPlanDayDTO)
                .sorted(Comparator.comparing(TravelPlanDayDTO::getTravelSequenceNumber))
                .collect(Collectors.toList());

        DailyItineraryFormDTO dto = new DailyItineraryFormDTO();
        dto.setDailyItems(dailyItemDTOs);
        dto.setTravelDayNumber(travelDayNumber);
        return dto;
    }

    @Override
    public TravelPlanPreviewDTO getFullTravelPlanDetails(Integer planId) {
        TravelPlan travelPlan = travelPlanRepository.findById(planId)
            .orElseThrow(() -> new IllegalArgumentException("Travel Plan not found with ID: " + planId));

        TravelPlanPreviewDTO dto = new TravelPlanPreviewDTO();
        dto.setTravelPlanId(travelPlan.getTravelPlanId());
        dto.setTravelTitle(travelPlan.getTravelTitle());
        dto.setTravelPlanDescription(travelPlan.getTravelPlanDescription());
        dto.setTravelPlanBannerUrl(travelPlan.getTravelPlanBannerUrl());

        List<TravelItinerary> itineraries = travelItineraryRepository.findByTravelPlan_TravelPlanId(planId);
        if (!itineraries.isEmpty()) {
            TravelItinerary itinerary = itineraries.get(0);
            dto.setTravelItineraryId(itinerary.getTravelItineraryId());
            dto.setMaxTourist(itinerary.getMaxTourist());
            dto.setTotalPrice(itinerary.getTotalPrice());
            dto.setStartDate(itinerary.getStartDate());
            dto.setEndDate(itinerary.getEndDate());

            List<TravelPlanDay> allDays = travelPlanDayRepository.findByTravelItinerary_TravelItineraryId(itinerary.getTravelItineraryId());

            Map<LocalDate, List<TravelPlanDayDTO>> groupedDailyItems = allDays.stream()
                .map(this::convertToTravelPlanDayDTO)
                .collect(Collectors.groupingBy(
                    TravelPlanDayDTO::getTraveltime,
                    TreeMap::new,
                    Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> list.stream()
                                    .sorted(Comparator.comparing(TravelPlanDayDTO::getTravelSequenceNumber))
                                    .collect(Collectors.toList())
                    )
                ));

            dto.setDailyItineraries(groupedDailyItems);
        } else {
            dto.setTravelItineraryId(null);
            dto.setDailyItineraries(new TreeMap<>());
        }
        return dto;
    }

   

    @Override
    public List<TravelPlanDayDTO> getDailyItemsForDate(Integer itineraryId, LocalDate date) {
        List<TravelPlanDay> entities = travelPlanDayRepository.findByTravelItinerary_TravelItineraryIdAndTraveltime(itineraryId, date);
        return entities.stream()
            .map(this::convertToTravelPlanDayDTO)
            .sorted(Comparator.comparing(TravelPlanDayDTO::getTravelSequenceNumber))
            .collect(Collectors.toList());
    }

    @Override
    public TravelItinerary saveTravelItineraryFromDto(@Valid TravelItineraryDTO dto) {
        if (dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new IllegalArgumentException("結束日期不能早於開始日期。");
        }

        TravelItinerary itinerary;
        if (dto.getTravelItineraryId() != null) {
            itinerary = travelItineraryRepository.findById(dto.getTravelItineraryId())
                .orElseThrow(() -> new IllegalArgumentException("找不到要更新的行程梯次，ID: " + dto.getTravelItineraryId()));
        } else {
            itinerary = new TravelItinerary();
            TravelPlan travelPlan = travelPlanRepository.findById(dto.getTravelPlanId())
                .orElseThrow(() -> new IllegalArgumentException("找不到關聯的旅行計畫，ID: " + dto.getTravelPlanId()));
            itinerary.setTravelPlan(travelPlan);
        }

        itinerary.setMaxTourist(dto.getMaxTourist());
        itinerary.setTotalPrice(dto.getTotalPrice());
        itinerary.setStartDate(dto.getStartDate());
        itinerary.setEndDate(dto.getEndDate());

        return travelItineraryRepository.save(itinerary);
    }


    @Override
    public Optional<TravelItinerary> getTravelItineraryById(Integer travelItineraryId) {
        return travelItineraryRepository.findById(travelItineraryId);
    }

    @Override
    public Optional<TravelItinerary> getTravelItineraryForPlan(Integer travelPlanId) {
        List<TravelItinerary> itineraries = travelItineraryRepository.findByTravelPlan_TravelPlanIdOrderByPublishedDateDesc(travelPlanId);
        if (!itineraries.isEmpty()) {
            return Optional.of(itineraries.get(0));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public TravelPlanPreviewDTO getTravelPlanPreview(Integer planId, Integer itineraryId) {
        TravelPlanPreviewDTO dto = new TravelPlanPreviewDTO();

        TravelPlan plan = travelPlanRepository.findById(planId)
            .orElseThrow(() -> new IllegalArgumentException("找不到旅行計畫 ID: " + planId));

        dto.setTravelPlanId(plan.getTravelPlanId());
        dto.setTravelTitle(plan.getTravelTitle());
        dto.setTravelPlanDescription(plan.getTravelPlanDescription());

        Optional<TravelItinerary> optionalItinerary = travelItineraryRepository.findById(itineraryId);
        if (optionalItinerary.isPresent()) {
            TravelItinerary itinerary = optionalItinerary.get();
            dto.setTravelItineraryId(itinerary.getTravelItineraryId());
            dto.setStartDate(itinerary.getStartDate());
            dto.setEndDate(itinerary.getEndDate());
            dto.setMaxTourist(itinerary.getMaxTourist());
            dto.setTotalPrice(itinerary.getTotalPrice());

            List<TravelPlanDay> planDays = travelPlanDayRepository.findByTravelItinerary_TravelItineraryId(itineraryId);

            Map<LocalDate, List<TravelPlanDayDTO>> grouped = planDays.stream()
                .map(this::convertToTravelPlanDayDTO)
                .collect(Collectors.groupingBy(
                    TravelPlanDayDTO::getTraveltime,
                    TreeMap::new,
                    Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> list.stream()
                                    .sorted(Comparator.comparing(TravelPlanDayDTO::getTravelSequenceNumber))
                                    .collect(Collectors.toList())
                    )
                ));

            dto.setDailyItineraries(grouped);
        } else {
            dto.setTravelItineraryId(null);
            dto.setDailyItineraries(new TreeMap<>());
        }

        return dto;
    }

    @Override
    public int calculateTotalDays(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) return 0;
        return (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }
    @Override
    public List<LocalDate> generateItineraryDates(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> dates = new ArrayList<>();
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            return dates; // 空列表
        }

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            dates.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }
        return dates;
    }

    @Override
    public TravelPlanPreviewDTO getTravelPlanPreview(Integer itineraryId) {
        TravelItinerary itinerary = travelItineraryRepository.findById(itineraryId)
                .orElseThrow(() -> new IllegalArgumentException("Itinerary not found, ID: " + itineraryId));

        List<TravelPlanDay> planDays = travelPlanDayRepository.findByTravelItinerary_TravelItineraryId(itineraryId);

        Map<LocalDate, List<TravelPlanDayDTO>> dailyItineraries = planDays.stream()
                .map(this::convertToTravelPlanDayDTO)
                .collect(Collectors.groupingBy(TravelPlanDayDTO::getTraveltime, TreeMap::new, Collectors.toList()));

        TravelPlanPreviewDTO previewDTO = new TravelPlanPreviewDTO();
        previewDTO.setTravelPlanId(itinerary.getTravelPlan().getTravelPlanId());
        previewDTO.setTravelTitle(itinerary.getTravelPlan().getTravelTitle());
        previewDTO.setTravelPlanDescription(itinerary.getTravelPlan().getTravelPlanDescription());
        previewDTO.setTravelPlanBannerUrl(itinerary.getTravelPlan().getTravelPlanBannerUrl());

        previewDTO.setTravelItineraryId(itinerary.getTravelItineraryId());
        previewDTO.setStartDate(itinerary.getStartDate());
        previewDTO.setEndDate(itinerary.getEndDate());
        previewDTO.setMaxTourist(itinerary.getMaxTourist());
        previewDTO.setTotalPrice(itinerary.getTotalPrice());
        previewDTO.setDailyItineraries(dailyItineraries);

        return previewDTO;
    }

    @Override
    public List<TravelPlan> searchByTitle(String keyword) {
        return travelPlanRepository.findByTravelTitleContaining(keyword);
    }


    @Override //01新增
    public Optional<TravelPlan> findById(Integer id) {
        return travelPlanRepository.findById(id);
    }


}
