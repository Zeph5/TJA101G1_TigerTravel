package com.member.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.member.model.TouristIdVO;
import com.member.model.TourOrderVO;

@Repository
public interface TouristIdRepository extends JpaRepository<TouristIdVO, Integer> {

    List<TouristIdVO> findByTourOrder(TourOrderVO tourOrder); // 自動生成 by 關聯
}