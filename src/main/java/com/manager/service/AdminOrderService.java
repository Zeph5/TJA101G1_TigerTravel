package com.manager.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.manager.model.DTO.OrderListDTO;
import com.manager.model.DTO.TouristListDTO;
import com.member.model.TourOrderVO;
import com.member.model.TouristIdVO;

@Service
public interface AdminOrderService {

	List<OrderListDTO> findAllOrders();

	OrderListDTO findOrderById(Integer id);



	OrderListDTO convertToDTO(TourOrderVO order);

	void updateOrder(Integer id, OrderListDTO updatedOrder);

	TouristListDTO findOrderByTouristId(Integer id);

	TourOrderVO findOrderEntityById(Integer id);
	
}
