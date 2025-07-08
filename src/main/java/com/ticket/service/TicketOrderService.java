package com.ticket.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.member.model.memVO;
import com.ticket.model.TicketOrderVO;
import com.ticket.repository.TicketOrderRepository;

@Service
public class TicketOrderService {

    private final TicketOrderRepository ticketOrderRepo;

    public TicketOrderService(TicketOrderRepository ticketOrderRepo) {
        this.ticketOrderRepo = ticketOrderRepo;
    }

    public List<TicketOrderVO> getOrdersByMember(memVO member) {
        return ticketOrderRepo.findByMember(member);
    }
    
    public Optional<TicketOrderVO> getOrderById(Integer id) {
        return ticketOrderRepo.findById(id);
    }

}
