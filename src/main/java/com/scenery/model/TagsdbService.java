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

	    // add Tag to tagsdb
	    public TagsdbVO add(TagsdbVO tagsdb) {
	    	if(tagsdbRepository.findByTagsName(tagsdb.getTagsName()).isPresent()) {
	    		throw new RuntimeException("標籤已存在");
	    	}
	        return tagsdbRepository.save(tagsdb);
	    }
	    
	    // find all 
	    public List<TagsdbVO> findAllTags() {
	        return tagsdbRepository.findAll();
	    }
		public List<TagsdbVO> findAllTags(Map<String, String[]> map) {
			return tagsdbRepository.findAll();
		}

	    // find by id
	    public Optional<TagsdbVO> findByTagsdbId(Integer id){
	    	return tagsdbRepository.findById(id);
	    }

	    // find by name
	    public Optional<TagsdbVO> findByTagsName(String tagsName) {
	        return tagsdbRepository.findByTagsName(tagsName);
	    }

	    
	    // update tag
	    public TagsdbVO save(TagsdbVO tagsdb) {
	    	return tagsdbRepository.save(tagsdb);
	    }
	    
	    // delete tag
	    public void deleteById(Integer id) {
	    	tagsdbRepository.deleteById(id);
	    }
}
