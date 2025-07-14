package com.member.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.member.model.memVO;
import com.ticket.model.TicketOrder;
import com.member.model.MemberTicketOrderRepository;

@Service
public class MemberTicketOrderService {

    private final MemberTicketOrderRepository ticketOrderRepo;

    public MemberTicketOrderService(MemberTicketOrderRepository ticketOrderRepo) {
        this.ticketOrderRepo = ticketOrderRepo;
    }

    
    public Optional<TicketOrder> getOrderById(Integer id) {
        return ticketOrderRepo.findById(id);
    }

    public List<TicketOrder> getOrdersByMemberId(Integer memberId) {
        return ticketOrderRepo.findByMemberId(memberId);
    }
    
}