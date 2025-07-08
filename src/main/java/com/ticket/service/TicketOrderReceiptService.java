package com.ticket.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ticket.model.TicketOrderReceiptVO;
import com.ticket.model.TicketOrderVO;
import com.ticket.repository.TicketOrderReceiptRepository;

@Service
public class TicketOrderReceiptService {

    private final TicketOrderReceiptRepository ticketOrderReceiptRepository;

    public TicketOrderReceiptService(TicketOrderReceiptRepository ticketOrderReceiptRepository) {
        this.ticketOrderReceiptRepository = ticketOrderReceiptRepository;
    }

    // ✅ 查詢指定訂單的所有票券明細
    public List<TicketOrderReceiptVO> getReceiptsByOrder(TicketOrderVO order) {
        return ticketOrderReceiptRepository.findByOrder(order);
    }
}