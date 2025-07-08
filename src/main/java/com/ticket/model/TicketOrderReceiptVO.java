package com.ticket.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "ticket_order_receipt")
public class TicketOrderReceiptVO {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ticketOrderReceiptId;

    @ManyToOne
    @JoinColumn(name = "ticket_order_id")
    private TicketOrderVO ticketOrder;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @Column(name = "ticket_count")
    private Integer ticketCount;

    @Column(name = "create_time")
    private Timestamp createTime;
    
    private String ticketImageBase64;

    public TicketOrderReceiptVO() {}
    
    public TicketOrderReceiptVO(Integer ticketOrderReceiptId, TicketOrderVO ticketOrder, Ticket ticket,
			Integer ticketCount, Timestamp createTime) {
		super();
		this.ticketOrderReceiptId = ticketOrderReceiptId;
		this.ticketOrder = ticketOrder;
		this.ticket = ticket;
		this.ticketCount = ticketCount;
		this.createTime = createTime;
	}
	

    public Integer getTicketOrderReceiptId() {
		return ticketOrderReceiptId;
	}

	public void setTicketOrderReceiptId(Integer ticketOrderReceiptId) {
		this.ticketOrderReceiptId = ticketOrderReceiptId;
	}

	public TicketOrderVO getTicketOrder() {
		return ticketOrder;
	}

	public void setTicketOrder(TicketOrderVO ticketOrder) {
		this.ticketOrder = ticketOrder;
	}

	public Ticket getTicket() {
		return ticket;
	}

	public void setTicket(Ticket ticket) {
		this.ticket = ticket;
	}

	public Integer getTicketCount() {
		return ticketCount;
	}

	public void setTicketCount(Integer ticketCount) {
		this.ticketCount = ticketCount;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	
	public String getTicketImageBase64() {
	    return ticketImageBase64;
	}

	public void setTicketImageBase64(String ticketImageBase64) {
	    this.ticketImageBase64 = ticketImageBase64;
	}
	
}