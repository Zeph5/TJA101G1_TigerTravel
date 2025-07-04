package com.scenery.model;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;

@Entity
@Table(name = "tags")
public class TagsVO implements java.io.Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer tagsId;

	@ManyToOne
	@JoinColumn(name = "tagsdb_id", nullable = false)
	private TagsdbVO tagsdb;

	@ManyToOne
	@JoinColumn(name = "scenery_id", nullable = false)
	private SceneryVO scenery;

	@Column(updatable = false)
	@CreationTimestamp
	private Timestamp createTime;

	public TagsVO() {
		super();
	}

	public TagsVO(Integer tagsId, TagsdbVO tagsdb, SceneryVO scenery, Timestamp createTime) {
		super();
		this.tagsId = tagsId;
		this.tagsdb = tagsdb;
		this.scenery = scenery;
		this.createTime = createTime;
	}

	public Integer getTagsId() {
		return tagsId;
	}

	public void setTagsId(Integer tagsId) {
		this.tagsId = tagsId;
	}

	public TagsdbVO getTagsdb() {
		return tagsdb;
	}

	public void setTagsdb(TagsdbVO tagsdb) {
		this.tagsdb = tagsdb;
	}

	public SceneryVO getScenery() {
		return scenery;
	}

	public void setScenery(SceneryVO scenery) {
		this.scenery = scenery;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
}
