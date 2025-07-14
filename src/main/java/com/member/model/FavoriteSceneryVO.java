package com.member.model;

import java.time.LocalDateTime;

import com.scenery.model.SceneryVO;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "favorite_scenery")
public class FavoriteSceneryVO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer favoriteSceId;
	
	@ManyToOne
	@JoinColumn(name = "member_id")
	private memVO member;
	
	@ManyToOne
	@JoinColumn(name = "scenery_id")
	private SceneryVO scenery;
	
	private LocalDateTime createTime;
	
	public FavoriteSceneryVO() {}

	public FavoriteSceneryVO(Integer favoriteSceId, memVO member, SceneryVO scenery, LocalDateTime createTime) {
		super();
		this.favoriteSceId = favoriteSceId;
		this.member = member;
		this.scenery = scenery;
		this.createTime = createTime;
	}

	public Integer getFavoriteSceId() {
		return favoriteSceId;
	}

	public void setFavoriteSceId(Integer favoriteSceId) {
		this.favoriteSceId = favoriteSceId;
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

	public LocalDateTime getCreateTime() {
		return createTime;
	}

	public void setCreateTime(LocalDateTime createTime) {
		this.createTime = createTime;
	};
	
}
