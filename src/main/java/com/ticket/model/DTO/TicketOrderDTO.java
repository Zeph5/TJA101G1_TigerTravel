package com.ticket.model.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// 主 DTO
public class TicketOrderDTO {
    private Integer orderId;                // 訂單ID
    private String memberName;              // 購買會員名稱
    private LocalDateTime orderDatetime;    // 購買時間
    private BigDecimal orderPrice;          // 購買票券總價
    private List<OrderDetailDTO> orderDetails; // 明細

    // Getter & Setter
    public Integer getOrderId() {
        return orderId;
    }
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getMemberName() {
        return memberName;
    }
    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public LocalDateTime getOrderDatetime() {
        return orderDatetime;
    }
    public void setOrderDatetime(LocalDateTime orderDatetime) {
        this.orderDatetime = orderDatetime;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }
    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }

    public List<OrderDetailDTO> getOrderDetails() {
        return orderDetails;
    }
    public void setOrderDetails(List<OrderDetailDTO> orderDetails) {
        this.orderDetails = orderDetails;
    }

    // ====== Inner Class for 訂單明細 ======
    public static class OrderDetailDTO {
        private String ticketName;      // 票券名稱
        private Integer quantity;       // 購買數量

        public String getTicketName() {
            return ticketName;
        }
        public void setTicketName(String ticketName) {
            this.ticketName = ticketName;
        }

        public Integer getQuantity() {
            return quantity;
        }
        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}
