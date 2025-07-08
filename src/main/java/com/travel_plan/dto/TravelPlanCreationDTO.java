package com.travel_plan.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;

public class TravelPlanCreationDTO {
    
    private Integer travelPlanId; // 旅行計畫ID
    
    @NotBlank(message = "旅行計畫標題不能為空")
    @Size(max = 100, message = "旅行計畫標題長度不能超過100個字")
    private String travelTitle; // 修正：駝峰命名
    
    @NotBlank(message = "旅行計畫描述不能為空")
    @Size(min = 10, max = 500, message = "旅行計畫描述長度必須在10-500個字之間")
    private String travelPlanDescription; // 修正：駝峰命名
    

    
   
    
    private MultipartFile bannerImage; // 旅行計畫橫幅圖片
    
    // 無參構造函數
    public TravelPlanCreationDTO() {
    }
    
    // 有參構造函數
    public TravelPlanCreationDTO(Integer travelPlanId, String travelTitle, String travelPlanDescription
                                ) {
        this.travelPlanId = travelPlanId;
        this.travelTitle = travelTitle;
        this.travelPlanDescription = travelPlanDescription;
        
    }
    
    
    


	// Getter 和 Setter 方法
    public Integer getTravelPlanId() {
        return travelPlanId;
    }
    
    public void setTravelPlanId(Integer travelPlanId) {
        this.travelPlanId = travelPlanId;
    }
    
    public String getTravelTitle() {
        return travelTitle;
    }
    
    public void setTravelTitle(String travelTitle) {
        this.travelTitle = travelTitle;
    }
    
    public String getTravelPlanDescription() {
        return travelPlanDescription;
    }
    
    public void setTravelPlanDescription(String travelPlanDescription) {
        this.travelPlanDescription = travelPlanDescription;
    }
    

    public MultipartFile getBannerImage() {
        return bannerImage;
    }
    
    public void setBannerImage(MultipartFile bannerImage) {
        this.bannerImage = bannerImage;
    }

	@Override
	public String toString() {
		return "TravelPlanCreationDTO [travelPlanId=" + travelPlanId + ", travelTitle=" + travelTitle
				+ ", travelPlanDescription=" + travelPlanDescription + ", bannerImage=" + bannerImage + "]";
	}

    

}