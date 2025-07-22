package com.scenery.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.scenery.model.SceneryScoreRepository;
import com.scenery.model.SceneryScoreService;
import com.scenery.model.SceneryScoreVO;

@Controller
@RequestMapping("/sceneryscore")
public class SceneryScoreController {

    @Autowired
    private SceneryScoreService scoreService;
    
    @Autowired
    private SceneryScoreRepository scoreRepository;

    @GetMapping("/findAll")
    public String findAllScores(Model model) {
        try {
            System.out.println("=== CONTROLLER: findAllScores() START ===");
            
            // Check authentication
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("Current user: " + (auth != null ? auth.getName() : "NOT AUTHENTICATED"));
            System.out.println("User authorities: " + (auth != null ? auth.getAuthorities() : "NONE"));
            
            // Test repository direct access first
            System.out.println("--- Testing Repository Direct Access ---");
            try {
                long repoCount = scoreRepository.count();
                System.out.println("Repository count(): " + repoCount);
                
                List<SceneryScoreVO> repoScores = scoreRepository.findAll();
                System.out.println("Repository findAll(): " + repoScores.size() + " scores");
            } catch (Exception e) {
                System.err.println("Repository direct access failed: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Test service access
            System.out.println("--- Testing Service Access ---");
            List<SceneryScoreVO> scores = scoreService.findAll();
            System.out.println("Service returned: " + scores.size() + " scores");
            
            // Add comprehensive model attributes
            model.addAttribute("scoreList", scores);
            model.addAttribute("totalCount", scores.size());
            model.addAttribute("hasData", !scores.isEmpty());
            model.addAttribute("currentUser", auth != null ? auth.getName() : "Anonymous");
            
            // Add debug info to model
            if (scores.isEmpty()) {
                model.addAttribute("debugMessage", "No scores found. Check database connection and data.");
                System.out.println("WARNING: No scores to display!");
            } else {
                System.out.println("SUCCESS: " + scores.size() + " scores will be displayed");
                // Add first score for debugging
                SceneryScoreVO firstScore = scores.get(0);
                model.addAttribute("firstScoreDebug", 
                    "First score - ID: " + firstScore.getScoreId() + 
                    ", Member: " + (firstScore.getMember() != null ? firstScore.getMember().getMemberAccount() : "NULL") +
                    ", Scenery: " + (firstScore.getScenery() != null ? firstScore.getScenery().getSceneryName() : "NULL"));
            }
            
            System.out.println("Model attributes set. Template: scenery/findallsceneryscore");
            System.out.println("=== CONTROLLER: findAllScores() END ===");
            
            return "scenery/findallsceneryscore";
            
        } catch (Exception e) {
            System.err.println("=== CRITICAL CONTROLLER ERROR ===");
            System.err.println("Error in findAllScores(): " + e.getMessage());
            System.err.println("Error type: " + e.getClass().getSimpleName());
            e.printStackTrace();
            System.err.println("=== END CRITICAL ERROR ===");
            
            // Add error info to model
            model.addAttribute("scoreList", List.of());
            model.addAttribute("totalCount", 0);
            model.addAttribute("hasData", false);
            model.addAttribute("error", "Critical error: " + e.getMessage());
            model.addAttribute("errorType", e.getClass().getSimpleName());
            
            return "scenery/findallsceneryscore";
        }
    }

    @GetMapping("/search")
    public String searchScores(
            @RequestParam(required = false) String memberAccount,
            @RequestParam(required = false) String sceneryName,
            Model model) {
        
        try {
            System.out.println("=== CONTROLLER: searchScores() START ===");
            System.out.println("Search parameters:");
            System.out.println("  - memberAccount: '" + memberAccount + "'");
            System.out.println("  - sceneryName: '" + sceneryName + "'");
            
            List<SceneryScoreVO> result = scoreService.searchScores(memberAccount, sceneryName);
            System.out.println("Search returned " + result.size() + " results");
            
            // Set model attributes
            model.addAttribute("scoreList", result);
            model.addAttribute("totalCount", result.size());
            model.addAttribute("hasData", !result.isEmpty());
            model.addAttribute("memberAccount", memberAccount);
            model.addAttribute("sceneryName", sceneryName);
            model.addAttribute("isSearchResult", true);
            
            // Build search info string
            StringBuilder searchInfo = new StringBuilder("搜尋結果: ");
            if (memberAccount != null && !memberAccount.trim().isEmpty()) {
                searchInfo.append("會員帳號包含「").append(memberAccount).append("」");
            }
            if (sceneryName != null && !sceneryName.trim().isEmpty()) {
                if (memberAccount != null && !memberAccount.trim().isEmpty()) {
                    searchInfo.append(" 且 ");
                }
                searchInfo.append("景點名稱包含「").append(sceneryName).append("」");
            }
            if ((memberAccount == null || memberAccount.trim().isEmpty()) && 
                (sceneryName == null || sceneryName.trim().isEmpty())) {
                searchInfo.append("顯示所有評價");
            }
            
            model.addAttribute("searchInfo", searchInfo.toString());
            
            System.out.println("Search info: " + searchInfo.toString());
            System.out.println("=== CONTROLLER: searchScores() END ===");
            
            return "scenery/findallsceneryscore";
            
        } catch (Exception e) {
            System.err.println("=== ERROR in searchScores() ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.err.println("=== END SEARCH ERROR ===");
            
            model.addAttribute("scoreList", List.of());
            model.addAttribute("totalCount", 0);
            model.addAttribute("hasData", false);
            model.addAttribute("error", "搜尋錯誤: " + e.getMessage());
            model.addAttribute("memberAccount", memberAccount);
            model.addAttribute("sceneryName", sceneryName);
            
            return "scenery/findallsceneryscore";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteScore(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("=== CONTROLLER: deleteScore(" + id + ") START ===");
            
            // Check authentication
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("Delete requested by: " + (auth != null ? auth.getName() : "ANONYMOUS"));
            
            scoreService.deleteById(id);
            
            System.out.println("Delete successful for score ID: " + id);
            redirectAttributes.addFlashAttribute("successMessage", "評價已成功刪除！(ID: " + id + ")");
            redirectAttributes.addFlashAttribute("messageType", "success");
            
        } catch (Exception e) {
            System.err.println("=== ERROR in deleteScore() ===");
            System.err.println("Error deleting score ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            System.err.println("=== END DELETE ERROR ===");
            
            redirectAttributes.addFlashAttribute("errorMessage", "刪除失敗: " + e.getMessage());
            redirectAttributes.addFlashAttribute("messageType", "error");
        }
        
        System.out.println("Redirecting to /sceneryscore/findAll");
        return "redirect:/sceneryscore/findAll";
    }
    
    // ===== DIAGNOSTIC ENDPOINTS =====
    
    @GetMapping("/diagnostic")
    public String diagnostic(Model model) {
        try {
            System.out.println("=== DIAGNOSTIC CONTROLLER ===");
            
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            
            // Repository tests
            long repoCount = scoreRepository.count();
            List<SceneryScoreVO> repoScores = scoreRepository.findAll();
            
            // Service tests
            List<SceneryScoreVO> serviceScores = scoreService.findAll();
            String dbDiagnostic = scoreService.getDatabaseDiagnostic();
            
            // Add all diagnostic info to model
            model.addAttribute("currentUser", auth != null ? auth.getName() : "NOT AUTHENTICATED");
            model.addAttribute("userAuthorities", auth != null ? auth.getAuthorities().toString() : "NONE");
            model.addAttribute("repoCount", repoCount);
            model.addAttribute("serviceCount", serviceScores.size());
            model.addAttribute("scoreList", serviceScores);
            model.addAttribute("dbDiagnostic", dbDiagnostic);
            model.addAttribute("repositoryClass", scoreRepository.getClass().getSimpleName());
            model.addAttribute("serviceClass", scoreService.getClass().getSimpleName());
            
            // First score analysis
            if (!serviceScores.isEmpty()) {
                SceneryScoreVO firstScore = serviceScores.get(0);
                model.addAttribute("firstScore", firstScore);
                model.addAttribute("firstScoreMember", firstScore.getMember());
                model.addAttribute("firstScoreScenery", firstScore.getScenery());
                
                if (firstScore.getMember() != null) {
                    model.addAttribute("memberAccount", firstScore.getMember().getMemberAccount());
                    model.addAttribute("memberName", firstScore.getMember().getMemberName());
                }
                if (firstScore.getScenery() != null) {
                    model.addAttribute("sceneryName", firstScore.getScenery().getSceneryName());
                }
            }
            
            return "scenery/diagnostic";
            
        } catch (Exception e) {
            System.err.println("Diagnostic error: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            model.addAttribute("errorClass", e.getClass().getSimpleName());
            return "scenery/diagnostic";
        }
    }

    @GetMapping("/json")
    @ResponseBody
    public String getScoresAsJson() {
        try {
            List<SceneryScoreVO> scores = scoreRepository.findAll();
            StringBuilder json = new StringBuilder();
            json.append("{\n");
            json.append("  \"status\": \"success\",\n");
            json.append("  \"timestamp\": \"").append(java.time.LocalDateTime.now()).append("\",\n");
            json.append("  \"total\": ").append(scores.size()).append(",\n");
            json.append("  \"scores\": [\n");
            
            for (int i = 0; i < scores.size(); i++) {
                SceneryScoreVO score = scores.get(i);
                json.append("    {\n");
                json.append("      \"scoreId\": ").append(score.getScoreId()).append(",\n");
                json.append("      \"score\": ").append(score.getScore()).append(",\n");
                json.append("      \"comment\": \"").append(score.getSceneryComment() != null ? 
                    score.getSceneryComment().replace("\"", "\\\"").replace("\n", "\\n") : "").append("\",\n");
                json.append("      \"createTime\": \"").append(score.getCreateTime()).append("\",\n");
                
                json.append("      \"member\": ");
                if (score.getMember() != null) {
                    try {
                        json.append("{\n");
                        json.append("        \"memberId\": ").append(score.getMember().getMemberId()).append(",\n");
                        json.append("        \"memberAccount\": \"").append(
                            score.getMember().getMemberAccount() != null ? score.getMember().getMemberAccount() : "NULL").append("\",\n");
                        json.append("        \"memberName\": \"").append(
                            score.getMember().getMemberName() != null ? score.getMember().getMemberName() : "NULL").append("\"\n");
                        json.append("      }");
                    } catch (Exception e) {
                        json.append("\"ERROR: ").append(e.getMessage()).append("\"");
                    }
                } else {
                    json.append("null");
                }
                json.append(",\n");
                
                json.append("      \"scenery\": ");
                if (score.getScenery() != null) {
                    try {
                        json.append("{\n");
                        json.append("        \"sceneryId\": ").append(score.getScenery().getSceneryId()).append(",\n");
                        json.append("        \"sceneryName\": \"").append(
                            score.getScenery().getSceneryName() != null ? 
                            score.getScenery().getSceneryName().replace("\"", "\\\"") : "NULL").append("\"\n");
                        json.append("      }");
                    } catch (Exception e) {
                        json.append("\"ERROR: ").append(e.getMessage()).append("\"");
                    }
                } else {
                    json.append("null");
                }
                json.append("\n    }");
                
                if (i < scores.size() - 1) {
                    json.append(",");
                }
                json.append("\n");
            }
            
            json.append("  ]\n");
            json.append("}");
            
            return json.toString();
            
        } catch (Exception e) {
            return "{\"status\": \"error\", \"message\": \"" + e.getMessage() + "\", \"class\": \"" + e.getClass().getSimpleName() + "\"}";
        }
    }

    @GetMapping("/security-test")
    @ResponseBody
    public String securityTest() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return "SUCCESS: Security allows access! User: " + (auth != null ? auth.getName() : "Anonymous") + 
               ", Authorities: " + (auth != null ? auth.getAuthorities() : "None");
    }

    @GetMapping("/test")
    public String test(Model model) {
        System.out.println("=== TEST ENDPOINT ACCESSED ===");
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("testMessage", "Controller is working!");
        model.addAttribute("currentUser", auth != null ? auth.getName() : "Not authenticated");
        model.addAttribute("timestamp", java.time.LocalDateTime.now());
        
        return "scenery/test";
    }
}