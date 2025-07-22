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
	    Files.createDirectories(Paths.get(uploadDir));

	    String extension = ".jpg"; // 固定輸出 JPG
	    String fullPath = uploadDir + newFileName + extension;

	    try (InputStream inputStream = file.getInputStream()) {
	        Thumbnails.of(inputStream)
	                .size(1000, 1000)
	                .outputFormat("jpg")
	                .outputQuality(0.8)
	                .toFile(new File(fullPath));
	    }

	    return "/uploads/" + newFileName + ".jpg"; // 用於前端顯示圖片
	}

	

}
