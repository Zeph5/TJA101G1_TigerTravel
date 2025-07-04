package com.ticket.tool;

import com.ticket.model.Ticket;
import com.ticket.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.nio.file.*;
import java.util.Optional;

@Component
public class TicketImageBatchInsert implements CommandLineRunner {

    @Autowired
    private TicketRepository ticketRepository;

    @Override
    public void run(String... args) throws Exception {
//    	/TJA101G1_TigerTravel/src/main/resources/static/ticket _image/1.jpg
        String imageDir = "src/main/resources/static/ticket_image";
        // 遍歷所有 jpg 檔案
        Files.list(Paths.get(imageDir))
            .filter(path -> path.toString().toLowerCase().endsWith(".jpg"))
            .forEach(path -> {
                try {
                    String filename = path.getFileName().toString();
                    // 取出檔名數字（ticket_id）
                    String idStr = filename.substring(0, filename.lastIndexOf('.'));
                    Integer ticketId = Integer.valueOf(idStr);
                    Optional<Ticket> optTicket = ticketRepository.findById(ticketId);
                    if (optTicket.isPresent()) {
                        Ticket ticket = optTicket.get();
                        byte[] imageBytes = Files.readAllBytes(path);
                        ticket.setTicketImage(imageBytes);
                        ticketRepository.save(ticket);
                        System.out.println("成功為票券ID " + ticketId + " 插入圖片：" + filename);
                    } else {
                        System.out.println("查無票券ID：" + ticketId + "，檔名：" + filename);
                    }
                } catch (Exception e) {
                    System.err.println("檔案處理錯誤：" + path + "：" + e.getMessage());
                }
            });
    }
}
