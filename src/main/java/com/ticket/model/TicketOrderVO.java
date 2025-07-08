package com.ticket.model;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.List;

import com.member.model.memVO;

@Entity
@Table(name = "ticket_order")
public class TicketOrderVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_order_id")
    private Integer ticketOrderId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private memVO member;

    @Column(name = "order_price")
    private Integer orderPrice;

    @Column(name = "order_status")
    private Integer orderStatus;

    @Column(name = "order_datetime")
    private Timestamp orderDatetime;

    @Column(name = "manager_id")
    private Integer managerId;
    
    @OneToMany(mappedBy = "ticketOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TicketOrderReceiptVO> ticketOrderReceipts;

    public TicketOrderVO() {}
    
    public TicketOrderVO(Integer ticketOrderId, memVO member, Integer orderPrice, Integer orderStatus,
			Timestamp orderDatetime, Integer managerId) {
		super();
		this.ticketOrderId = ticketOrderId;
		this.member = member;
		this.orderPrice = orderPrice;
		this.orderStatus = orderStatus;
		this.orderDatetime = orderDatetime;
		this.managerId = managerId;
	}
    
    public List<TicketOrderReceiptVO> getTicketOrderReceipts() {
        return ticketOrderReceipts;
    }

    public void setTicketOrderReceipts(List<TicketOrderReceiptVO> ticketOrderReceipts) {
        this.ticketOrderReceipts = ticketOrderReceipts;
    }
	// ===== Getter & Setter =====

    public Integer getTicketOrderId() {
        return ticketOrderId;
    }

    public void setTicketOrderId(Integer ticketOrderId) {
        this.ticketOrderId = ticketOrderId;
    }

    public memVO getMember() {
        return member;
    }

    public void setMember(memVO member) {
        this.member = member;
    }

    public Integer getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(Integer orderPrice) {
        this.orderPrice = orderPrice;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Timestamp getOrderDatetime() {
        return orderDatetime;
    }

    public void setOrderDatetime(Timestamp orderDatetime) {
        this.orderDatetime = orderDatetime;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }
}