package com.member.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ticket.model.Ticket;

@Repository
public interface FavoriteTicketRepository extends JpaRepository<FavoriteTicketVO, Integer> {
	List<FavoriteTicketVO> findByMember(memVO member);
	
	boolean existsByMemberAndTicket(memVO member, Ticket ticket);
	
	void deleteByMemberAndTicket(memVO member, Ticket ticeky);

}