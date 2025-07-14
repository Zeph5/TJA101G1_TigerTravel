package com.scenery.model;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List; // 如果您希望返回多個圖片

public interface SceneryImageRepository extends JpaRepository<SceneryImageVO, Integer> {

	List<SceneryImageVO> findByScenery_SceneryId(Integer sceneryId);
	
	

}