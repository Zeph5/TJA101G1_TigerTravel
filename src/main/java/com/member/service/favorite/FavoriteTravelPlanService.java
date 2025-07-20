package com.member.service.favorite;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.member.model.FavoriteTravelPlan;
import com.member.model.FavoriteTravelPlanRepository;
import com.member.model.MemberRepository;
import com.member.model.memVO;
import com.travel_plan.model.TravelItinerary;
import com.travel_plan.model.TravelPlan;

public interface FavoriteTravelPlanService {
	boolean existsByMember_MemberIdAndTravelPlan_TravelPlanId(Integer memberId, Integer travelPlanId);

    void deleteByMember_MemberIdAndTravelPlan_TravelPlanId(Integer memberId, Integer travelPlanId);

    FavoriteTravelPlan save(FavoriteTravelPlan favorite);
    
    List<TravelPlan> getTravelPlansByMember(memVO member);
    
    void addFavorite(memVO member, TravelPlan travelPlan);
    
    void removeFavorite(memVO member, TravelPlan plan);
    List<FavoriteTravelPlan> getFavoritesByMember(memVO member);
    boolean isFavorite(memVO member, TravelPlan plan);
}

