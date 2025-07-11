package com.scenery.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SceneryRepository extends JpaRepository<SceneryVO, Integer> {
	
	Optional<SceneryVO> findBySceneryName(String sceneryName);

}