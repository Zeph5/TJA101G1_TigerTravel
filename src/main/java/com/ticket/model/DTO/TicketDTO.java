package com.ticket.model.DTO;

import java.time.LocalDateTime;

public class TicketDTO {
    private Long id;
    private String ticketName;
    private String ticketDescription;
    private Double ticketPrice;
    private Integer ticketStock;
    private Integer ticketStatus;
    private LocalDateTime createTime;
    private byte[] ticketImage; // 若需要傳遞圖片

    // 建構子
    public TicketDTO() {}

    public TicketDTO(Long id, String ticketName, String ticketDescription, Double ticketPrice,
                     Integer ticketStock, Integer ticketStatus, LocalDateTime createTime, byte[] ticketImage) {
        this.id = id;
        this.ticketName = ticketName;
        this.ticketDescription = ticketDescription;
        this.ticketPrice = ticketPrice;
        this.ticketStock = ticketStock;
        this.ticketStatus = ticketStatus;
        this.createTime = createTime;
        this.ticketImage = ticketImage;
    }

    // Getter & Setter
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getTicketName() {
        return ticketName;
    }
    public void setTicketName(String ticketName) {
        this.ticketName = ticketName;
    }

    public String getTicketDescription() {
        return ticketDescription;
    }
    public void setTicketDescription(String ticketDescription) {
        this.ticketDescription = ticketDescription;
    }

    public Double getTicketPrice() {
        return ticketPrice;
    }
    public void setTicketPrice(Double ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public Integer getTicketStock() {
        return ticketStock;
    }
    public void setTicketStock(Integer ticketStock) {
        this.ticketStock = ticketStock;
    }

    public Integer getTicketStatus() {
        return ticketStatus;
    }
    public void setTicketStatus(Integer ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public byte[] getTicketImage() {
        return ticketImage;
    }
    public void setTicketImage(byte[] ticketImage) {
        this.ticketImage = ticketImage;
    }
}
