package com.ticket.service;

import com.ticket.model.*;
import com.ticket.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class TicketOrderService {

    @Autowired
    private TicketOrderRepository ticketOrderRepository;

    @Autowired
    private TicketOrderReceiptRepository ticketOrderReceiptRepository;

    @Autowired
    private TicketRepository ticketRepository;

    // 下單主邏輯
    public Integer createOrder(Integer memberId, List<Integer> ticketIds, List<Integer> quantities) {
        // 計算總金額
        BigDecimal total = BigDecimal.ZERO;
        List<TicketOrderReceipt> receiptList = new ArrayList<>();
        TicketOrder order = new TicketOrder();
        order.setMemberId(memberId);
        order.setOrderStatus(1); // 已付款（示範專案）
        order.setOrderDatetime(LocalDateTime.now());

        for (int i = 0; i < ticketIds.size(); i++) {
            Integer ticketId = ticketIds.get(i);
            Integer qty = quantities.get(i);
            Ticket ticket = ticketRepository.findById(ticketId).orElseThrow();

            BigDecimal price = ticket.getTicketPrice().multiply(BigDecimal.valueOf(qty));
            total = total.add(price);

            TicketOrderReceipt receipt = new TicketOrderReceipt();
            receipt.setTicketOrder(order);
            receipt.setTicketId(ticketId);
            receipt.setTicketCount(qty);
            receipt.setCreateTime(LocalDateTime.now());
            receiptList.add(receipt);
        }

        order.setOrderPrice(total);
        order.setTicketOrderReceipts(receiptList);

        ticketOrderRepository.save(order); // 會一併存明細（cascade 設定）

        return order.getTicketOrderId();
    }

    // 查詢訂單明細
    public TicketOrder getOrderDetail(Integer orderId) {
        return ticketOrderRepository.findById(orderId).orElse(null);
    }
}
