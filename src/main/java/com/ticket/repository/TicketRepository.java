// src/main/java/com/ticket/repository/TicketRepository.java
package com.ticket.repository;

import com.ticket.model.Ticket;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    // 指定搜尋
	List<Ticket> findByTicketNameContainingIgnoreCase(String ticketName);
}
