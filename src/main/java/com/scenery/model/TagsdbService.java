package com.scenery.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("TagsdbService")
public class TagsdbService {

    @Autowired
    private TagsdbRepository tagsdbRepository;

    public List<TagsdbVO> findAll() {
        return tagsdbRepository.findAll();
    }

    public Optional<TagsdbVO> findById(Integer id) {
        return tagsdbRepository.findById(id);
    }

    public List<TagsdbVO> findByTagsNameContaining(String name) {
        return tagsdbRepository.findByTagsNameContainingIgnoreCase(name);
    }

    public TagsdbVO save(TagsdbVO vo) {
        // Check for duplicate on add or update
        Optional<TagsdbVO> existing = tagsdbRepository.findByTagsName(vo.getTagsName());

        if (existing.isPresent()) {
            // If adding new (no ID yet) or updating different ID → throw exception
            if (vo.getTagsdbId() == null || !existing.get().getTagsdbId().equals(vo.getTagsdbId())) {
                throw new IllegalArgumentException("Tag name '" + vo.getTagsName() + "' already exists.");
            }
        }

        return tagsdbRepository.save(vo);
    }

    public void deleteById(Integer id) {
        tagsdbRepository.deleteById(id);
    }
}
