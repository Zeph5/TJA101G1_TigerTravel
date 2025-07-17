package com.scenery.model;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "scenery")
public class SceneryVO implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scenery_id")
    private Integer sceneryId;

    @Column(name = "sce_name", nullable = false, unique = true)
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

    @Lob
    @Column(name = "sce_banner", columnDefinition = "LONGBLOB")
    @Basic(fetch = FetchType.LAZY)
    private byte[] sceneryBanner;

    @Column(name = "sce_status")
    private Integer sceneryStatus;

    @Column(updatable = false)
    @CreationTimestamp
    private Timestamp createTime;

    @Transient
    private Double rating;

    @OneToMany(mappedBy = "scenery", fetch = FetchType.LAZY)
    private List<SceneryScoreVO> sceneryScores;
    
    @OneToMany(mappedBy = "scenery", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<SceneryImageVO> sceneryImages = new HashSet<>();

    public Set<SceneryImageVO> getSceneryImages() {
        return sceneryImages;
    }

    public void setSceneryImages(Set<SceneryImageVO> sceneryImages) {
        this.sceneryImages = sceneryImages;
    }

    public SceneryVO() {
        super();
    }

    public SceneryVO(Integer sceneryId, String sceneryName, String sceneryIntro, Integer sceneryTotalScore,
                     Integer sceneryTotalScoreCount, String sceneryAddress, Double sceneryLongitude,
                     Double sceneryLatitude, byte[] sceneryBanner, Timestamp createTime) {
        super();
        this.sceneryId = sceneryId;
        this.sceneryName = sceneryName;
        this.sceneryIntro = sceneryIntro;
        this.sceneryTotalScore = sceneryTotalScore;
        this.sceneryTotalScoreCount = sceneryTotalScoreCount;
        this.sceneryAddress = sceneryAddress;
        this.sceneryLongitude = sceneryLongitude;
        this.sceneryLatitude = sceneryLatitude;
        this.sceneryBanner = sceneryBanner;
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

    public byte[] getSceneryBanner() {
        return sceneryBanner;
    }

    public void setSceneryBanner(byte[] sceneryBanner) {
        this.sceneryBanner = sceneryBanner;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Transient
    public Double getRating() {
        if (sceneryScores == null || sceneryScores.isEmpty()) {
            return 0.0;
        }
        double total = sceneryScores.stream().mapToInt(SceneryScoreVO::getScore).sum();
        return Math.round((total / sceneryScores.size()) * 10.0) / 10.0; // rounded to 1 decimal
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public List<SceneryScoreVO> getSceneryScores() {
        return sceneryScores;
    }

    public void setSceneryScores(List<SceneryScoreVO> sceneryScores) {
        this.sceneryScores = sceneryScores;
    }

    public Integer getSceneryStatus() {
        return sceneryStatus;
    }

    public void setSceneryStatus(Integer sceneryStatus) {
        this.sceneryStatus = sceneryStatus;
    }

}
