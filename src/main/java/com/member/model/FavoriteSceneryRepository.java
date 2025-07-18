package com.member.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteSceneryRepository extends JpaRepository<FavoriteSceneryVO, Integer>{
	List<FavoriteSceneryVO> findByMember(memVO member);

    boolean existsByMember_MemberIdAndScenery_SceneryId(Integer memberId, Integer sceneryId);
    
    void deleteByMember_MemberIdAndScenery_SceneryId(Integer memberId, Integer sceneryId);
    
    List<FavoriteSceneryVO> findByMember_MemberId(Integer memberId);
}

