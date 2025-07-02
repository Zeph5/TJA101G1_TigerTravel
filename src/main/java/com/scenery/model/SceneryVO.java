package com.scenery.model;

import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;


@Entity
@Table(name = "scenery")
public class SceneryVO implements java.io.Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer sceneryId;
	
	@Column(name = "sce_name",nullable = false, unique = true)
	private String sceneryName;
	
	@Column(name = "sce_intro")
	private String sceneryIntro;
	
	@Column(name = "sce_total_score")
	private Integer sceneryTotalScore;
	
	@Column(name = "score_sce_total_score")
	private Integer sceneryTotalScoreCount;
	
	@Column(name = "sce_address")
	private String sceneryAddress;
	
	@Column(name = "sce_longitude")
	private Double sceneryLongitude;
	
	@Column(name = "sce_latitude")
	private Double sceneryLatitude;
	
	@Column(updatable = false)
	@CreationTimestamp
	private Timestamp createTime;

	public SceneryVO() {
		super();
	}

	public SceneryVO(Integer sceneryId, String sceneryName, String sceneryIntro, Integer sceneryTotalScore,
			Integer sceneryTotalScoreCount, String sceneryAddress, Double sceneryLongitude, Double sceneryLatitude,
			Timestamp createTime) {
		super();
		this.sceneryId = sceneryId;
		this.sceneryName = sceneryName;
		this.sceneryIntro = sceneryIntro;
		this.sceneryTotalScore = sceneryTotalScore;
		this.sceneryTotalScoreCount = sceneryTotalScoreCount;
		this.sceneryAddress = sceneryAddress;
		this.sceneryLongitude = sceneryLongitude;
		this.sceneryLatitude = sceneryLatitude;
		this.createTime = createTime;
	}

	public Integer getSceneryId() {
		return sceneryId;
	}

	public void setSceneryId(Integer sceneryId) {
		this.sceneryId = sceneryId;
	}

	public String getSceneryName() {
		return sceneryName;
	}

	public void setSceneryName(String sceneryName) {
		this.sceneryName = sceneryName;
	}

	public String getSceneryIntro() {
		return sceneryIntro;
	}

	public void setSceneryIntro(String sceneryIntro) {
		this.sceneryIntro = sceneryIntro;
	}

	public Integer getSceneryTotalScore() {
		return sceneryTotalScore;
	}

	public void setSceneryTotalScore(Integer sceneryTotalScore) {
		this.sceneryTotalScore = sceneryTotalScore;
	}

	public Integer getSceneryTotalScoreCount() {
		return sceneryTotalScoreCount;
	}

	public void setSceneryTotalScoreCount(Integer sceneryTotalScoreCount) {
		this.sceneryTotalScoreCount = sceneryTotalScoreCount;
	}

	public String getSceneryAddress() {
		return sceneryAddress;
	}

	public void setSceneryAddress(String sceneryAddress) {
		this.sceneryAddress = sceneryAddress;
	}

	public Double getSceneryLongitude() {
		return sceneryLongitude;
	}

	public void setSceneryLongitude(Double sceneryLongitude) {
		this.sceneryLongitude = sceneryLongitude;
	}

	public Double getSceneryLatitude() {
		return sceneryLatitude;
	}

	public void setSceneryLatitude(Double sceneryLatitude) {
		this.sceneryLatitude = sceneryLatitude;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	
	
	
}
