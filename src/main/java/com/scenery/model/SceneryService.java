package com.scenery.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.scenery.model.DTO.SceneryDTO;
import org.springframework.data.domain.PageImpl;

@Service("SceneryService")
public class SceneryService {

    @Autowired
    private SceneryRepository sceneryRepository;

    @Autowired
    private SceneryImageRepository sceneryImageRepository;

    @Autowired
    private TagsRepository tagsRepository;

    public List<SceneryVO> getAllSceneries() {
        return sceneryRepository.findAll();
    }

    public SceneryVO getById(Integer id) {
        return sceneryRepository.findById(id).orElse(null);
    }

    public Page<SceneryVO> advancedSearch(String sceneryName, String sceneryAddress, Integer sceneryStatusFilter, Pageable pageable) {
        if ((sceneryName == null || sceneryName.trim().isEmpty()) &&
            (sceneryAddress == null || sceneryAddress.trim().isEmpty()) &&
            sceneryStatusFilter == null) {
            return sceneryRepository.findAll(pageable);
        } else {
            return sceneryRepository.advancedSearch(sceneryName, sceneryAddress, sceneryStatusFilter, pageable);
        }
    }

    public void addScenery(SceneryVO vo) {
        sceneryRepository.save(vo);
    }

    public void updateScenery(SceneryVO vo) {
        sceneryRepository.save(vo);
    }

    public void updateSceneryStatus(Integer sceneryId, Integer status) {
        SceneryVO scenery = sceneryRepository.findById(sceneryId).orElseThrow(() -> new RuntimeException("Scenery not found"));
        scenery.setSceneryStatus(status);
        sceneryRepository.save(scenery);
    }

    public void addSceneryImage(Integer sceneryId, MultipartFile file) throws IOException {
        SceneryVO scenery = getById(sceneryId);
        if (scenery == null) {
            throw new RuntimeException("Scenery not found for ID: " + sceneryId);
        }

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty or missing");
        }

        SceneryImageVO imageVO = new SceneryImageVO();
        imageVO.setScenery(scenery);

        byte[] bytes = file.getBytes();
        Byte[] bytesObj = new Byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            bytesObj[i] = bytes[i];
        }
        imageVO.setSceneryImage(bytesObj);

        sceneryImageRepository.save(imageVO);
    }

    public List<SceneryImageVO> getImagesBySceneryId(Integer sceneryId) {
        return sceneryImageRepository.findByScenery_SceneryId(sceneryId);
    }

    public SceneryDTO convertToDTO(SceneryVO vo) {
        SceneryDTO dto = new SceneryDTO();
        dto.setSceneryId(vo.getSceneryId());
        dto.setSceneryName(vo.getSceneryName());
        dto.setSceneryIntro(vo.getSceneryIntro());
        dto.setSceneryAddress(vo.getSceneryAddress());
        dto.setSceneryLongitude(vo.getSceneryLongitude());
        dto.setSceneryLatitude(vo.getSceneryLatitude());
        dto.setExistingImages(new ArrayList<>(vo.getSceneryImages()));
        return dto;
    }

    public Page<SceneryVO> searchSceneryByNameOrTag(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return sceneryRepository.findBySceneryStatus(1, pageable);
        }

        // Search by name
        List<SceneryVO> byName = sceneryRepository.findBySceneryNameContainingAndSceneryStatus(keyword, 1);

        // Search by tag
        List<Integer> sceneryIdsByTag = tagsRepository.findSceneryIdsByTagName("%" + keyword + "%");
        List<SceneryVO> byTag = sceneryRepository.findAllById(sceneryIdsByTag).stream()
                .filter(s -> s.getSceneryStatus() == 1)
                .toList();

        // Combine and deduplicate
        List<SceneryVO> combined = new ArrayList<>(byName);
        for (SceneryVO vo : byTag) {
            if (!combined.contains(vo)) {
                combined.add(vo);
            }
        }

        // Safely handle pagination boundaries
        int start = Math.min((int) pageable.getOffset(), combined.size());
        int end = Math.min(start + pageable.getPageSize(), combined.size());
        List<SceneryVO> pageContent = combined.subList(start, end);

        return new PageImpl<>(pageContent, pageable, combined.size());
    }

    public List<SceneryVO> getAllAvailableSceneries() {
        return sceneryRepository.findBySceneryStatus(1);
    }

    // ===== NEW: CACHED RATING METHODS =====

    /**
     * Updates the cached rating for a scenery when a new score is added/updated
     * Call this method whenever a SceneryScoreVO is added, updated, or deleted
     */
    @Transactional
    public void updateSceneryCachedRating(Integer sceneryId) {
        try {
            // Get the scenery with all its scores
            SceneryVO scenery = sceneryRepository.findById(sceneryId).orElse(null);
            if (scenery == null) {
                System.err.println("Scenery not found for ID: " + sceneryId);
                return;
            }
            
            // Calculate new totals from all scores
            List<SceneryScoreVO> scores = scenery.getSceneryScores();
            if (scores == null || scores.isEmpty()) {
                // No scores - set to 0
                sceneryRepository.updateCachedRating(sceneryId, 0, 0);
                System.out.println("Reset cached rating for " + scenery.getSceneryName() + " (no scores)");
                return;
            }
            
            // Calculate totals
            int totalScore = scores.stream()
                    .filter(score -> score.getScore() != null)
                    .mapToInt(SceneryScoreVO::getScore)
                    .sum();
            int totalCount = (int) scores.stream()
                    .filter(score -> score.getScore() != null)
                    .count();
            
            // Update cached values
            sceneryRepository.updateCachedRating(sceneryId, totalScore, totalCount);
            
            double avgRating = totalCount > 0 ? (double) totalScore / totalCount : 0.0;
            System.out.println("Updated cached rating for " + scenery.getSceneryName() + 
                              ": " + totalScore + "/" + totalCount + 
                              " = " + String.format("%.1f", avgRating));
                              
        } catch (Exception e) {
            System.err.println("Error updating cached rating for scenery " + sceneryId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Bulk update all scenery cached ratings (useful for initial setup or maintenance)
     */
    @Transactional
    public void updateAllSceneryCachedRatings() {
        try {
            List<SceneryVO> allSceneries = sceneryRepository.findAll();
            System.out.println("Starting bulk update of cached ratings for " + allSceneries.size() + " sceneries...");
            
            int updated = 0;
            for (SceneryVO scenery : allSceneries) {
                updateSceneryCachedRating(scenery.getSceneryId());
                updated++;
                
                // Progress indicator for large datasets
                if (updated % 10 == 0) {
                    System.out.println("Progress: " + updated + "/" + allSceneries.size() + " sceneries updated");
                }
            }
            
            System.out.println("Completed bulk update of cached ratings for " + updated + " sceneries");
        } catch (Exception e) {
            System.err.println("Error during bulk rating cache update: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get top rated sceneries using cached values (fast query for homepage)
     */
    public List<SceneryVO> getTopRatedSceneries(int limit) {
        try {
            org.springframework.data.domain.Pageable pageable = 
                org.springframework.data.domain.PageRequest.of(0, limit);
            
            // Use the cached rating query - this should be fast
            List<SceneryVO> topSceneries = sceneryRepository.findTop4ByHighestRating(pageable);
            
            // Don't load unnecessary relationships for homepage
            return topSceneries;
            
        } catch (Exception e) {
            System.err.println("Error getting top rated sceneries: " + e.getMessage());
            // Fallback to any active sceneries - but limit the query
            try {
                org.springframework.data.domain.Pageable fallbackPageable = 
                    org.springframework.data.domain.PageRequest.of(0, limit);
                return sceneryRepository.findBySceneryStatus(1, fallbackPageable).getContent();
            } catch (Exception fallbackError) {
                System.err.println("Fallback query also failed: " + fallbackError.getMessage());
                return new ArrayList<>(); // Return empty list to prevent homepage crash
            }
        }
    }
    
    public Page<SceneryVO> searchSceneryByAddress(String address, Pageable pageable) {
        try {
            System.out.println("Searching sceneries by address: " + address);
            
            // Use the corrected repository method that matches your field naming
            Page<SceneryVO> result = sceneryRepository.findBySceneryAddressContainingIgnoreCaseAndSceneryStatus(
                address, 1, pageable);
            
            System.out.println("Found " + result.getTotalElements() + " sceneries matching address: " + address);
            return result;
            
        } catch (Exception e) {
            System.err.println("Error searching sceneries by address: " + e.getMessage());
            e.printStackTrace();
            // Return empty page on error
            return Page.empty(pageable);
        }
    }
}