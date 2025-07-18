package com.member.controller;

import com.member.model.MemberRepository;
import com.member.model.memVO;
import com.member.service.MailService;
import com.member.service.MemberService;
import com.scenery.model.SceneryRepository;
import com.scenery.model.SceneryScoreRepository;
import com.scenery.model.SceneryScoreVO;
import com.scenery.model.SceneryService;
import com.scenery.model.SceneryVO;
import com.ticket.model.Ticket;
import com.ticket.repository.TicketRepository;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
    private SceneryRepository sceneryRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private SceneryScoreRepository sceneryScoreRepository;

    @GetMapping({"/", "/index"})
    public String showIndex(@RequestParam(required = false) String keyword, Model model) {
        loadCommonData(model, keyword);
        return "index";
    }

    // Fixed scenery search handler - now properly handles POST requests
    @PostMapping("/scenery/search")
    public String searchSceneryPost(@RequestParam("keyword") String keyword, 
                                   RedirectAttributes redirectAttributes) {
        try {
            // Validate keyword
            if (keyword == null || keyword.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "請輸入搜尋關鍵字");
                return "redirect:/";
            }
            
            // Encode keyword for URL safety
            String encodedKeyword = UriUtils.encodeQueryParam(keyword.trim(), StandardCharsets.UTF_8);
            return "redirect:/search?keyword=" + encodedKeyword + "&page=1";
            
        } catch (Exception e) {
            System.err.println("Error in scenery search: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "搜尋時發生錯誤，請稍後再試");
            return "redirect:/";
        }
    }

    // Enhanced search results page
    @GetMapping("/search")
    public String searchScenery(@RequestParam String keyword,
                                @RequestParam(defaultValue = "1") int page,
                                Model model) {
        try {
            // Validate inputs
            if (keyword == null || keyword.trim().isEmpty()) {
                model.addAttribute("error", "搜尋關鍵字不能為空");
                return "frontend/scenery/scenerysearch";
            }
            
            if (page < 1) {
                page = 1;
            }
            
            int pageSize = 10;
            Pageable pageable = PageRequest.of(page - 1, pageSize);

            // Perform search
            Page<SceneryVO> sceneryPage = sceneryService.searchSceneryByNameOrTag(keyword.trim(), pageable);

            // Encode images and ratings
            encodeImagesAndRatings(sceneryPage.getContent());

            // Add attributes to model
            model.addAttribute("sceneryPage", sceneryPage);
            model.addAttribute("keyword", keyword.trim());
            
            // Add pagination info for the view
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", sceneryPage.getTotalPages());
            model.addAttribute("totalElements", sceneryPage.getTotalElements());
            
        } catch (Exception e) {
            System.err.println("Error in search results: " + e.getMessage());
            model.addAttribute("error", "搜尋時發生錯誤，請稍後再試");
        }
        
        return "frontend/scenery/scenerysearch";
    }

    // ===== SCENERY DETAIL PAGE WITH COMMENTS =====

    @GetMapping("/frontend/scenery/detail/{id}")
    public String showSceneryDetail(@PathVariable("id") Integer id, Model model) {
        try {
            // Validate scenery ID
            if (id == null || id <= 0) {
                return "error/404";
            }
            
            SceneryVO scenery = sceneryService.getById(id);
            if (scenery == null) {
                return "error/404";
            }

            // Encode banner image for display
            if (scenery.getSceneryBanner() != null) {
                String base64Image = Base64.getEncoder().encodeToString(scenery.getSceneryBanner());
                scenery.setImageUrl("data:image/png;base64," + base64Image);
            }

            // Load comments/scores with safer approach
            try {
                List<SceneryScoreVO> scores = sceneryScoreRepository.findByScenery_SceneryIdOrderByCreateTimeDesc(id);
                
                // Debug logging (remove in production)
                System.out.println("Found " + scores.size() + " scores for scenery " + id);
                
                scenery.setSceneryScores(scores);
            } catch (Exception e) {
                System.err.println("Error loading scores: " + e.getMessage());
                scenery.setSceneryScores(new ArrayList<>());
            }

            // Calculate and set rating safely
            try {
                Double rating = scenery.getRating();
                scenery.setRating(rating != null ? rating : 0.0);
            } catch (Exception e) {
                System.err.println("Error calculating rating: " + e.getMessage());
                scenery.setRating(0.0);
            }

            model.addAttribute("scenery", scenery);
            return "frontend/scenery/Scenery";
            
        } catch (Exception e) {
            System.err.println("Error in showSceneryDetail: " + e.getMessage());
            e.printStackTrace();
            return "error/500";
        }
    }

    // ===== COMMENT AND RATING FUNCTIONALITY =====

    @PostMapping("/frontend/scenery/detail/{id}/add-comment")
    public String addComment(@PathVariable("id") Integer sceneryId,
                            @RequestParam("score") Integer score,
                            @RequestParam("sceneryComment") String comment,
                            Authentication authentication,
                            RedirectAttributes redirectAttributes,
                            HttpServletRequest request) {
        
        System.out.println("=== ADD COMMENT DEBUG ===");
        System.out.println("Scenery ID: " + sceneryId);
        System.out.println("Score: " + score);
        System.out.println("Comment: " + comment);
        System.out.println("Authentication: " + (authentication != null ? authentication.getName() : "null"));
        
        try {
            // Validate inputs
            if (sceneryId == null || sceneryId <= 0) {
                redirectAttributes.addFlashAttribute("error", "無效的景點ID");
                return "redirect:/";
            }
            
            if (score == null || score < 1 || score > 5) {
                redirectAttributes.addFlashAttribute("error", "評分必須在1-5之間");
                return "redirect:/frontend/scenery/detail/" + sceneryId;
            }

            // Check authentication
            if (authentication == null) {
                redirectAttributes.addFlashAttribute("error", "請先登入");
                return "redirect:/login";
            }

            String memberAccount = authentication.getName();
            Optional<memVO> memberOpt = memberService.findByAccount(memberAccount);
            
            if (memberOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "會員資料不存在，請重新登入");
                return "redirect:/login";
            }
            
            memVO member = memberOpt.get();
            
            // Get the scenery
            SceneryVO scenery = sceneryService.getById(sceneryId);
            if (scenery == null) {
                redirectAttributes.addFlashAttribute("error", "景點不存在");
                return "redirect:/";
            }
            
            // Check if user has already rated this scenery
            boolean hasRated = sceneryScoreRepository.existsByMemberAndScenery(member.getMemberId(), sceneryId);
            
            if (hasRated) {
                redirectAttributes.addFlashAttribute("error", "您已經評論過這個景點了");
                return "redirect:/frontend/scenery/detail/" + sceneryId;
            }
            
            // Create new score/comment
            SceneryScoreVO scoreVO = new SceneryScoreVO();
            scoreVO.setMember(member);
            scoreVO.setScenery(scenery);
            scoreVO.setScore(score);
            
            // Clean comment text
            String cleanComment = comment != null ? comment.trim() : "";
            scoreVO.setSceneryComment(cleanComment.isEmpty() ? null : cleanComment);
            
            // Save the score
            sceneryScoreRepository.save(scoreVO);
            System.out.println("Score saved successfully!");
            
            // Update cached rating
            sceneryService.updateSceneryCachedRating(sceneryId);
            System.out.println("Cached rating updated!");
            
            redirectAttributes.addFlashAttribute("success", "評論提交成功！感謝您的分享");
            
        } catch (Exception e) {
            System.err.println("Error adding comment for scenery " + sceneryId + ": " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "評論提交失敗，請稍後再試");
        }
        
        return "redirect:/frontend/scenery/detail/" + sceneryId;
    }

    // ===== HELPER METHODS =====

    private void loadCommonData(Model model, String keyword) {
        try {
            // Load top sceneries using cached ratings
            List<SceneryVO> topSceneries = sceneryService.getTopRatedSceneries(4);
            
            // Encode images for the sceneries
            encodeImagesAndRatings(topSceneries);
            model.addAttribute("topSceneries", topSceneries);

            // Load tickets
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

            // Load homepage images
            List<String> homepageImages = new ArrayList<>();
            String imageDir = "src/main/resources/static/homepage_images";
            File folder = new File(imageDir);
            if (folder.exists() && folder.isDirectory()) {
                File[] files = folder.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile() && isImageFile(file.getName())) {
                            homepageImages.add("/homepage_images/" + file.getName());
                        }
                    }
                }
            }
            model.addAttribute("homepageImages", homepageImages);
            
        } catch (Exception e) {
            System.err.println("Error loading common data: " + e.getMessage());
            // Set default empty values to prevent template errors
            model.addAttribute("topSceneries", new ArrayList<>());
            model.addAttribute("ticketList", new ArrayList<>());
            model.addAttribute("homepageImages", new ArrayList<>());
        }
    }

    private boolean isImageFile(String filename) {
        if (filename == null) return false;
        String lower = filename.toLowerCase();
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || 
               lower.endsWith(".png") || lower.endsWith(".gif") || 
               lower.endsWith(".bmp") || lower.endsWith(".webp");
    }

    private void encodeImagesAndRatings(List<SceneryVO> sceneryList) {
        for (SceneryVO scenery : sceneryList) {
            try {
                if (scenery.getSceneryBanner() != null) {
                    String base64Image = Base64.getEncoder().encodeToString(scenery.getSceneryBanner());
                    scenery.setImageUrl("data:image/png;base64," + base64Image);
                }

                scenery.setRatingStars(generateStarHtml(scenery.getRating()));
            } catch (Exception e) {
                System.err.println("Error encoding scenery " + scenery.getSceneryId() + ": " + e.getMessage());
                // Set default values to prevent template errors
                scenery.setImageUrl("/images/default-scenery.jpg");
                scenery.setRatingStars("☆☆☆☆☆");
            }
        }
    }

    private String generateStarHtml(Double rating) {
        if (rating == null) return "☆☆☆☆☆";
        try {
            StringBuilder stars = new StringBuilder();
            int fullStars = rating.intValue();
            for (int i = 0; i < fullStars && i < 5; i++) {
                stars.append("★");
            }
            for (int i = fullStars; i < 5; i++) {
                stars.append("☆");
            }
            return stars.toString();
        } catch (Exception e) {
            return "☆☆☆☆☆";
        }
    }

    // ===== ADMIN DEBUG ENDPOINTS =====
    
    @GetMapping("/admin/update-all-ratings")
    @ResponseBody
    public String updateAllRatings() {
        try {
            System.out.println("=== MANUAL RATING CACHE UPDATE ===");
            sceneryService.updateAllSceneryCachedRatings();
            return "✅ All scenery ratings have been updated! Check console for details.";
        } catch (Exception e) {
            System.err.println("Error updating all ratings: " + e.getMessage());
            e.printStackTrace();
            return "❌ Error updating ratings: " + e.getMessage();
        }
    }
    
    @GetMapping("/admin/check-ratings")
    @ResponseBody
    public String checkRatings() {
        try {
            StringBuilder result = new StringBuilder();
            result.append("<h3>Scenery Ratings Comparison</h3>");
            
            List<SceneryVO> allActiveSceneries = sceneryRepository.findBySceneryStatus(1);
            
            for (SceneryVO scenery : allActiveSceneries) {
                double cachedRating = 0.0;
                double calculatedRating = 0.0;
                
                // Check cached rating
                if (scenery.getSceneryTotalScoreCount() != null && scenery.getSceneryTotalScoreCount() > 0) {
                    cachedRating = (double) scenery.getSceneryTotalScore() / scenery.getSceneryTotalScoreCount();
                }
                
                // Check real-time rating
                try {
                    calculatedRating = scenery.getRating();
                } catch (Exception e) {
                    calculatedRating = 0.0;
                }
                
                result.append("<p><strong>").append(scenery.getSceneryName()).append("</strong><br>");
                result.append("Cached: ").append(String.format("%.2f", cachedRating));
                result.append(" (").append(scenery.getSceneryTotalScore()).append("/").append(scenery.getSceneryTotalScoreCount()).append(")<br>");
                result.append("Calculated: ").append(String.format("%.2f", calculatedRating));
                
                if (Math.abs(cachedRating - calculatedRating) > 0.1) {
                    result.append(" <span style='color: red;'>⚠️ MISMATCH!</span>");
                }
                result.append("</p>");
            }
            
            return result.toString();
        } catch (Exception e) {
            return "❌ Error checking ratings: " + e.getMessage();
        }
    }
}