package com.member.service.travel;

import org.springframework.data.domain.Page;

import com.travel_plan.model.TravelPlan;

public interface MemberTravelPlanService {
    Page<TravelPlan> getAllAvailablePlansPaged(int page, int size);
    Page<TravelPlan> searchAvailablePlansByKeyword(String keyword, int page, int size);
}

