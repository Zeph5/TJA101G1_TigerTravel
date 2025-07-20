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
import com.scenery.model.SceneryVO;
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
	public void saveDailyItems(Integer itineraryId, LocalDate unusedDate, List<TravelPlanDayDTO> dailyItems) {
	    TravelItinerary itinerary = travelItineraryRepository.findById(itineraryId)
	            .orElseThrow(() -> new IllegalArgumentException("Travel Itinerary not found with ID: " + itineraryId));
	    TravelPlan travelPlan = itinerary.getTravelPlan();

	    List<TravelPlanDay> itemsToSave = new ArrayList<>();

	    for (int i = 0; i < dailyItems.size(); i++) {
	        TravelPlanDayDTO dto = dailyItems.get(i);

	        if (dto.getTravelPlanDayId() != null) {
	            continue; // 只處理新增的
	        }

	        LocalDate date = dto.getTraveltime();
	        if (date == null) {
	            continue;
	        }

	     // ✅ 只有在前端沒給序號時才自動編號，避免覆蓋掉原本值
	        if (dto.getTravelSequenceNumber() == null) {
	            dto.setTravelSequenceNumber(i + 1);
	        }

	        dto.setTravelDayNumber(calculateTravelDayNumber(itineraryId, date));

	        TravelPlanDay entity = convertToTravelPlanDayEntity(dto);
	        entity.setTravelItinerary(itinerary);
	        entity.setTravelPlan(travelPlan);
	        entity.setTraveltime(date);
	        entity.setTravelDayNumber(dto.getTravelDayNumber());
	        entity.setTravelSequenceNumber(dto.getTravelSequenceNumber());

	        if (dto.getSceneryId() != null) {
	            sceneryRepository.findById(dto.getSceneryId()).ifPresent(entity::setScenery);
	        }

	        itemsToSave.add(entity);
	    }

	    if (!itemsToSave.isEmpty()) {
	        travelPlanDayRepository.saveAll(itemsToSave);
	        System.out.println("✅ 儲存成功筆數：" + itemsToSave.size());
	    } else {
	        System.out.println("⚠️ 沒有要儲存的資料");
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
	@Transactional
	public void updateTravelPlanDay(TravelPlanDayDTO dto, Integer itineraryId) {
	    TravelPlanDay entity = travelPlanDayRepository.findById(dto.getTravelPlanDayId())
	        .orElseThrow(() -> new IllegalArgumentException("找不到對應的 TravelPlanDay，ID: " + dto.getTravelPlanDayId()));

	    entity.setTravelSequenceNumber(dto.getTravelSequenceNumber());
	    entity.setTraveltime(dto.getTraveltime());

	    // 加入防 null 的邏輯
	    if (dto.getTravelDayNumber() == null) {
	        int calculatedDayNumber = calculateTravelDayNumber(itineraryId, dto.getTraveltime());
	        System.out.println("TravelDayNumber 為 null，自動計算為: " + calculatedDayNumber);
	        entity.setTravelDayNumber(calculatedDayNumber);
	    } else {
	        entity.setTravelDayNumber(dto.getTravelDayNumber());
	    }

	    // 景點處理
	    if (dto.getSceneryId() != null) {
	        sceneryRepository.findById(dto.getSceneryId()).ifPresent(entity::setScenery);
	    } else {
	        entity.setScenery(null);
	    }

	    TravelItinerary itinerary = travelItineraryRepository.findById(itineraryId)
	        .orElseThrow(() -> new IllegalArgumentException("找不到行程梯次"));
	    entity.setTravelItinerary(itinerary);
	    entity.setTravelPlan(itinerary.getTravelPlan());

	    travelPlanDayRepository.save(entity);
	}

	@Override
	public List<TravelPlanDay> getDaysByItineraryIdAndDate(Integer itineraryId, LocalDate currentEditDate) {
		return travelPlanDayRepository.findByTravelItinerary_TravelItineraryIdAndTraveltime(itineraryId, currentEditDate);
	}

	@Override
	public List<SceneryVO> findAllScenery() {
		return sceneryRepository.findAll().stream()
				.map(scenery -> {
					SceneryVO sceneryVO = new SceneryVO();
					BeanUtils.copyProperties(scenery, sceneryVO);
					return sceneryVO;
				}).collect(Collectors.toList());
	}

	@Override
	public List<TravelPlanDay> getDaysByItineraryIdAndDateSortedBySequence(Integer itineraryId,
			LocalDate currentEditDate) {
		 return travelPlanDayRepository
			        .findByTravelItinerary_TravelItineraryIdAndTraveltimeOrderByTravelSequenceNumberAsc(itineraryId, currentEditDate);
	}

	@Override
	@Transactional
	public void deleteTravelPlanDayById(Integer travelPlanDayId, Integer itineraryId) {
		travelPlanDayRepository.deleteById(travelPlanDayId);
		
	}
	
	
	@Override
	public List<Integer> findSequenceNumbersByItineraryIdAndDate(Integer itineraryId, LocalDate date) {
		 return travelPlanDayRepository.findByTravelItinerary_TravelItineraryIdAndTraveltime(itineraryId, date)
			        .stream()
			        .map(TravelPlanDay::getTravelSequenceNumber)
			        .filter(Objects::nonNull)
			        .collect(Collectors.toList());
	}

	@Override
	public void updateTravelPlanDaySequence(Integer travelPlanDayId, String action) {
		TravelPlanDay day = travelPlanDayRepository.findById(travelPlanDayId)
				.orElseThrow(() -> new IllegalArgumentException("找不到 ID 為 " + travelPlanDayId + " 的旅行行程。"));

		
		if ("increase".equals(action)) {
	        day.setTravelSequenceNumber(day.getTravelSequenceNumber() + 1);
	    } else if ("decrease".equals(action) && day.getTravelSequenceNumber() > 1) {
	        day.setTravelSequenceNumber(day.getTravelSequenceNumber() - 1);
	    }

	    travelPlanDayRepository.save(day);
	}

	@Override
	@Transactional
	public void updateOrder(List<Integer> sortedIds) {
		for (int i = 0; i < sortedIds.size(); i++) {
	        Integer id = sortedIds.get(i);
	        TravelPlanDay day = travelPlanDayRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("找不到 ID: " + id));
	        day.setTravelSequenceNumber(i + 1);
	        travelPlanDayRepository.save(day);
	    }
	}
	@Transactional
	public void saveOrUpdateItems(Integer itineraryId, LocalDate date, List<TravelPlanDayDTO> dailyItems) {
	    TravelItinerary itinerary = travelItineraryRepository.findById(itineraryId)
	            .orElseThrow(() -> new IllegalArgumentException("找不到行程梯次"));

	    TravelPlan travelPlan = itinerary.getTravelPlan();

	    // 找出當日已存在的序號（避免重複）
	    List<Integer> existingSequences = travelPlanDayRepository
	            .findByTravelItinerary_TravelItineraryIdAndTraveltime(itineraryId, date)
	            .stream().map(TravelPlanDay::getTravelSequenceNumber)
	            .collect(Collectors.toList());

	    int maxSeq = existingSequences.stream().mapToInt(Integer::intValue).max().orElse(0);

	    for (TravelPlanDayDTO dto : dailyItems) {
	        // 若沒有序號就補
	        if (dto.getTravelSequenceNumber() == null) {
	            dto.setTravelSequenceNumber(++maxSeq);
	        }

	        if (dto.getTravelPlanDayId() != null) {
	            // 更新
	            TravelPlanDay existing = travelPlanDayRepository.findById(dto.getTravelPlanDayId())
	                    .orElseThrow(() -> new IllegalArgumentException("找不到每日行程 ID: " + dto.getTravelPlanDayId()));

	            existing.setScenery(sceneryRepository.findById(dto.getSceneryId()).orElse(null));
	            existing.setTraveltime(dto.getTraveltime());
	            existing.setTravelDayNumber(dto.getTravelDayNumber());
	            existing.setTravelSequenceNumber(dto.getTravelSequenceNumber());

	        } else {
	            // 新增
	            TravelPlanDay entity = convertToTravelPlanDayEntity(dto);
	            entity.setTravelItinerary(itinerary);
	            entity.setTravelPlan(travelPlan);
	            entity.setTraveltime(dto.getTraveltime());
	            entity.setTravelDayNumber(dto.getTravelDayNumber());
	            entity.setTravelSequenceNumber(dto.getTravelSequenceNumber());

	            if (dto.getSceneryId() != null) {
	                sceneryRepository.findById(dto.getSceneryId()).ifPresent(entity::setScenery);
	            }

	            travelPlanDayRepository.save(entity);
	        }
	    }
	}

	@Override
	public void deleteAllByItineraryIdAndDate(Integer itineraryId, LocalDate date) {
		List<TravelPlanDay> dayList = travelPlanDayRepository.findByTravelItinerary_TravelItineraryIdAndTraveltime(itineraryId, date);
	    travelPlanDayRepository.deleteAll(dayList);
	}

	@Override
	public void deleteMultipleByIds(Integer itineraryId, List<Integer> dayIds) {
	    List<TravelPlanDay> toDelete = travelPlanDayRepository.findAllById(dayIds).stream()
	        .filter(day -> day.getTravelItinerary().getTravelItineraryId().equals(itineraryId))
	        .collect(Collectors.toList());
	    travelPlanDayRepository.deleteAll(toDelete);
	}

	



}
