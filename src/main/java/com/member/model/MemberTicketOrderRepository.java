package com.member.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.member.model.memVO;
import com.ticket.model.TicketOrder;

public interface MemberTicketOrderRepository extends JpaRepository<TicketOrder, Integer> {

    
    List<TicketOrder> findByMemberId(Integer memberId);
    
}