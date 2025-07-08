package com.member.service.favorite;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.member.model.FavoriteTicketRepository;
import com.member.model.FavoriteTicketVO;
import com.member.model.memVO;
import com.ticket.model.Ticket;

@Service
public class FavoriteTicketService {
	
//	@Autowired
	private FavoriteTicketRepository favoriteTicketRepo;
	
	//Spring boot建構子注入
	private FavoriteTicketService(FavoriteTicketRepository favoriteTicketRepo) {
		this.favoriteTicketRepo = favoriteTicketRepo;
	}
		
	public void addFavoriteTickey(memVO member, Ticket ticket) {
		if(!favoriteTicketRepo.existsByMemberAndTicket(member, ticket)) {
			FavoriteTicketVO fav = new FavoriteTicketVO();
			fav.setMember(member);
			fav.setTicket(ticket);
			fav.setCreateTime(new Timestamp(System.currentTimeMillis()));
			favoriteTicketRepo.save(fav);
		}
	}
	
	public void removeFavoriteTicket(memVO member, Ticket ticket) {
		favoriteTicketRepo.deleteByMemberAndTicket(member, ticket);
	}
	
	public List<FavoriteTicketVO> getFavoriteByMember(memVO member){
		return favoriteTicketRepo.findByMember(member);
	}
}
