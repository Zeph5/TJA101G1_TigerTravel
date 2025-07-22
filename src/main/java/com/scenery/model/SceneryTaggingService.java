package com.scenery.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scenery.util.RegionTagUtil;

@Service
public class SceneryTaggingService {

    @Autowired
    private SceneryRepository sceneryRepository;

    @Autowired
    private TagsdbRepository tagsdbRepository;

    public void assignTagsToSceneries() {
        List<SceneryVO> sceneries = sceneryRepository.findAll();

        for (SceneryVO scenery : sceneries) {
            String region = RegionTagUtil.determineRegionTag(scenery.getSceneryAddress());
            if (region != null) {
                tagsdbRepository.findByTagsName(region).ifPresent(tag -> {
                    scenery.setTagsdbVO(tag); // assumes SceneryVO has this setter
                    sceneryRepository.save(scenery);
                });
            }
        }
    }
}
