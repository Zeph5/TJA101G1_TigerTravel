package com.member.controller;

import com.member.model.MemberRepository;
import com.member.model.memVO;
import com.member.service.FavoriteSceneryService;
import com.member.service.MailService;
import com.member.service.MemberService;
import com.scenery.model.SceneryRepository;
import com.scenery.model.SceneryScoreRepository;
import com.scenery.model.SceneryScoreVO;
import com.scenery.model.SceneryService;
import com.scenery.model.SceneryVO;
import com.ticket.model.Ticket;
import com.ticket.repository.TicketRepository;
import com.travel_plan.model.TravelPlan;
import com.travel_plan.repository.TravelPlanRepository;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
    private FavoriteSceneryService favoriteSceneryService;
    @Autowired
    private SceneryRepository sceneryRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private SceneryScoreRepository sceneryScoreRepository;
    @Autowired
    private TravelPlanRepository travelPlanRepository;

    @GetMapping({"/", "/index"})
    public String showIndex(@RequestParam(required = false) String keyword, Model model) {
        System.out.println("=== INDEX PAGE LOADING ===");
        loadCommonData(model, keyword);
        return "index";
    }

 // Fixed scenery search handler - works for both authenticated and anonymous users
    @PostMapping("/scenery/search")
    public String searchSceneryPost(@RequestParam("keyword") String keyword,
                                   RedirectAttributes redirectAttributes,
                                   Authentication authentication) { // Optional authentication
        try {
            System.out.println("=== SCENERY SEARCH POST DEBUG ===");
            System.out.println("Keyword: " + keyword);
            System.out.println("User authenticated: " + (authentication != null && authentication.isAuthenticated()));
            
            // Validate keyword
            if (keyword == null || keyword.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "請輸入搜尋關鍵字");
                return "redirect:/";
            }
            
            // Encode keyword for URL safety
            String encodedKeyword = UriUtils.encodeQueryParam(keyword.trim(), StandardCharsets.UTF_8);
            String redirectUrl = "redirect:/search?keyword=" + encodedKeyword + "&page=1";
            
            System.out.println("Redirecting to: " + redirectUrl);
            return redirectUrl;
            
        } catch (Exception e) {
            System.err.println("Error in scenery search: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "搜尋時發生錯誤，請稍後再試");
            return "redirect:/";
        }
    }

    // Enhanced search results page - works for both authenticated and anonymous users
    @GetMapping("/search")
    public String searchScenery(@RequestParam String keyword,
                                @RequestParam(defaultValue = "1") int page,
                                Model model,
                                Authentication authentication) { // Optional authentication
        try {
            System.out.println("=== SEARCH RESULTS DEBUG ===");
            System.out.println("Keyword: " + keyword);
            System.out.println("Page: " + page);
            System.out.println("User authenticated: " + (authentication != null && authentication.isAuthenticated()));
            
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
            System.out.println("Found " + sceneryPage.getTotalElements() + " results");

            // Encode images and ratings
            encodeImagesAndRatings(sceneryPage.getContent());

            // Add attributes to model
            model.addAttribute("sceneryPage", sceneryPage);
            model.addAttribute("keyword", keyword.trim());
            
            // Add pagination info for the view
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", sceneryPage.getTotalPages());
            model.addAttribute("totalElements", sceneryPage.getTotalElements());
            
            // Add user authentication status for the template
            model.addAttribute("isAuthenticated", authentication != null && authentication.isAuthenticated());
            
            System.out.println("Returning template: frontend/scenery/scenerysearch");
            return "frontend/scenery/scenerysearch";
            
        } catch (Exception e) {
            System.err.println("Error in search results: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "搜尋時發生錯誤，請稍後再試");
            return "frontend/scenery/scenerysearch";
        }
    }

    // ===== SCENERY DETAIL PAGE WITH COMMENTS =====

    @GetMapping("/frontend/scenery/detail/{id}")
    public String showSceneryDetail(@PathVariable("id") Integer id, Model model, 
                                   HttpServletRequest request, Authentication authentication) {
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

            // Check if user is logged in and if scenery is favorited using Authentication
            if (authentication != null && authentication.isAuthenticated()) {
                try {
                    String memberAccount = authentication.getName();
                    System.out.println("Checking favorite status for user: " + memberAccount);
                    
                    Optional<memVO> memberOpt = memberService.findByAccount(memberAccount);
                    if (memberOpt.isPresent()) {
                        memVO currentUser = memberOpt.get();
                        boolean isFavorited = favoriteSceneryService.isFavorited(currentUser.getMemberId(), id);
                        model.addAttribute("isFavorited", isFavorited);
                        System.out.println("User " + currentUser.getMemberId() + " favorite status for scenery " + id + ": " + isFavorited);
                    } else {
                        System.out.println("User not found in database: " + memberAccount);
                        model.addAttribute("isFavorited", false);
                    }
                } catch (Exception e) {
                    System.err.println("Error checking favorite status: " + e.getMessage());
                    model.addAttribute("isFavorited", false);
                }
            } else {
                model.addAttribute("isFavorited", false);
                System.out.println("No user authenticated, setting isFavorited to false");
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

    // ===== TRAVEL DETAIL PAGE =====
    
    @GetMapping("/travel/detail/{id}")
    public String showTravelDetail(@PathVariable("id") Integer id, Model model, Authentication authentication) {
        try {
            System.out.println("=== TRAVEL DETAIL DEBUG ===");
            System.out.println("Requested travel ID: " + id);
            
            // Validate the ID
            if (id == null || id <= 0) {
                System.err.println("Invalid travel ID: " + id);
                model.addAttribute("error", "無效的旅程ID");
                return "redirect:/";
            }

            // Get the travel plan
            Optional<TravelPlan> travelPlanOpt = travelPlanRepository.findById(id);
            if (travelPlanOpt.isEmpty()) {
                System.err.println("Travel plan not found with ID: " + id);
                model.addAttribute("error", "旅程不存在 (ID: " + id + ")");
                return "redirect:/";
            }

            TravelPlan plan = travelPlanOpt.get();
            System.out.println("Found travel plan: " + plan.getTravelTitle());
            
            // Add the plan to model with both names for compatibility
            model.addAttribute("plan", plan);
            model.addAttribute("travelPlan", plan);

            // Add empty collections for now (you can populate these later with actual data)
            model.addAttribute("itineraries", new ArrayList<>());
            model.addAttribute("groupedDays", new HashMap<>());
            model.addAttribute("memberOrders", new ArrayList<>());
            
            // Check if user is authenticated for favorite status
            boolean isAuthenticated = false;
            memVO currentUser = null;
            
            if (authentication != null && authentication.isAuthenticated()) {
                try {
                    String memberAccount = authentication.getName();
                    System.out.println("Authenticated user: " + memberAccount);
                    
                    Optional<memVO> memberOpt = memberService.findByAccount(memberAccount);
                    if (memberOpt.isPresent()) {
                        currentUser = memberOpt.get();
                        isAuthenticated = true;
                        model.addAttribute("currentUser", currentUser);
                        System.out.println("Current user ID: " + currentUser.getMemberId());
                        
                        // You can implement favorite checking here if needed
                        // For now, set it to false
                        model.addAttribute("isFavorite", false);
                    } else {
                        System.out.println("User not found in database: " + memberAccount);
                        model.addAttribute("isFavorite", false);
                    }
                } catch (Exception e) {
                    System.err.println("Error checking authentication: " + e.getMessage());
                    model.addAttribute("isFavorite", false);
                }
            } else {
                System.out.println("No user authenticated");
                model.addAttribute("isFavorite", false);
            }
            
            model.addAttribute("isAuthenticated", isAuthenticated);
            
            System.out.println("Returning template: member/member-travel-detail");
            return "member/member-travel-detail";

        } catch (Exception e) {
            System.err.println("Error in showTravelDetail: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "載入旅程詳情時發生錯誤: " + e.getMessage());
            return "redirect:/";
        }
    }

    // ===== HELPER METHODS =====

    private void loadCommonData(Model model, String keyword) {
        try {
            System.out.println("Loading common data for homepage...");
            
            // Load top sceneries using cached ratings
            List<SceneryVO> topSceneries = sceneryService.getTopRatedSceneries(4);
            
            // Encode images for the sceneries
            encodeImagesAndRatings(topSceneries);
            model.addAttribute("topSceneries", topSceneries);
            System.out.println("Loaded " + topSceneries.size() + " top sceneries");

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
            System.out.println("Loaded " + ticketList.size() + " tickets");

            // Load travel tours for homepage
            try {
                List<TravelPlan> popularTours = travelPlanRepository.findTop6ByOrderByTravelPlanIdDesc();
                if (popularTours == null) {
                    popularTours = new ArrayList<>();
                }
                
                // Process tour data for display
                List<Map<String, Object>> tourList = popularTours.stream()
                        .limit(6)
                        .map(tour -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("travelPlanId", tour.getTravelPlanId());
                            map.put("travelTitle", tour.getTravelTitle());
                            map.put("travelPlanDescription", tour.getTravelPlanDescription());
                            map.put("travelPlanBannerUrl", tour.getTravelPlanBannerUrl());
                            return map;
                        }).toList();
                
                System.out.println("Loaded " + tourList.size() + " tours for homepage");
                
                // Debug each tour
                for (int i = 0; i < tourList.size(); i++) {
                    Map<String, Object> tour = tourList.get(i);
                    System.out.println("Tour " + i + ": ID=" + tour.get("travelPlanId") + ", Title=" + tour.get("travelTitle"));
                }
                
                model.addAttribute("tourList", tourList);
                
            } catch (Exception e) {
                System.err.println("Error loading travel tours: " + e.getMessage());
                e.printStackTrace();
                model.addAttribute("tourList", new ArrayList<>());
            }

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
            System.out.println("Loaded " + homepageImages.size() + " homepage images");
            
        } catch (Exception e) {
            System.err.println("Error loading common data: " + e.getMessage());
            e.printStackTrace();
            // Set default empty values to prevent template errors
            model.addAttribute("topSceneries", new ArrayList<>());
            model.addAttribute("ticketList", new ArrayList<>());
            model.addAttribute("tourList", new ArrayList<>());
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
    
    @PostMapping("/frontend/scenery/detail/{id}/favorite/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addFavorite(@PathVariable Integer id, 
                                                          HttpServletRequest request,
                                                          Authentication authentication) {
        System.out.println("=== ADD FAVORITE DEBUG ===");
        System.out.println("Scenery ID: " + id);
        System.out.println("Authentication: " + (authentication != null ? authentication.getName() : "null"));
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check authentication
            if (authentication == null || !authentication.isAuthenticated()) {
                System.out.println("No authentication - returning unauthorized");
                response.put("success", false);
                response.put("message", "請先登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // Get user from authentication
            String memberAccount = authentication.getName();
            System.out.println("Member account from auth: " + memberAccount);
            
            Optional<memVO> memberOpt = memberService.findByAccount(memberAccount);
            if (memberOpt.isEmpty()) {
                System.out.println("Member not found in database");
                response.put("success", false);
                response.put("message", "會員資料不存在，請重新登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            memVO currentUser = memberOpt.get();
            System.out.println("Found member: " + currentUser.getMemberAccount() + " (ID: " + currentUser.getMemberId() + ")");
            
            // Check if already favorited
            boolean alreadyFavorited = favoriteSceneryService.isFavorited(currentUser.getMemberId(), id);
            System.out.println("Already favorited: " + alreadyFavorited);
            
            if (alreadyFavorited) {
                System.out.println("Already in favorites - returning message");
                response.put("success", false);
                response.put("message", "已經在收藏清單中");
                return ResponseEntity.ok(response);
            }
            
            // Add to favorites
            System.out.println("Adding to favorites...");
            favoriteSceneryService.addFavorite(currentUser.getMemberId(), id);
            System.out.println("Successfully added to favorites!");
            
            response.put("success", true);
            response.put("message", "已加入收藏");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Error adding favorite: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "操作失敗");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/frontend/scenery/detail/{id}/favorite/remove")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removeFavorite(@PathVariable Integer id, 
                                                             HttpServletRequest request,
                                                             Authentication authentication) {
        System.out.println("=== REMOVE FAVORITE DEBUG ===");
        System.out.println("Scenery ID: " + id);
        System.out.println("Authentication: " + (authentication != null ? authentication.getName() : "null"));
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Check authentication
            if (authentication == null || !authentication.isAuthenticated()) {
                System.out.println("No authentication - returning unauthorized");
                response.put("success", false);
                response.put("message", "請先登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // Get user from authentication
            String memberAccount = authentication.getName();
            System.out.println("Member account from auth: " + memberAccount);
            
            Optional<memVO> memberOpt = memberService.findByAccount(memberAccount);
            if (memberOpt.isEmpty()) {
                System.out.println("Member not found in database");
                response.put("success", false);
                response.put("message", "會員資料不存在，請重新登入");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            memVO currentUser = memberOpt.get();
            System.out.println("Found member: " + currentUser.getMemberAccount() + " (ID: " + currentUser.getMemberId() + ")");
            
            // Remove from favorites
            System.out.println("Removing from favorites...");
            favoriteSceneryService.removeFavorite(currentUser.getMemberId(), id);
            System.out.println("Successfully removed from favorites!");
            
            response.put("success", true);
            response.put("message", "已從收藏中移除");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Error removing favorite: " + e.getMessage());
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "操作失敗");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}