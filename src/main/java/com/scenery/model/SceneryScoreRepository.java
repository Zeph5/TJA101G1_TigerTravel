package com.scenery.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SceneryScoreRepository extends JpaRepository<SceneryScoreVO, Integer>{

	Optional<SceneryScoreVO> findByScoreId(Integer scoreId);
}
