package com.scenery.model;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import com.member.model.memVO;

import jakarta.persistence.*;

@Entity
@Table(name = "scenery_score")
public class SceneryScoreVO implements java.io.Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer scoreId;

	@ManyToOne
	@JoinColumn(name = "member_id", nullable = false)
	private memVO member;

	@ManyToOne
	@JoinColumn(name = "scenery_id", nullable = false)
	private SceneryVO scenery;

	@Column(name = "score")
	private Integer score;

	@Column(name = "sce_comment")
	private String sceneryComment;

	@Column(updatable = false)
	@CreationTimestamp
	private Timestamp createTime;

	public SceneryScoreVO() {
		super();
	}

	public SceneryScoreVO(Integer scoreId, memVO member, SceneryVO scenery, Integer score, String sceneryComment,
			Timestamp createTime) {
		super();
		this.scoreId = scoreId;
		this.member = member;
		this.scenery = scenery;
		this.score = score;
		this.sceneryComment = sceneryComment;
		this.createTime = createTime;
	}

	public Integer getScoreId() {
		return scoreId;
	}

	public void setScoreId(Integer scoreId) {
		this.scoreId = scoreId;
	}

	public memVO getMember() {
		return member;
	}

	public void setMember(memVO member) {
		this.member = member;
	}

	public SceneryVO getScenery() {
		return scenery;
	}

	public void setScenery(SceneryVO scenery) {
		this.scenery = scenery;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
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