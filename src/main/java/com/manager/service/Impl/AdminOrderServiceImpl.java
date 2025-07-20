package com.manager.service.Impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.manager.model.DTO.OrderListDTO;
import com.manager.model.DTO.TouristListDTO;
import com.manager.repository.AdminOrderRepository;
import com.manager.repository.AdminTouristRepository;
import com.manager.service.AdminOrderService;
import com.member.model.TourOrderRepository;
import com.member.model.TourOrderVO;
import com.member.model.TouristIdVO;

@Service
public class AdminOrderServiceImpl implements AdminOrderService {
	
	private final AdminOrderRepository adminOrderRepository;
	private final AdminTouristRepository adminTouristRepository;
	public AdminOrderServiceImpl(AdminOrderRepository adminOrderRepository,
			AdminTouristRepository adminTouristRepository) {
		this.adminOrderRepository = adminOrderRepository;
		this.adminTouristRepository = adminTouristRepository;
	}
	
	
	
	
	@Override
	public OrderListDTO convertToDTO(TourOrderVO order) {
		OrderListDTO dto = new OrderListDTO();
		BeanUtils.copyProperties(order, dto); // 使用 BeanUtils 複製屬性
		 if (order.getMember() != null) {
		        dto.setMemberName(order.getMember().getMemberName()); // 假設是 memName
		    }
		return dto;
	}	

	@Override
	public OrderListDTO findOrderById(Integer id) {
		TourOrderVO orderVO = adminOrderRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Order not found with id: " + id)); // 查找訂單並處理未找到的情況
		return convertToDTO(orderVO);
	}

	



	@Override
	public List<OrderListDTO> findAllOrders() {
		return adminOrderRepository.findAllOrderDTOs(); // 使用自定義查詢方法獲取所有訂單的 DTO 列表
	}

	@Override
	public void updateOrder(Integer id, OrderListDTO updatedOrder) {
		TourOrderVO existingOrder = adminOrderRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
		BeanUtils.copyProperties(updatedOrder, existingOrder, "id"); // 更新訂單屬性，排除 id 屬性
		adminOrderRepository.save(existingOrder); // 保存更新後的訂單資料		
	}




	@Override
	public TouristListDTO findOrderByTouristId(Integer id) {
		TouristVO touristVO = adminTouristRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Tourist not found with id: " + id)); // 查找旅客並處理未找到的情況
		TouristListDTO touristDTO = new TouristListDTO();
		BeanUtils.copyProperties(touristVO, touristDTO); // 使用 BeanUtils 複製屬性
		return touristDTO;
	}




	@Override
	public TourOrderVO findOrderEntityById(Integer id) {
		return adminOrderRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Order not found with id: " + id)); // 查找訂單實體並處理未找到的情況
	}










}
