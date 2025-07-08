package com.member.model;

import java.sql.Timestamp;

import com.scenery.model.SceneryVO;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "favorite_tour")
public class FavoriteTourVO {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer favoriteId;
	
	@ManyToOne
	@JoinColumn(name = "member_id")
	private memVO member;
	
	@ManyToOne
	@JoinColumn(name = "scenery_id")
	private SceneryVO scenery;
	
	private Timestamp createTime;
	
	public FavoriteTourVO() {
	}
	
	public FavoriteTourVO(Integer favoriteId, memVO member, Integer sceneryId, Timestamp createTime) {
		super();
		this.favoriteId = favoriteId;
		this.member = member;
		this.scenery = scenery;
		this.createTime = createTime;
	}

	public Integer getFavoriteId() {
		return favoriteId;
	}

	public void setFavoriteId(Integer favoriteId) {
		this.favoriteId = favoriteId;
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

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	

}
