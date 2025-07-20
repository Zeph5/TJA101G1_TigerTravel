package com.manager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.manager.model.DTO.OrderListDTO;
import com.member.model.TourOrderVO;
import com.member.model.TouristIdVO;
import com.member.model.TouristVO;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface AdminOrderRepository extends JpaRepository<TourOrderVO, Integer> {
	@Query("SELECT new com.manager.model.DTO.OrderListDTO(o.tourOrderId, m.memberName, o.travelItinerary.travelItineraryId, o.totalAmount, o.tourOrderStatus, o.createTime, SIZE(o.tourists)) FROM TourOrderVO o JOIN o.member m")
	List<OrderListDTO> findAllOrderDTOs();

	@Query("SELECT t.tourist FROM TouristIdVO t WHERE t.tourOrder.tourOrderId = :orderId")
	List<TouristVO> findTouristsByOrderId(@Param("orderId") Integer orderId);


}
