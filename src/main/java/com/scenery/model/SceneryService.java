package com.scenery.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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

}
