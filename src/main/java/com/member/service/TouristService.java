package com.member.service;

import java.util.List;
import java.util.Optional;

import com.member.model.TouristVO;
import com.member.model.TourOrderVO;

public interface TouristService {

    TouristVO save(TouristVO tourist); // 存新遊客或更新

    Optional<TouristVO> findById(Integer touristId); // 找單個遊客 by ID

    List<TouristVO> findByTourOrder(TourOrderVO tourOrder); // 找某訂單的所有遊客

    void deleteById(Integer touristId); // 刪遊客（如果需取消）
}