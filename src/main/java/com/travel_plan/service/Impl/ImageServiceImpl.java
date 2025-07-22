package com.travel_plan.service.Impl;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.travel_plan.service.ImageService;

import net.coobird.thumbnailator.Thumbnails;

@Service
public class ImageServiceImpl implements ImageService {

	


	@Override
	public String saveAndResizeImage(MultipartFile file, String uploadDir, String newFileName) throws Exception {
	    // 確保目錄存在
	    Files.createDirectories(Paths.get(uploadDir));

	    // 圖片副檔名（保留原始格式）
	    String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
	    String fullPath = uploadDir + newFileName + extension;

	    try (InputStream inputStream = file.getInputStream()) {
	        // 縮放圖片到 1000x1000（保持比例），並輸出為 JPG
	        Thumbnails.of(inputStream)
	                .size(1000, 1000) // 最大寬高
	                .outputFormat("jpg") // 輸出格式
	                .outputQuality(0.8)  // 圖片品質（0~1）
	                .toFile(new File(fullPath));
	    }
	    System.out.println("📂 儲存圖片到：" + fullPath);
	    // 回傳相對路徑存入資料庫
	    return "/uploads/" + newFileName + ".jpg";
	}
	

}
