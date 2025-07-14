package com.ticket.repository;

import com.ticket.model.TicketOrderReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TicketOrderReceiptRepository extends JpaRepository<TicketOrderReceipt, Integer> {
    // 查詢某筆訂單所有明細
    List<TicketOrderReceipt> findByTicketOrder_TicketOrderId(Integer ticketOrderId);
}
