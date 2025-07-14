package com.travel_plan.service.Impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scenery.model.SceneryRepository;
import com.travel_plan.dto.TravelItineraryDTO;
import com.travel_plan.dto.TravelPlanDayDTO;
import com.travel_plan.model.TravelItinerary;
import com.travel_plan.model.TravelPlan;
import com.travel_plan.model.TravelPlanDay;
import com.travel_plan.repository.TravelItineraryRepository;
import com.travel_plan.repository.TravelPlanDayRepository;
import com.travel_plan.service.TravelPlanDayService;

import jakarta.validation.Valid;

@Service
public class TravelPlanDayServiceImpl implements TravelPlanDayService {

	private final TravelPlanDayRepository travelPlanDayRepository;
	private final TravelItineraryRepository travelItineraryRepository;
	private final SceneryRepository sceneryRepository;

	public TravelPlanDayServiceImpl(TravelPlanDayRepository travelPlanDayRepository,
			TravelItineraryRepository travelItineraryRepository, SceneryRepository sceneryRepository) {
		this.travelPlanDayRepository = travelPlanDayRepository;
		this.travelItineraryRepository = travelItineraryRepository;
		this.sceneryRepository = sceneryRepository;
	}

	@Override
	public void createTravelPlanDayFromDto(TravelItinerary travelItinerary, @Valid TravelPlanDayDTO dto) {
		// 這裡實現 DTO 到 TravelPlanDay 的轉換邏輯
		// 例如：將 TravelPlanDayDTO 轉換為 TravelPlanDay 實體並儲存到資料庫
		// 返回儲存後的 TravelPlanDay 實體
	}

	@Override
	public List<TravelPlanDay> getDaysByItineraryId(Integer itineraryId) {
		return travelPlanDayRepository.findByTravelItinerary_TravelItineraryId(itineraryId);
	}

	@Override
	public Optional<TravelPlanDayDTO> getTravelPlanDayDTOById(Integer id) {
		return travelPlanDayRepository.findById(id).map(entity -> convertToDTO(entity));
	}

	private TravelPlanDayDTO convertToDTO(TravelPlanDay entity) {
		if (entity == null) {
			return null;
		}
		TravelPlanDayDTO dto = new TravelPlanDayDTO();
		BeanUtils.copyProperties(entity, dto);
		if (entity.getScenery() != null) {
			dto.setSceneryId(entity.getScenery().getSceneryId());
			dto.setSceneryName(entity.getScenery().getSceneryName());
		}
		return dto;
	}

	@Override
	@Transactional
	public void saveDailyItems(Integer itineraryId, LocalDate date, List<TravelPlanDayDTO> dailyItems) {
		List<TravelPlanDay> existingEntities = travelPlanDayRepository
				.findByTravelItinerary_TravelItineraryIdAndTraveltime(itineraryId, date);
		Map<Integer, TravelPlanDay> existingMap = existingEntities.stream()
				.collect(Collectors.toMap(TravelPlanDay::getTravelPlanDayId, e -> e));
		Set<Integer> toDeleteIds = new HashSet<>(existingMap.keySet());

		TravelItinerary itinerary = travelItineraryRepository.findById(itineraryId)
				.orElseThrow(() -> new IllegalArgumentException("Travel Itinerary not found with ID: " + itineraryId));
		TravelPlan travelPlan = itinerary.getTravelPlan();

		List<TravelPlanDay> itemsToSave = new ArrayList<>();

		for (int i = 0; i < dailyItems.size(); i++) {
			TravelPlanDayDTO dto = dailyItems.get(i);
			dto.setTraveltime(date);
			dto.setTravelDayNumber(calculateTravelDayNumber(itinerary.getTravelItineraryId(), date));
			

			TravelPlanDay entity;
			if (dto.getTravelPlanDayId() != null && existingMap.containsKey(dto.getTravelPlanDayId())) {
				entity = existingMap.get(dto.getTravelPlanDayId());
				toDeleteIds.remove(dto.getTravelPlanDayId());

				entity.setTravelSequenceNumber(dto.getTravelSequenceNumber());
				entity.setTraveltime(dto.getTraveltime());
				entity.setTravelDayNumber(dto.getTravelDayNumber());
				entity.setTravelItinerary(itinerary);
				entity.setTravelPlan(travelPlan);

				Integer currentSceneryId = entity.getScenery() != null ? entity.getScenery().getSceneryId() : null;
				if (!Objects.equals(currentSceneryId, dto.getSceneryId())) {
					if (dto.getSceneryId() != null) {
						sceneryRepository.findById(dto.getSceneryId()).ifPresent(entity::setScenery);
					} else {
						entity.setScenery(null);
					}
				}
			} else {
				entity = convertToTravelPlanDayEntity(dto);
				entity.setTravelItinerary(itinerary);
				entity.setTravelPlan(travelPlan);
			}
			itemsToSave.add(entity);
		}

		if (!itemsToSave.isEmpty()) {
			travelPlanDayRepository.saveAll(itemsToSave);
		}
		if (!toDeleteIds.isEmpty()) {
			travelPlanDayRepository.deleteAllById(toDeleteIds);
		}
	}

	@Override
	public Integer calculateTravelDayNumber(Integer itineraryId, LocalDate currentDate) {
		TravelItinerary itinerary = travelItineraryRepository.findById(itineraryId)
				.orElseThrow(() -> new IllegalArgumentException("找不到行程梯次，ID: " + itineraryId));

		if (itinerary.getStartDate() == null) {
			throw new IllegalStateException("行程梯次 (ID: " + itineraryId + ") 沒有設定開始日期。");
		}

		if (currentDate.isBefore(itinerary.getStartDate())) {
			throw new IllegalArgumentException("當前日期早於行程開始日期。");
		}

		long daysBetween = ChronoUnit.DAYS.between(itinerary.getStartDate(), currentDate);
		return (int) daysBetween + 1;
	}

	private TravelPlanDay convertToTravelPlanDayEntity(TravelPlanDayDTO dto) {
		TravelPlanDay entity = new TravelPlanDay();
		BeanUtils.copyProperties(dto, entity);
		if (dto.getSceneryId() != null) {
			sceneryRepository.findById(dto.getSceneryId()).ifPresent(entity::setScenery);
		} else {
			entity.setScenery(null);
		}
		return entity;
	}

	@Override
	public void updateTravelPlanDay(TravelPlanDayDTO travelPlanDayDTO, Integer itineraryId) {
		TravelPlanDay entity;
	    if (travelPlanDayDTO.getTravelPlanDayId() != null) {
	        entity = travelPlanDayRepository.findById(travelPlanDayDTO.getTravelPlanDayId())
	                 .orElse(new TravelPlanDay());
	    } else {
	        entity = new TravelPlanDay();
	    }
	    BeanUtils.copyProperties(travelPlanDayDTO, entity, "scenery");
	    if (travelPlanDayDTO.getSceneryId() != null) {
	        sceneryRepository.findById(travelPlanDayDTO.getSceneryId()).ifPresent(entity::setScenery);
	    }
	    TravelItinerary itinerary = travelItineraryRepository.findById(itineraryId)
	            .orElseThrow(() -> new IllegalArgumentException("找不到行程梯次"));
	    entity.setTravelItinerary(itinerary);
	    entity.setTravelPlan(itinerary.getTravelPlan());
	    travelPlanDayRepository.save(entity);
		
	}

}
