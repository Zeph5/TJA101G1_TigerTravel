package com.ticket.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ticket_order")
public class TicketOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_order_id")
    private Integer ticketOrderId;

    @Column(name = "member_id", nullable = false)
    private Integer memberId;

    @Column(name = "order_price", nullable = false)
    private BigDecimal orderPrice;

    @Column(name = "order_status")
    private Integer orderStatus;

    @Column(name = "order_datetime")
    private LocalDateTime orderDatetime;

    @Column(name = "manager_id")
    private Integer managerId;

    // 關聯明細（多對一）
    @OneToMany(mappedBy = "ticketOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TicketOrderReceipt> ticketOrderReceipts;

    // === Getter / Setter ===
    public Integer getTicketOrderId() { return ticketOrderId; }
    public void setTicketOrderId(Integer ticketOrderId) { this.ticketOrderId = ticketOrderId; }

    public Integer getMemberId() { return memberId; }
    public void setMemberId(Integer memberId) { this.memberId = memberId; }

    public BigDecimal getOrderPrice() { return orderPrice; }
    public void setOrderPrice(BigDecimal orderPrice) { this.orderPrice = orderPrice; }

    public Integer getOrderStatus() { return orderStatus; }
    public void setOrderStatus(Integer orderStatus) { this.orderStatus = orderStatus; }

    public LocalDateTime getOrderDatetime() { return orderDatetime; }
    public void setOrderDatetime(LocalDateTime orderDatetime) { this.orderDatetime = orderDatetime; }

    public Integer getManagerId() { return managerId; }
    public void setManagerId(Integer managerId) { this.managerId = managerId; }

    public List<TicketOrderReceipt> getTicketOrderReceipts() { return ticketOrderReceipts; }
    public void setTicketOrderReceipts(List<TicketOrderReceipt> ticketOrderReceipts) { this.ticketOrderReceipts = ticketOrderReceipts; }
}
