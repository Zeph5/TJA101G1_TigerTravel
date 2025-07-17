package com.member.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ticket.model.TicketOrderReceipt;
import com.ticket.model.TicketOrder;
import com.member.model.MemberTicketOrderReceiptRepository;

@Service
public class MemberTicketOrderReceiptService {

    private final MemberTicketOrderReceiptRepository ticketOrderReceiptRepository;

    public MemberTicketOrderReceiptService(MemberTicketOrderReceiptRepository ticketOrderReceiptRepository) {
        this.ticketOrderReceiptRepository = ticketOrderReceiptRepository;
    }

    // ✅ 查詢指定訂單的所有票券明細
    public List<TicketOrderReceipt> getReceiptsByOrder(TicketOrder order) {
        return ticketOrderReceiptRepository.findByOrder(order);
    }
    
    
    
}
