package com.scenery.model;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "scenery_image")
public class SceneryImageVO implements java.io.Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer sceneryImageId;

	@ManyToOne
	@JoinColumn(name = "scenery_id", nullable = false)
	private SceneryVO scenery;

	@Lob
	@Basic(fetch = FetchType.EAGER)
	private Byte[] sceneryImage;

	@Column(updatable = false)
	@CreationTimestamp
	private Timestamp createTime;

	public SceneryImageVO() {
		super();
	}

	public SceneryImageVO(Integer sceneryImageId, SceneryVO scenery, Byte[] sceneryImage, Timestamp createTime) {
		super();
		this.sceneryImageId = sceneryImageId;
		this.scenery = scenery;
		this.sceneryImage = sceneryImage;
		this.createTime = createTime;
	}

	public Integer getSceneryImageId() {
		return sceneryImageId;
	}

	public void setSceneryImageId(Integer sceneryImageId) {
		this.sceneryImageId = sceneryImageId;
	}

	public SceneryVO getScenery() {
		return scenery;
	}

	public void setScenery(SceneryVO scenery) {
		this.scenery = scenery;
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
