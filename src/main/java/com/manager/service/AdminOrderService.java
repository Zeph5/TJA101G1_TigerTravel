package com.manager.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.manager.model.DTO.OrderListDTO;
import com.member.model.TourOrderVO;

@Service
public interface AdminOrderService {

	List<OrderListDTO> findAllOrders();

	OrderListDTO findOrderById(Integer id);

	void updateOrder(Integer id, TourOrderVO updatedOrder);

	OrderListDTO convertToDTO(TourOrderVO order);

}
