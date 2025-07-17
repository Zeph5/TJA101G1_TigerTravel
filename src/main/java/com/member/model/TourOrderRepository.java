package com.member.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TourOrderRepository extends JpaRepository<TourOrderVO, Integer> {

	
	// ✅ 使用物件導向關聯查詢
	List<TourOrderVO> findByMember(memVO member);

	
}
