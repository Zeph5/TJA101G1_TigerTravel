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
@Table(name = "scenery_score")
public class SceneryScoreVO implements java.io.Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer scoreId;
	
	private Integer memberId;
	
	@Column(name = "score")
	private Integer score;
	
	private Integer sceneryId;
	
	@Column(name = "sce_comment")
	private String sceneryComment;
	
	@Column(updatable = false)
	@CreationTimestamp
	private Timestamp createTime;

	public SceneryScoreVO() {
		super();
	}

	public SceneryScoreVO(Integer scoreId, Integer memberId, Integer score, Integer sceneryId, String sceneryComment,
			Timestamp createTime) {
		super();
		this.scoreId = scoreId;
		this.memberId = memberId;
		this.score = score;
		this.sceneryId = sceneryId;
		this.sceneryComment = sceneryComment;
		this.createTime = createTime;
	}

	public Integer getScoreId() {
		return scoreId;
	}

	public void setScoreId(Integer scoreId) {
		this.scoreId = scoreId;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Integer getSceneryId() {
		return sceneryId;
	}

	public void setSceneryId(Integer sceneryId) {
		this.sceneryId = sceneryId;
	}

	public String getSceneryComment() {
		return sceneryComment;
	}

	public void setSceneryComment(String sceneryComment) {
		this.sceneryComment = sceneryComment;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	
	
}
