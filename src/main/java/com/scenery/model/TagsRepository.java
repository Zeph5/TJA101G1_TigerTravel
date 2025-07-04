package com.scenery.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagsRepository extends JpaRepository<TagsVO, Integer>{

	Optional<TagsVO> findBySceneryId(Integer sceneryId);
	
	Optional<TagsVO> findByTagsdbId(Integer tagsdbId);
}
