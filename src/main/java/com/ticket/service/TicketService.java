package com.ticket.service;


import com.ticket.model.Ticket;
import com.ticket.repository.TicketRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    public Page<Ticket> getTickets(Pageable pageable) {
        return ticketRepository.findAll(pageable);
    }
    public List<Ticket> findByName(String keyword) {
        return ticketRepository.findByTicketNameContainingIgnoreCase(keyword);
    }
    
    public Optional<Ticket> findById(Integer id) {
        return ticketRepository.findById(id);
    }
    /** 新增或更新票券 */
    public Ticket save(Ticket ticket) {
        return ticketRepository.save(ticket);
    }
    
}
