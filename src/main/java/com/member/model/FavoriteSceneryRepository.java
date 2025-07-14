package com.member.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteSceneryRepository extends JpaRepository<FavoriteSceneryVO, Integer>{
	List<FavoriteSceneryVO> findByMember(memVO member);
}
