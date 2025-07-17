package com.member.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.member.model.TouristVO;
import com.member.model.TourOrderVO;

public interface TouristRepository extends JpaRepository<TouristVO, Integer> {

    List<TouristVO> findByTourOrder(TourOrderVO tourOrder); // 自動生成 by 關聯
}