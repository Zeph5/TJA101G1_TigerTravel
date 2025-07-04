package com.scenery.model;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tags")
public class TagsVO implements java.io.Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer tagsId;
	
	@ManyToOne
	@JoinColumn(name = "tagsdb_id")
	private Integer tagsdbId;
	
	@OneToMany
	@JoinColumn(name = "scenery_id")
	private Integer sceneryId;
	
	
	@Column(updatable = false)
	@CreationTimestamp
	private Timestamp createTime;


	public TagsVO() {
		super();
	}


	public TagsVO(Integer tagsId, Integer tagsdbId, Integer sceneryId, Timestamp createTime) {
		super();
		this.tagsId = tagsId;
		this.tagsdbId = tagsdbId;
		this.sceneryId = sceneryId;
		this.createTime = createTime;
	}


	public Integer getTagsId() {
		return tagsId;
	}


	public void setTagsId(Integer tagsId) {
		this.tagsId = tagsId;
	}


	public Integer getTagsdbId() {
		return tagsdbId;
	}


	public void setTagsdbId(Integer tagsdbId) {
		this.tagsdbId = tagsdbId;
	}


	public Integer getSceneryId() {
		return sceneryId;
	}


	public void setSceneryId(Integer sceneryId) {
		this.sceneryId = sceneryId;
	}


	public Timestamp getCreateTime() {
		return createTime;
	}


	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	
	
}
