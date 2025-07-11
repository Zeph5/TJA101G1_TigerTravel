package com.scenery.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("TagsService")
public class TagsService {

    @Autowired
    private TagsRepository tagsRepository;
    
    @Autowired
    private TagsdbRepository tagsdbRepository;

    @Autowired
    private SceneryRepository sceneryRepository;

    // Add tag to scenery
    public TagsVO addTag(Integer tagsdbId, Integer sceneryId) {
        Optional<TagsdbVO> tagsdbOpt = tagsdbRepository.findById(tagsdbId);
        Optional<SceneryVO> sceneryOpt = sceneryRepository.findById(sceneryId);

        if (tagsdbOpt.isEmpty() || sceneryOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid tagsdbId or sceneryId");
        }

        TagsVO tag = new TagsVO();
        tag.setTagsdb(tagsdbOpt.get());
        tag.setScenery(sceneryOpt.get());
        // createTime will be auto-set by @CreationTimestamp

        return tagsRepository.save(tag);
    }

    public List<TagsVO> findAllTags() {
        return tagsRepository.findAll();
    }

    public List<TagsVO> findAllTags(Map<String, String[]> map) {
        return tagsRepository.findAll(); // You can add filtering later
    }

    public List<TagsVO> findByScenery(SceneryVO scenery) {
        return tagsRepository.findByScenery(scenery);
    } 

    public List<TagsVO> findByTagsdb(TagsdbVO tagsdb) {
        return tagsRepository.findByTagsdb(tagsdb);
    }

    public TagsVO save(TagsVO tags) {
        return tagsRepository.save(tags);
    }

    public void deleteById(Integer id) {
        tagsRepository.deleteById(id);
    }
    
    public Optional<TagsVO> findById(Integer id) {
        return tagsRepository.findById(id);
    }
    
    public void updateTag(Integer tagsId, Integer tagsdbId, Integer sceneryId) {
        TagsVO tag = tagsRepository.findById(tagsId)
            .orElseThrow(() -> new IllegalArgumentException("Tag not found"));

        TagsdbVO tagdb = tagsdbRepository.findById(tagsdbId)
            .orElseThrow(() -> new IllegalArgumentException("Tag name not found"));

        SceneryVO scenery = sceneryRepository.findById(sceneryId)
            .orElseThrow(() -> new IllegalArgumentException("Scenery not found"));

        tag.setTagsdb(tagdb);
        tag.setScenery(scenery);

        tagsRepository.save(tag);
    }
}
