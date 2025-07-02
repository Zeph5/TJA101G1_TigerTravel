package com.scenery.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scenery.model.SceneryRepository;
import com.scenery.model.SceneryVO;

@Service("SceneryService")
public class SceneryService {

	
	  @Autowired
	    private SceneryRepository sceneryRepository;

	    // add scenery
	    public SceneryVO add(SceneryVO scenery) {
	    	if(sceneryRepository.findBySceneryName(scenery.getSceneryName()).isPresent()) {
	    		throw new RuntimeException("景點已存在");
	    	}
	        return sceneryRepository.save(scenery);
	    }
	    
	    // find all 
	    public List<SceneryVO> findAllScenery() {
	        return sceneryRepository.findAll();
	    }
		public List<SceneryVO> findAllScenery(Map<String, String[]> map) {
			return sceneryRepository.findAll();
		}

	    // find by id
	    public Optional<SceneryVO> findBySceneryId(Integer id){
	    	return sceneryRepository.findById(id);
	    }

	    // find by name
	    public Optional<SceneryVO> findBySceneryName(String sceneryName) {
	        return sceneryRepository.findBySceneryName(sceneryName);
	    }

	    
	    // update scenery
	    public SceneryVO save(SceneryVO scenery) {
	    	return sceneryRepository.save(scenery);
	    }
	    
	    // delete scenery
	    public void deleteById(Integer id) {
	    	sceneryRepository.deleteById(id);
	    }


}
