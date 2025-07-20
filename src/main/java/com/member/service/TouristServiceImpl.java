package com.member.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.member.model.TouristIdVO;
import com.member.model.TourOrderVO;
import com.member.model.TouristIdRepository; // 假設你的 repository

@Service
public class TouristServiceImpl implements TouristService {

    private final TouristIdRepository touristRepo;
    
    public TouristServiceImpl(TouristIdRepository touristRepo) {
    	this.touristRepo = touristRepo;
    }

    public TouristIdVO save(TouristIdVO tourist) {
    	System.out.println("✅ 儲存旅客: " + tourist.getTouristName() + ", 對應訂單ID: " + tourist.getTourOrder().getTourOrderId());
        return touristRepo.save(tourist); // JPA save，自動處理 insert/update
    }

    public Optional<TouristIdVO> findById(Integer touristId) {
        return touristRepo.findById(touristId);
    }

    public List<TouristIdVO> findByTourOrder(TourOrderVO tourOrder) {
        return touristRepo.findByTourOrder(tourOrder); // 假設 repository 有這個方法
    }

    public void deleteById(Integer touristId) {
        touristRepo.deleteById(touristId);
    }
}