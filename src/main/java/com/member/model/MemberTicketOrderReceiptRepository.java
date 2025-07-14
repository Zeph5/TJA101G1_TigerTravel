package com.member.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ticket.model.TicketOrderReceipt;
import com.ticket.model.TicketOrder;

import java.util.List;
import java.util.Optional;

public interface MemberTicketOrderReceiptRepository extends JpaRepository<TicketOrderReceipt, Integer> {

    // 查詢某張訂單的發票
    Optional<TicketOrderReceipt> findByTicketOrder(TicketOrder order);
    



    @Query("SELECT r FROM TicketOrderReceipt r WHERE r.ticketOrder = :order")
    List<TicketOrderReceipt> findByOrder(@Param("order") TicketOrder order);

}