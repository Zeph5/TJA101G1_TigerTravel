package com.ticket.service;

import com.ticket.model.*;
import com.ticket.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // ★ 加入交易控制

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

    /**
     * 下單主邏輯
     * 下單時會自動檢查庫存並扣除，庫存不足時直接拋出例外並回滾所有操作
     */
    @Transactional // ★ 交易安全，任一步驟失敗都會 rollback
    public Integer createOrder(Integer memberId, List<Integer> ticketIds, List<Integer> quantities) {
        // 計算總金額
        BigDecimal total = BigDecimal.ZERO;
        List<TicketOrderReceipt> receiptList = new ArrayList<>();
        TicketOrder order = new TicketOrder();
        order.setMemberId(memberId);
        order.setOrderStatus(1); // 已付款（示範專案）
        order.setOrderDatetime(LocalDateTime.now());

        // ★ 1. 先檢查所有票券庫存是否足夠，避免分批扣庫存時部分失敗
        for (int i = 0; i < ticketIds.size(); i++) {
            Integer ticketId = ticketIds.get(i);
            Integer qty = quantities.get(i);

            Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(
                () -> new RuntimeException("找不到票券ID: " + ticketId)
            );

            if (ticket.getTicketStock() < qty) {
                throw new RuntimeException("票券 [" + ticket.getTicketName() + "] 庫存不足，剩餘 " + ticket.getTicketStock() + " 張");
            }
        }

        // ★ 2. 通過檢查後，開始正式扣庫存＋建明細
        for (int i = 0; i < ticketIds.size(); i++) {
            Integer ticketId = ticketIds.get(i);
            Integer qty = quantities.get(i);

            Ticket ticket = ticketRepository.findById(ticketId).orElseThrow();

            // 扣減庫存
            ticket.setTicketStock(ticket.getTicketStock() - qty);
            ticketRepository.save(ticket);

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
