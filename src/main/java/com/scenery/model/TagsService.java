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

    // Add tag to scenery
    public TagsVO add(TagsVO tags) {
        Optional<TagsVO> existingTag = tagsRepository.findBySceneryAndTagsdb(tags.getScenery(), tags.getTagsdb());
        if (existingTag.isPresent()) {
            throw new RuntimeException("該景點已有該標籤");
        }
        return tagsRepository.save(tags);
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
}
