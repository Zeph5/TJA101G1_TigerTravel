package com.travel_plan.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;



public class TravelPlanCreationDTO {
	private Integer travelPlanId; // 旅行計畫ID
	
	@NotBlank(message = "旅行計畫標題不能為空")
	@Size(max = 100, message = "旅行計畫標題長度不能超過100個字")
	private String traveltitle; // 旅行計畫標題
	
	@NotBlank(message = "旅行計畫描述不能為空")
	@Size(max = 500, message = "旅行計畫描述長度不能超過500個字")
	private String travelplandescription; // 旅行計畫描述
	
	private MultipartFile bannerImage; // 旅行計畫橫幅圖片URL

	public Integer getTravelPlanId() {
		return travelPlanId;
	}

	public void setTravelPlanId(Integer travelPlanId) {
		this.travelPlanId = travelPlanId;
	}

	public String getTraveltitle() {
		return traveltitle;
	}

	public void setTraveltitle(String traveltitle) {
		this.traveltitle = traveltitle;
	}

	public String getTravelplandescription() {
		return travelplandescription;
	}

	public void setTravelplandescription(String travelplandescription) {
		this.travelplandescription = travelplandescription;
	}

	public MultipartFile getBannerImage() {
		return bannerImage;
	}

	public void setBannerImage(MultipartFile bannerImage) {
		this.bannerImage = bannerImage;
	}

	@Override
	public String toString() {
		return "TravelPlanCreatDTO [travelPlanId=" + travelPlanId + ", traveltitle=" + traveltitle
				+ ", travelplandescription=" + travelplandescription + ", bannerImage=" + bannerImage + "]";
	}


	
	
	
	
}
