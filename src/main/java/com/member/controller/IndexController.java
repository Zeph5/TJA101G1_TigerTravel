package com.member.controller;

import com.member.model.MemberRepository;
import com.member.service.MailService;
import com.member.service.MemberService;
import com.scenery.model.SceneryService;
import com.scenery.model.SceneryVO;
import com.ticket.model.Ticket;
import com.ticket.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

import java.util.Base64;

@Controller
public class IndexController {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MailService mailService;
    @Autowired
    private SceneryService sceneryService;
    @Autowired
    private TicketRepository ticketRepository;

    @GetMapping({"/", "/index"})
    public String showIndex(@RequestParam(required = false) String keyword, Model model) {
        loadCommonData(model, keyword);
        return "index";
    }

    @PostMapping("/scenery/search")
    public String searchSceneryPost(@RequestParam("keyword") String keyword) {
        String encodedKeyword = UriUtils.encodeQueryParam(keyword, StandardCharsets.UTF_8);
        return "redirect:/search?keyword=" + encodedKeyword + "&page=1";
    }

    @GetMapping("/search")
    public String searchScenery(@RequestParam String keyword,
                                @RequestParam(defaultValue = "1") int page,
                                Model model) {
        int pageSize = 10;
        Pageable pageable = PageRequest.of(page - 1, pageSize);

        Page<SceneryVO> sceneryPage = sceneryService.searchSceneryByNameOrTag(keyword, pageable);

        encodeImagesAndRatings(sceneryPage.getContent());

        model.addAttribute("sceneryPage", sceneryPage);
        model.addAttribute("keyword", keyword);
        return "frontend/scenery/scenerysearch";
    }

    private void loadCommonData(Model model, String keyword) {
        List<Ticket> allTickets = ticketRepository.findAll();
        List<Map<String, Object>> ticketList = allTickets.stream()
                .limit(10)
                .map(ticket -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("ticketName", ticket.getTicketName());
                    map.put("ticketPrice", ticket.getTicketPrice());
                    map.put("ticketDescription", ticket.getTicketDescription());

                    if (ticket.getTicketImage() != null) {
                        String base64 = Base64.getEncoder().encodeToString(ticket.getTicketImage());
                        map.put("ticketImageBase64", base64);
                    } else {
                        map.put("ticketImageBase64", null);
                    }
                    return map;
                }).toList();
        model.addAttribute("ticketList", ticketList);

        List<String> homepageImages = new ArrayList<>();
        String imageDir = "src/main/resources/static/homepage_images";
        File folder = new File(imageDir);
        if (folder.exists() && folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                if (file.isFile()) {
                    homepageImages.add("/homepage_images/" + file.getName());
                }
            }
        }
        model.addAttribute("homepageImages", homepageImages);
    }

    private void encodeImagesAndRatings(List<SceneryVO> sceneryList) {
        for (SceneryVO scenery : sceneryList) {
            if (scenery.getSceneryBanner() != null) {
                String base64Image = Base64.getEncoder().encodeToString(scenery.getSceneryBanner());
                scenery.setImageUrl("data:image/png;base64," + base64Image);
            }

            scenery.setRatingStars(generateStarHtml(scenery.getRating()));}
        }


    private String generateStarHtml(Double rating) {
        if (rating == null) return "";
        StringBuilder stars = new StringBuilder();
        int fullStars = rating.intValue();
        for (int i = 0; i < fullStars; i++) {
            stars.append("★");
        }
        for (int i = fullStars; i < 5; i++) {
            stars.append("☆");
        }
        return stars.toString();
    }

    @GetMapping("/frontend/scenery/detail/{id}")
    public String showSceneryDetail(@PathVariable("id") Integer id, Model model) {
        SceneryVO scenery = sceneryService.getById(id);
        if (scenery == null) {
            return "error/404";
        }

        if (scenery.getSceneryBanner() != null) {
            String base64Image = Base64.getEncoder().encodeToString(scenery.getSceneryBanner());
            scenery.setImageUrl("data:image/png;base64," + base64Image);
        }

        model.addAttribute("scenery", scenery);
        return "frontend/scenery/Scenery";
    }
}
