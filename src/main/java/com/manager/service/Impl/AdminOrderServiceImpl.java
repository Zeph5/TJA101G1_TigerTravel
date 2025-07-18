package com.manager.service.Impl;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.manager.model.DTO.OrderListDTO;
import com.manager.repository.AdminOrderRepository;
import com.manager.service.AdminOrderService;
import com.member.model.TourOrderRepository;
import com.member.model.TourOrderVO;

@Service
public class AdminOrderServiceImpl implements AdminOrderService {
	
	private final AdminOrderRepository adminOrderRepository;
	public AdminOrderServiceImpl(AdminOrderRepository adminOrderRepository) {
		this.adminOrderRepository = adminOrderRepository;
	}
	
	
	
	
	@Override
	public OrderListDTO convertToDTO(TourOrderVO order) {
		OrderListDTO dto = new OrderListDTO();
		BeanUtils.copyProperties(order, dto); 
		return dto;
	}
	

	@Override
	public OrderListDTO findOrderById(Integer id) {
		TourOrderVO orderVO = adminOrderRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Order not found with id: " + id)); // 查找訂單並處理未找到的情況
		return convertToDTO(orderVO);
	}

	@Override
	public void updateOrder(Integer id, TourOrderVO updatedOrder) {
		
	}



	@Override
	public List<OrderListDTO> findAllOrders() {
		return adminOrderRepository.findAllOrderDTOs(); // 使用自定義查詢方法獲取所有訂單的 DTO 列表
	}



}
