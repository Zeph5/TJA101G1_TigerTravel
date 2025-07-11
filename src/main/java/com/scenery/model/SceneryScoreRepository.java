package com.scenery.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SceneryScoreRepository extends JpaRepository<SceneryScoreVO, Integer>{
	
	List<SceneryScoreVO> findByMember_MemberAccountContainingIgnoreCase(String memberAccount);

    List<SceneryScoreVO> findByScenery_SceneryNameContainingIgnoreCase(String sceneryName);

    List<SceneryScoreVO> findByMember_MemberAccountContainingIgnoreCaseAndScenery_SceneryNameContainingIgnoreCase(String memberAccount, String sceneryName);

	Optional<SceneryScoreVO> findByScoreId(Integer scoreId);
}
