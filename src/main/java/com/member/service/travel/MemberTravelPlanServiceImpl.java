package com.member.service.travel;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.travel_plan.model.TravelPlan;
import com.travel_plan.repository.TravelPlanRepository;
import com.travel_plan.service.TravelPlanService;



@Service
public class MemberTravelPlanServiceImpl implements MemberTravelPlanService{
	
	 private final TravelPlanRepository travelPlanRepo;
	 private final TravelPlanService travelPlanService;
	 
	 public MemberTravelPlanServiceImpl(TravelPlanRepository travelPlanRepo,
			 							TravelPlanService travelPlanService) {
		 this.travelPlanRepo = travelPlanRepo;
		 this.travelPlanService = travelPlanService;
	 }
	 
	 @Override
	    public Page<TravelPlan> getAllAvailablePlansPaged(int page, int size) {
	        Pageable pageable = PageRequest.of(page, size);
	        Page<TravelPlan> all = travelPlanRepo.findAll(pageable);

	        // 篩掉沒有 itinerary 的旅程
	        List<TravelPlan> filtered = all.stream()
	                .filter(plan -> travelPlanService.getTravelItineraryForPlan(plan.getTravelPlanId()).isPresent())
	                .toList();

	        return new PageImpl<>(filtered, pageable, filtered.size());
	    }

	    @Override
	    public Page<TravelPlan> searchAvailablePlansByKeyword(String keyword, int page, int size) {
	        Pageable pageable = PageRequest.of(page, size);
	        
	        Page<TravelPlan> all = travelPlanRepo.findByTravelTitleContaining(keyword, pageable);

	        List<TravelPlan> filtered = all.stream()
	                .filter(plan -> travelPlanService.getTravelItineraryForPlan(plan.getTravelPlanId()).isPresent())
	                .toList();

	        return new PageImpl<>(filtered, pageable, filtered.size());
	    }
}
