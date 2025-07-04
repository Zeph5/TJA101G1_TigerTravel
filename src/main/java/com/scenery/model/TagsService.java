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

	    // add tag to scenery
	    public TagsVO add(TagsVO tags) {
	    	if(tagsRepository.findBySceneryId(tags.getSceneryId()).isPresent() && tagsRepository.findByTagsdbId(tags.getTagsdbId()).isPresent()) {
	    		throw new RuntimeException("該景點已有該標籤");
	    	}
	        return tagsRepository.save(tags);
	    }
	    
	    // find all 
	    public List<TagsVO> findAllTags() {
	        return tagsRepository.findAll();
	    }
		public List<TagsVO> findAllTags(Map<String, String[]> map) {
			return tagsRepository.findAll();
		}

	    // find by scenery id
	    public Optional<TagsVO> findBySceneryId(Integer id){
	    	return tagsRepository.findById(id);
	    }

	    // find by tagsdb id
	    public Optional<TagsVO> findByTagsdbId(Integer id) {
	        return tagsRepository.findByTagsdbId(id);
	    }

	    
	    // update scenery
	    public TagsVO save(TagsVO tags) {
	    	return tagsRepository.save(tags);
	    }
	    
	    // delete scenery
	    public void deleteById(Integer id) {
	    	tagsRepository.deleteById(id);
	    }
}
