package com.scenery.model;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "scenery_image")
public class SceneryImageVO implements java.io.Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer sceneryImageId;
	
	@OneToMany
	@JoinColumn(name = "scenery_id")
	private String sceneryId;
	
	@Column(name = "sce_image")
	private Byte[] sceneryImage;
	
	@Column(updatable = false)
	@CreationTimestamp
	private Timestamp createTime;

	public SceneryImageVO() {
		super();
	}

	public SceneryImageVO(Integer sceneryImageId, String sceneryId, Byte[] sceneryImage, Timestamp createTime) {
		super();
		this.sceneryImageId = sceneryImageId;
		this.sceneryId = sceneryId;
		this.sceneryImage = sceneryImage;
		this.createTime = createTime;
	}

	public Integer getSceneryImageId() {
		return sceneryImageId;
	}

	public void setSceneryImageId(Integer sceneryImageId) {
		this.sceneryImageId = sceneryImageId;
	}

	public String getSceneryId() {
		return sceneryId;
	}

	public void setSceneryId(String sceneryId) {
		this.sceneryId = sceneryId;
	}

	public Byte[] getSceneryImage() {
		return sceneryImage;
	}

	public void setSceneryImage(Byte[] sceneryImage) {
		this.sceneryImage = sceneryImage;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	
	
}
