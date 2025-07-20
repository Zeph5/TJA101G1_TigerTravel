package com.manager.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.member.model.TouristVO;

public interface AdminTouristRepository extends JpaRepository<TouristVO, Integer> {

	// 這裡可以添加自定義查詢方法，例如根據旅客ID查找旅客信息
	Optional<TouristVO> findById(Integer id);





}
