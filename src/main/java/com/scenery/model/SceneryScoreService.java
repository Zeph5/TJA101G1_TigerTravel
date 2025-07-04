package com.scenery.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("SceneryScoreService")
public class SceneryScoreService {

	 @Autowired
	    private SceneryScoreRepository sceneryScoreRepository;

	    // add score
	    public SceneryScoreVO add(SceneryScoreVO scescore) {
	    	if(sceneryScoreRepository.findByScoreId(scescore.getScoreId()).isPresent()) {
	    		throw new RuntimeException("景點已存在");
	    	}
	        return sceneryScoreRepository.save(scescore);
	    }
	    
	    // find all 
	    public List<SceneryScoreVO> findAllSceneryScore() {
	        return sceneryScoreRepository.findAll();
	    }
		public List<SceneryScoreVO> findAllSceneryScore(Map<String, String[]> map) {
			return sceneryScoreRepository.findAll();
		}

	    // find by id
	    public Optional<SceneryScoreVO> findByScoreId(Integer id){
	    	return sceneryScoreRepository.findById(id);
	    }

	    // update score
	    public SceneryScoreVO save(SceneryScoreVO scescore) {
	    	return sceneryScoreRepository.save(scescore);
	    }
	    
	    // delete score
	    public void deleteById(Integer id) {
	    	sceneryScoreRepository.deleteById(id);
	    }
}
