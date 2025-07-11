package com.ticket.repository;

import com.ticket.model.TicketOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketOrderRepository extends JpaRepository<TicketOrder, Integer> {
    // 如需擴充，請加自訂查詢方法
}
