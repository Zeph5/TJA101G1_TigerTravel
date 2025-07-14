package com.scenery.model.DTO;

import org.springframework.web.multipart.MultipartFile;

import com.scenery.model.SceneryImageVO;

import java.util.List;

public class SceneryDTO {
    private Integer sceneryId;
    private String sceneryName;
    private String sceneryIntro;
    private String sceneryAddress;
    private Double sceneryLongitude;
    private Double sceneryLatitude;
    private MultipartFile sceneryBannerFile;
    private List<MultipartFile> sceneryImages;  // <-- added
    private List<SceneryImageVO> existingImages;

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

    public MultipartFile getSceneryBannerFile() {
        return sceneryBannerFile;
    }

    public void setSceneryBannerFile(MultipartFile sceneryBannerFile) {
        this.sceneryBannerFile = sceneryBannerFile;
    }

    public List<MultipartFile> getSceneryImages() {
        return sceneryImages;
    }

    public void setSceneryImages(List<MultipartFile> sceneryImages) {
        this.sceneryImages = sceneryImages;
    }
    
    public List<SceneryImageVO> getExistingImages() {
        return existingImages;
    }

    public void setExistingImages(List<SceneryImageVO> existingImages) {
        this.existingImages = existingImages;
    }
}
