package com.scenery.model;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SceneryScoreService {

    @Autowired
    private SceneryScoreRepository scoreRepository;

    public List<SceneryScoreVO> findAll() {
        try {
            System.out.println("=== SceneryScoreService.findAll() START ===");
            
            // First, check if repository is injected
            if (scoreRepository == null) {
                System.err.println("ERROR: SceneryScoreRepository is NULL!");
                throw new RuntimeException("Repository not injected properly");
            }
            
            System.out.println("Repository found: " + scoreRepository.getClass().getSimpleName());
            
            // Get all scores
            List<SceneryScoreVO> scores = scoreRepository.findAll();
            System.out.println("Raw query returned " + scores.size() + " scores");
            
            if (scores.isEmpty()) {
                System.out.println("WARNING: No scores found in database!");
                // Let's check if tables exist and have data
                long count = scoreRepository.count();
                System.out.println("Repository count() returns: " + count);
                return scores; // Return empty list
            }
            
            // Debug each score in detail
            System.out.println("=== DETAILED SCORE ANALYSIS ===");
            for (int i = 0; i < Math.min(5, scores.size()); i++) {
                SceneryScoreVO score = scores.get(i);
                System.out.println("--- Score " + (i + 1) + " ---");
                System.out.println("  Score ID: " + score.getScoreId());
                System.out.println("  Score Value: " + score.getScore());
                System.out.println("  Comment: " + (score.getSceneryComment() != null ? 
                    score.getSceneryComment().substring(0, Math.min(50, score.getSceneryComment().length())) + "..." 
                    : "NULL"));
                System.out.println("  Create Time: " + score.getCreateTime());
                
                // Test member relationship
                try {
                    if (score.getMember() != null) {
                        System.out.println("  Member Object: EXISTS");
                        System.out.println("  Member ID: " + score.getMember().getMemberId());
                        System.out.println("  Member Account: " + score.getMember().getMemberAccount());
                        System.out.println("  Member Name: " + score.getMember().getMemberName());
                    } else {
                        System.err.println("  Member Object: NULL!");
                    }
                } catch (Exception e) {
                    System.err.println("  Error accessing member: " + e.getMessage());
                    e.printStackTrace();
                }
                
                // Test scenery relationship
                try {
                    if (score.getScenery() != null) {
                        System.out.println("  Scenery Object: EXISTS");
                        System.out.println("  Scenery ID: " + score.getScenery().getSceneryId());
                        System.out.println("  Scenery Name: " + score.getScenery().getSceneryName());
                    } else {
                        System.err.println("  Scenery Object: NULL!");
                    }
                } catch (Exception e) {
                    System.err.println("  Error accessing scenery: " + e.getMessage());
                    e.printStackTrace();
                }
                System.out.println("  ==================");
            }
            
            System.out.println("=== SceneryScoreService.findAll() SUCCESS ===");
            System.out.println("Returning " + scores.size() + " scores to controller");
            return scores;
            
        } catch (Exception e) {
            System.err.println("=== CRITICAL ERROR in SceneryScoreService.findAll() ===");
            System.err.println("Error Type: " + e.getClass().getSimpleName());
            System.err.println("Error Message: " + e.getMessage());
            System.err.println("Stack Trace:");
            e.printStackTrace();
            System.err.println("=== END CRITICAL ERROR ===");
            
            // Return empty list instead of throwing exception to prevent page crash
            return List.of();
        }
    }

    public List<SceneryScoreVO> searchScores(String memberAccount, String sceneryName) {
        try {
            System.out.println("=== SceneryScoreService.searchScores() START ===");
            System.out.println("Raw params - memberAccount: '" + memberAccount + "', sceneryName: '" + sceneryName + "'");
            
            // Clean and validate parameters
            String cleanMemberAccount = (memberAccount != null && !memberAccount.trim().isEmpty()) 
                ? memberAccount.trim() : null;
            String cleanSceneryName = (sceneryName != null && !sceneryName.trim().isEmpty()) 
                ? sceneryName.trim() : null;
                
            System.out.println("Clean params - memberAccount: '" + cleanMemberAccount + "', sceneryName: '" + cleanSceneryName + "'");
            
            List<SceneryScoreVO> result;
            
            if (cleanMemberAccount != null && cleanSceneryName != null) {
                System.out.println("Searching by BOTH member account AND scenery name");
                result = scoreRepository.findByMember_MemberAccountContainingIgnoreCaseAndScenery_SceneryNameContainingIgnoreCase(
                    cleanMemberAccount, cleanSceneryName);
            } else if (cleanMemberAccount != null) {
                System.out.println("Searching by MEMBER ACCOUNT only: " + cleanMemberAccount);
                result = scoreRepository.findByMember_MemberAccountContainingIgnoreCase(cleanMemberAccount);
            } else if (cleanSceneryName != null) {
                System.out.println("Searching by SCENERY NAME only: " + cleanSceneryName);
                result = scoreRepository.findByScenery_SceneryNameContainingIgnoreCase(cleanSceneryName);
            } else {
                System.out.println("No valid search parameters, returning ALL scores");
                result = findAll();
            }
            
            System.out.println("Search query returned " + result.size() + " results");
            
            // Force load relationships for search results
            for (SceneryScoreVO score : result) {
                try {
                    if (score.getMember() != null) {
                        score.getMember().getMemberAccount(); // Force lazy load
                    }
                    if (score.getScenery() != null) {
                        score.getScenery().getSceneryName(); // Force lazy load
                    }
                } catch (Exception e) {
                    System.err.println("Error loading relationships for search result: " + e.getMessage());
                }
            }
            
            System.out.println("=== SceneryScoreService.searchScores() END ===");
            return result;
            
        } catch (Exception e) {
            System.err.println("=== ERROR in SceneryScoreService.searchScores() ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.err.println("=== END SEARCH ERROR ===");
            
            // Return empty list on error
            return List.of();
        }
    }

    @Transactional
    public void deleteById(Integer id) {
        try {
            System.out.println("=== SceneryScoreService.deleteById(" + id + ") START ===");
            
            if (id == null) {
                throw new IllegalArgumentException("Score ID cannot be null");
            }
            
            // Check if score exists
            Optional<SceneryScoreVO> scoreOpt = scoreRepository.findById(id);
            if (scoreOpt.isPresent()) {
                SceneryScoreVO score = scoreOpt.get();
                System.out.println("Found score to delete:");
                System.out.println("  - ID: " + score.getScoreId());
                System.out.println("  - Score: " + score.getScore());
                System.out.println("  - Member: " + (score.getMember() != null ? score.getMember().getMemberAccount() : "NULL"));
                System.out.println("  - Scenery: " + (score.getScenery() != null ? score.getScenery().getSceneryName() : "NULL"));
                
                scoreRepository.deleteById(id);
                System.out.println("Successfully deleted score with ID: " + id);
            } else {
                System.err.println("Score with ID " + id + " not found for deletion");
                throw new RuntimeException("Score not found with ID: " + id);
            }
            
            System.out.println("=== SceneryScoreService.deleteById() END ===");
            
        } catch (Exception e) {
            System.err.println("=== ERROR in SceneryScoreService.deleteById() ===");
            System.err.println("Error deleting score ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            System.err.println("=== END DELETE ERROR ===");
            throw new RuntimeException("Error deleting score: " + e.getMessage(), e);
        }
    }

    public Optional<SceneryScoreVO> findById(Integer id) {
        try {
            Optional<SceneryScoreVO> scoreOpt = scoreRepository.findById(id);
            if (scoreOpt.isPresent()) {
                SceneryScoreVO score = scoreOpt.get();
                // Force load relationships
                try {
                    if (score.getMember() != null) {
                        score.getMember().getMemberAccount();
                    }
                    if (score.getScenery() != null) {
                        score.getScenery().getSceneryName();
                    }
                } catch (Exception e) {
                    System.err.println("Error loading relationships for score ID " + id + ": " + e.getMessage());
                }
            }
            return scoreOpt;
        } catch (Exception e) {
            System.err.println("Error finding score by ID " + id + ": " + e.getMessage());
            throw new RuntimeException("Error finding score: " + e.getMessage(), e);
        }
    }

    @Transactional
    public SceneryScoreVO save(SceneryScoreVO score) {
        try {
            System.out.println("Saving score: " + score.getScoreId());
            return scoreRepository.save(score);
        } catch (Exception e) {
            System.err.println("Error saving score: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error saving score: " + e.getMessage(), e);
        }
    }

    // Additional utility methods
    public Double getAverageRatingBySceneryId(Integer sceneryId) {
        try {
            return scoreRepository.getAverageRatingBySceneryId(sceneryId);
        } catch (Exception e) {
            System.err.println("Error getting average rating for scenery " + sceneryId + ": " + e.getMessage());
            return 0.0;
        }
    }

    public boolean hasUserRatedScenery(Integer memberId, Integer sceneryId) {
        try {
            return scoreRepository.existsByMemberAndScenery(memberId, sceneryId);
        } catch (Exception e) {
            System.err.println("Error checking if user rated scenery: " + e.getMessage());
            return false;
        }
    }

    public List<SceneryScoreVO> findBySceneryId(Integer sceneryId) {
        try {
            List<SceneryScoreVO> scores = scoreRepository.findByScenery_SceneryIdOrderByCreateTimeDesc(sceneryId);
            // Force load relationships
            for (SceneryScoreVO score : scores) {
                try {
                    if (score.getMember() != null) score.getMember().getMemberAccount();
                    if (score.getScenery() != null) score.getScenery().getSceneryName();
                } catch (Exception e) {
                    System.err.println("Error loading relationship: " + e.getMessage());
                }
            }
            return scores;
        } catch (Exception e) {
            System.err.println("Error finding scores by scenery ID: " + e.getMessage());
            return List.of();
        }
    }

    public List<SceneryScoreVO> findByMemberId(Integer memberId) {
        try {
            List<SceneryScoreVO> scores = scoreRepository.findByMember_MemberId(memberId);
            // Force load relationships
            for (SceneryScoreVO score : scores) {
                try {
                    if (score.getMember() != null) score.getMember().getMemberAccount();
                    if (score.getScenery() != null) score.getScenery().getSceneryName();
                } catch (Exception e) {
                    System.err.println("Error loading relationship: " + e.getMessage());
                }
            }
            return scores;
        } catch (Exception e) {
            System.err.println("Error finding scores by member ID: " + e.getMessage());
            return List.of();
        }
    }

    // Database diagnostic method
    public String getDatabaseDiagnostic() {
        try {
            StringBuilder diag = new StringBuilder();
            
            // Count total scores
            long totalCount = scoreRepository.count();
            diag.append("Total scores in database: ").append(totalCount).append("\n");
            
            if (totalCount > 0) {
                // Get first few scores
                List<SceneryScoreVO> sample = scoreRepository.findAll().stream().limit(3).toList();
                diag.append("Sample scores:\n");
                for (SceneryScoreVO score : sample) {
                    diag.append("  - ID: ").append(score.getScoreId())
                        .append(", Score: ").append(score.getScore())
                        .append(", Member: ").append(score.getMember() != null ? score.getMember().getMemberAccount() : "NULL")
                        .append(", Scenery: ").append(score.getScenery() != null ? score.getScenery().getSceneryName() : "NULL")
                        .append("\n");
                }
            }
            
            return diag.toString();
        } catch (Exception e) {
            return "Error getting diagnostic: " + e.getMessage();
        }
    }
}