package com.ticket.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_order_receipt")
public class TicketOrderReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_order_receipt_id")
    private Integer ticketOrderReceiptId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_order_id")
    private TicketOrder ticketOrder;

    @Column(name = "ticket_id", nullable = false)
    private Integer ticketId;

    @Column(name = "ticket_count", nullable = false)
    private Integer ticketCount;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    // === Getter / Setter ===
    public Integer getTicketOrderReceiptId() { return ticketOrderReceiptId; }
    public void setTicketOrderReceiptId(Integer ticketOrderReceiptId) { this.ticketOrderReceiptId = ticketOrderReceiptId; }

    public TicketOrder getTicketOrder() { return ticketOrder; }
    public void setTicketOrder(TicketOrder ticketOrder) { this.ticketOrder = ticketOrder; }

    public Integer getTicketId() { return ticketId; }
    public void setTicketId(Integer ticketId) { this.ticketId = ticketId; }

    public Integer getTicketCount() { return ticketCount; }
    public void setTicketCount(Integer ticketCount) { this.ticketCount = ticketCount; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
