package com.member.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.member.model.TourOrderRepository;
import com.member.model.TourOrderVO;
import com.member.model.memVO;
import com.ticket.model.TicketOrder;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class TourOrderServiceImpl implements TourOrderService {
	
	private final TourOrderRepository tourOrderRepo;
	
	public TourOrderServiceImpl(TourOrderRepository tourOrderRepo) {
		this.tourOrderRepo = tourOrderRepo;
	}


	public List<TourOrderVO> findByMember(memVO member) {
		return tourOrderRepo.findByMember(member);
	}


	public Optional<TourOrderVO> findById(Integer tourOrderId) {
		return tourOrderRepo.findById(tourOrderId);
	}


	public TourOrderVO save(TourOrderVO order) {
		return tourOrderRepo.save(order);
	}

}
