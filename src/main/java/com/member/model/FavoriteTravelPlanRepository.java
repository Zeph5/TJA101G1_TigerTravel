package com.member.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.travel_plan.model.TravelPlan;

public interface FavoriteTravelPlanRepository extends JpaRepository<FavoriteTravelPlan, Integer> {
	
	//查詢某個會員的所有收藏
	List<FavoriteTravelPlan> findByMember(memVO member);
	
	boolean existsByMember_MemberIdAndTravelPlan_TravelPlanId(Integer memberId, Integer travelPlanId);

	void deleteByMember_MemberIdAndTravelPlan_TravelPlanId(Integer memberId, Integer travelPlanId);
    
    List<TravelPlan> getTravelPlansByMember(memVO member);
	
}
