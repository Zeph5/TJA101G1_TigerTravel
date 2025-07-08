package com.ticket.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ticket.model.TicketOrderReceiptVO;
import com.ticket.model.TicketOrderVO;

import java.util.List;
import java.util.Optional;

public interface TicketOrderReceiptRepository extends JpaRepository<TicketOrderReceiptVO, Integer> {

    // 查詢某張訂單的發票
    Optional<TicketOrderReceiptVO> findByTicketOrder(TicketOrderVO order);


    @Query("SELECT r FROM TicketOrderReceiptVO r WHERE r.ticketOrder = :order")
    List<TicketOrderReceiptVO> findByOrder(@Param("order") TicketOrderVO order);

}