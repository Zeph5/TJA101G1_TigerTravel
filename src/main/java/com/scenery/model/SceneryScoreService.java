package com.scenery.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("SceneryScoreService")
public class SceneryScoreService {

	@Autowired
	    private SceneryScoreRepository scoreRepo;

	    public List<SceneryScoreVO> findAll() {
	        return scoreRepo.findAll();
	    }
	    
	    public List<SceneryScoreVO> searchScores(String memberAccount, String sceneryName) {
	        if ((memberAccount == null || memberAccount.isEmpty()) && (sceneryName == null || sceneryName.isEmpty())) {
	            return scoreRepo.findAll();
	        } else if (memberAccount != null && !memberAccount.isEmpty() && (sceneryName == null || sceneryName.isEmpty())) {
	            return scoreRepo.findByMember_MemberAccountContainingIgnoreCase(memberAccount);
	        } else if ((memberAccount == null || memberAccount.isEmpty()) && sceneryName != null && !sceneryName.isEmpty()) {
	            return scoreRepo.findByScenery_SceneryNameContainingIgnoreCase(sceneryName);
	        } else {
	            return scoreRepo.findByMember_MemberAccountContainingIgnoreCaseAndScenery_SceneryNameContainingIgnoreCase(memberAccount, sceneryName);
	        }
	    }
	    
	    public void deleteById(Integer scoreId) {
	        scoreRepo.deleteById(scoreId);
	    }
}
