package com.member.model;

import java.sql.Timestamp;

import com.scenery.model.SceneryVO;

import jakarta.persistence.*;

@Entity
@Table(name = "favorite_tour")
public class FavoriteTourVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_tour_id") // ⬅️ 明確指定對應的資料庫欄位名稱
    private Integer favoriteTourId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private memVO member;

    @ManyToOne
    @JoinColumn(name = "scenery_id")
    private SceneryVO scenery;

    private Timestamp createTime;

    public FavoriteTourVO() {
    }

    public FavoriteTourVO(Integer favoriteTourId, memVO member, SceneryVO scenery, Timestamp createTime) {
        this.favoriteTourId = favoriteTourId;
        this.member = member;
        this.scenery = scenery;
        this.createTime = createTime;
    }

    public Integer getFavoriteTourId() {
        return favoriteTourId;
    }

    public void setFavoriteTourId(Integer favoriteTourId) {
        this.favoriteTourId = favoriteTourId;
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
