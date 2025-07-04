package com.scenery.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagsdbRepository extends JpaRepository<TagsdbVO, Integer>{

	Optional<TagsdbVO> findByTagsName(String tagsName);
}
