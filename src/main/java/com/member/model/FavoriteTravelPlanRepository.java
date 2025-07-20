package com.member.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.travel_plan.model.TravelItinerary;
import com.travel_plan.model.TravelPlan;

public interface FavoriteTravelPlanRepository extends JpaRepository<FavoriteTravelPlan, Integer> {
	
	//查詢某個會員的所有收藏(包含TravelPlan)
	List<FavoriteTravelPlan> findByMember(memVO member);
	
	//檢查是否已收藏 (根據 member + plan ID)
	boolean existsByMember_MemberIdAndTravelPlan_TravelPlanId(Integer memberId, Integer travelPlanId);

	// 刪除收藏
	void deleteByMember_MemberIdAndTravelPlan_TravelPlanId(Integer memberId, Integer travelPlanId);
    
    // 檢查是否已收藏 (用物件)
    Optional<FavoriteTravelPlan> findByMemberAndTravelPlan(memVO member, TravelPlan travelPlan);
	
}
