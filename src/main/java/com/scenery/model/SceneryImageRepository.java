package com.scenery.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SceneryImageRepository extends JpaRepository<SceneryImageVO, Integer>{

	Optional<SceneryImageVO> findBySceneryId(Integer sceneryId);
}
