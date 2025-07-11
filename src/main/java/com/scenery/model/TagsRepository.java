package com.scenery.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface TagsRepository extends JpaRepository<TagsVO, Integer>{

	Optional<TagsVO> findBySceneryAndTagsdb(SceneryVO scenery, TagsdbVO tagsdb);

    List<TagsVO> findByScenery(SceneryVO scenery);
    List<TagsVO> findByTagsdb(TagsdbVO tagsdb);
    
    List<TagsVO> findByTagsdb_TagsName(String tagsName);
    List<TagsVO> findByScenery_SceneryName(String sceneryName);
}
