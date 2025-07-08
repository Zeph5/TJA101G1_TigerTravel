package com.member.model;

import java.sql.Timestamp;

import com.ticket.model.Ticket;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "favorite_ticket")
public class FavoriteTicketVO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer favoriteId;
	
	@ManyToOne
	@JoinColumn(name = "member_id")
	private memVO member;
	
	@ManyToOne
	@JoinColumn(name = "ticket_id")
	private Ticket ticket;
	
	public FavoriteTicketVO() {	};
	
	public FavoriteTicketVO(Integer favoriteId, memVO member, Ticket ticket, Timestamp createTime) {
		super();
		this.favoriteId = favoriteId;
		this.member = member;
		this.ticket = ticket;
		this.createTime = createTime;
	}

	public Integer getFavoriteId() {
		return favoriteId;
	}

	public void setFavoriteId(Integer favoriteId) {
		this.favoriteId = favoriteId;
	}

	public memVO getMember() {
		return member;
	}

	public void setMember(memVO member) {
		this.member = member;
	}

	public Ticket getTicket() {
		return ticket;
	}

	public void setTicket(Ticket ticket) {
		this.ticket = ticket;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	private Timestamp createTime;
	
}
