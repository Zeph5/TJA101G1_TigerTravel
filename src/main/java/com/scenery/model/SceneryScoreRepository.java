package com.scenery.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SceneryScoreRepository extends JpaRepository<SceneryScoreVO, Integer>{
	
	List<SceneryScoreVO> findByMember_MemberAccountContainingIgnoreCase(String memberAccount);

    List<SceneryScoreVO> findByScenery_SceneryNameContainingIgnoreCase(String sceneryName);

    List<SceneryScoreVO> findByMember_MemberAccountContainingIgnoreCaseAndScenery_SceneryNameContainingIgnoreCase(String memberAccount, String sceneryName);

	Optional<SceneryScoreVO> findByScoreId(Integer scoreId);
	
	// Find all scores for a specific scenery
    List<SceneryScoreVO> findByScenery_SceneryIdOrderByCreateTimeDesc(Integer sceneryId);
    
    // Find all scores by a specific member
    List<SceneryScoreVO> findByMember_MemberId(Integer memberId);
    
    // Check if a member has already rated a scenery
    @Query("SELECT COUNT(s) > 0 FROM SceneryScoreVO s WHERE s.member.memberId = :memberId AND s.scenery.sceneryId = :sceneryId")
    boolean existsByMemberAndScenery(@Param("memberId") Integer memberId, @Param("sceneryId") Integer sceneryId);
    
    // Get average rating for a scenery
    @Query("SELECT AVG(CAST(s.score AS double)) FROM SceneryScoreVO s WHERE s.scenery.sceneryId = :sceneryId")
    Double getAverageRatingBySceneryId(@Param("sceneryId") Integer sceneryId);
}
