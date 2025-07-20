package com.travel_plan.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface ImageService {

	String saveAndResizeImage(MultipartFile file, String uploadDir, String newFileName) throws Exception;

}
