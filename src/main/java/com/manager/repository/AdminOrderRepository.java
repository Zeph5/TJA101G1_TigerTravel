package com.manager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.manager.model.DTO.OrderListDTO;
import com.member.model.TourOrderVO;

@Repository
public interface AdminOrderRepository extends JpaRepository<TourOrderVO, Integer> {
	@Query("SELECT new com.manager.model.DTO.OrderListDTO(" +
		       "o.tourOrderId, m.memberName, o.travelItineraryId, " +
		       "o.totalAmount, o.tourOrderStatus, o.createTime, SIZE(o.tourists)) " +
		       "FROM TourOrderVO o JOIN o.member m")
		List<OrderListDTO> findAllOrderDTOs();

}
