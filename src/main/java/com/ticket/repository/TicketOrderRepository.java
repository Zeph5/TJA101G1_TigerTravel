package com.ticket.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.member.model.memVO;
import com.ticket.model.TicketOrderVO;

public interface TicketOrderRepository extends JpaRepository<TicketOrderVO, Integer> {

    // 查詢某會員的所有訂單
    List<TicketOrderVO> findByMember(memVO member);
}
