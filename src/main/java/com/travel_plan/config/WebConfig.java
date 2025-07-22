package com.travel_plan.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	 public void addResourceHandlers(ResourceHandlerRegistry registry) {
	        // 對外開放 uploads 資料夾（放在專案根目錄）
	        registry.addResourceHandler("/uploads/**")
	                .addResourceLocations("file:C:\\TJA101-WebApp\\spring boot\\TJA101G1_TigerTravel\\uploads\\");
	    }	

	
}
