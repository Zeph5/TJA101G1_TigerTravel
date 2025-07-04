package com.scenery.model;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tagsdb")
public class TagsdbVO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer tagsdbId;
	
	@Column(name = "tags_name",nullable = false, unique = true)
	private String tagsName;
	
	@Column(updatable = false)
	@CreationTimestamp
	private Timestamp createTime;

	public TagsdbVO() {
		super();
	}

	public TagsdbVO(Integer tagsdbId, String tagsName, Timestamp createTime) {
		super();
		this.tagsdbId = tagsdbId;
		this.tagsName = tagsName;
		this.createTime = createTime;
	}

	public Integer getTagsdbId() {
		return tagsdbId;
	}

	public void setTagsdbId(Integer tagsdbId) {
		this.tagsdbId = tagsdbId;
	}

	public String getTagsName() {
		return tagsName;
	}

	public void setTagsName(String tagsName) {
		this.tagsName = tagsName;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	
	
}
