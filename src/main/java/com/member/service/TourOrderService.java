package com.member.service;

import java.util.List;
import java.util.Optional;

import com.member.model.TourOrderVO;
import com.member.model.memVO;

public interface TourOrderService {
    List<TourOrderVO> findByMember(memVO member);
    
    Optional<TourOrderVO> findById(Integer tourOrderId);
    
    TourOrderVO save(TourOrderVO order);
}

