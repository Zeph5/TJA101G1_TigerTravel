package com.ticket.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ticketId;

    private Integer ticketStock;

    private BigDecimal ticketPrice;

    private String ticketName;

    @Column(columnDefinition = "TEXT")
    private String ticketDescription;

    private Integer ticketStatus;

    @Lob
    private byte[] ticketImage;

    private LocalDateTime createTime;

    // Getters and Setters
    public Integer getTicketId() { return ticketId; }
    public void setTicketId(Integer ticketId) { this.ticketId = ticketId; }
    public Integer getTicketStock() { return ticketStock; }
    public void setTicketStock(Integer ticketStock) { this.ticketStock = ticketStock; }
    public BigDecimal getTicketPrice() { return ticketPrice; }
    public void setTicketPrice(BigDecimal ticketPrice) { this.ticketPrice = ticketPrice; }
    public String getTicketName() { return ticketName; }
    public void setTicketName(String ticketName) { this.ticketName = ticketName; }
    public String getTicketDescription() { return ticketDescription; }
    public void setTicketDescription(String ticketDescription) { this.ticketDescription = ticketDescription; }
    public Integer getTicketStatus() { return ticketStatus; }
    public void setTicketStatus(Integer ticketStatus) { this.ticketStatus = ticketStatus; }
    public byte[] getTicketImage() { return ticketImage; }
    public void setTicketImage(byte[] ticketImage) { this.ticketImage = ticketImage; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
