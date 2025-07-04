package com.scenery.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("SceneryImageService")
public class SceneryImageService {

	 @Autowired
	    private SceneryImageRepository sceneryImageRepository;

	    // add scenery
	    public SceneryImageVO add(SceneryImageVO sceneryImage) {
	    	return sceneryImageRepository.save(sceneryImage);
	    }
	    
	    // find all 
	    public List<SceneryImageVO> findAllSceneryImage() {
	        return sceneryImageRepository.findAll();
	    }
		public List<SceneryImageVO> findAllSceneryImage(Map<String, String[]> map) {
			return sceneryImageRepository.findAll();
		}

	    // find by id
	    public Optional<SceneryImageVO> findBySceneryImageId(Integer id){
	    	return sceneryImageRepository.findById(id);
	    }
	    
	    // update scenery image
	    public SceneryImageVO save(SceneryImageVO sceneryImage) {
	    	return sceneryImageRepository.save(sceneryImage);
	    }
	    
	    // delete scenery image
	    public void deleteById(Integer id) {
	    	sceneryImageRepository.deleteById(id);
	    }
}
