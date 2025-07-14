package com.member.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteTravelPlanRepository extends JpaRepository<FavoriteTravelPlan, Integer> {
	
	//查詢某個會員的所有收藏
	List<FavoriteTravelPlan> findByMember(memVO member);
	
}
