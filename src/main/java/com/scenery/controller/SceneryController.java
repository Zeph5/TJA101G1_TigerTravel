package com.scenery.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.scenery.model.SceneryVO;
import com.scenery.model.DTO.SceneryDTO;
import com.scenery.model.SceneryImageRepository;
import com.scenery.model.SceneryImageVO;
import com.scenery.model.SceneryService;

@Controller
@RequestMapping("/scenery")
public class SceneryController {

	@Autowired
	private SceneryService sceneryService;

	@Autowired
	private SceneryImageRepository sceneryImageRepository;

	@GetMapping("/listallscenery")
	public String listAllScenery(@RequestParam(value = "sceneryName", required = false) String sceneryName,
			@RequestParam(value = "sceneryAddress", required = false) String sceneryAddress,
			@RequestParam(value = "sceneryStatusFilter", required = false, defaultValue = "-1") Integer sceneryStatusFilter,
			@RequestParam(value = "page", defaultValue = "0") int page, Model model) {

		if (sceneryStatusFilter != null && sceneryStatusFilter == -1) {
			sceneryStatusFilter = null;
		}

		int pageSize = 10;
		Pageable pageable = PageRequest.of(page, pageSize);

		Page<SceneryVO> sceneryPage = sceneryService.advancedSearch(sceneryName, sceneryAddress, sceneryStatusFilter,
				pageable);

		model.addAttribute("sceneryPage", sceneryPage);
		model.addAttribute("sceneryName", sceneryName);
		model.addAttribute("sceneryAddress", sceneryAddress);
		model.addAttribute("sceneryStatusFilter", sceneryStatusFilter != null ? sceneryStatusFilter : -1);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", sceneryPage.getTotalPages());

		return "scenery/listallscenery";
	}

	@GetMapping("/banner/{sceneryId}")
	public ResponseEntity<byte[]> getSceneryBanner(@PathVariable Integer sceneryId) {
		SceneryVO scenery = sceneryService.getById(sceneryId);
		if (scenery == null || scenery.getSceneryBanner() == null) {
			return ResponseEntity.notFound().build();
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG);
		return new ResponseEntity<>(scenery.getSceneryBanner(), headers, HttpStatus.OK);
	}

	@GetMapping("/index")
	public String sceneryIndex(Model model) {
		return "scenery/sceneryindex";
	}

	@GetMapping("/addscenery")
	public String showAddForm(Model model) {
		model.addAttribute("sceneryDTO", new SceneryDTO());
		return "scenery/addscenery";
	}

	@PostMapping("/addscenery")
	public String addScenery(@ModelAttribute SceneryDTO dto, Model model) throws IOException {
		SceneryVO vo = new SceneryVO();
		vo.setSceneryName(dto.getSceneryName());
		vo.setSceneryIntro(dto.getSceneryIntro());
		vo.setSceneryAddress(dto.getSceneryAddress());
		vo.setSceneryLongitude(dto.getSceneryLongitude());
		vo.setSceneryLatitude(dto.getSceneryLatitude());

		MultipartFile file = dto.getSceneryBannerFile();
		if (file != null && !file.isEmpty()) {
			vo.setSceneryBanner(file.getBytes());
		}

		sceneryService.addScenery(vo);
		return "scenery/addsuccess";
	}
	
	

	@GetMapping("/updatescenery/{id}")
	public String showUpdateScenery(@PathVariable Integer id, Model model) {
	    SceneryVO scenery = sceneryService.getById(id);
	    if (scenery == null) {
	        return "redirect:/scenery/listallscenery";
	    }
	    Set<SceneryImageVO> images = sceneryService.getImagesBySceneryId(id).stream().collect(Collectors.toSet());
	    scenery.setSceneryImages(images);

	    SceneryDTO dto = sceneryService.convertToDTO(scenery);
	    model.addAttribute("sceneryDTO", dto);
	    return "scenery/updatescenery";
	}

	@PostMapping("/updatescenery")
	public String updateScenery(@ModelAttribute SceneryDTO dto) throws IOException {
		SceneryVO existing = sceneryService.getById(dto.getSceneryId());
		if (existing == null) {
			return "redirect:/scenery/listallscenery";
		}

		existing.setSceneryName(dto.getSceneryName());
		existing.setSceneryIntro(dto.getSceneryIntro());
		existing.setSceneryAddress(dto.getSceneryAddress());
		existing.setSceneryLongitude(dto.getSceneryLongitude());
		existing.setSceneryLatitude(dto.getSceneryLatitude());

		MultipartFile bannerFile = dto.getSceneryBannerFile();
		if (bannerFile != null && !bannerFile.isEmpty()) {
			existing.setSceneryBanner(bannerFile.getBytes());
		}

		sceneryService.updateScenery(existing);

		// Save multiple gallery images if any uploaded
		if (dto.getSceneryImages() != null && !dto.getSceneryImages().isEmpty()) {
			for (MultipartFile imageFile : dto.getSceneryImages()) {
				if (!imageFile.isEmpty()) {
					sceneryService.addSceneryImage(existing.getSceneryId(), imageFile);
				}
			}
		}

		return "redirect:/scenery/" + existing.getSceneryId();
	}

	@GetMapping("/{id}")
	public String showSelectedScenery(@PathVariable("id") Integer id, Model model) {
		SceneryVO scenery = sceneryService.getById(id);
		if (scenery == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Scenery not found");
		}

		List<SceneryImageVO> images = sceneryService.getImagesBySceneryId(id);
		model.addAttribute("scenery", scenery);
		model.addAttribute("sceneryImages", images);

		return "scenery/selectedscenery";
	}

	@PostMapping("/updatestatus")
	public String updateSceneryStatus(@RequestParam("sceneryId") Integer sceneryId,
			@RequestParam("sceneryStatus") Integer sceStatus) {
		sceneryService.updateSceneryStatus(sceneryId, sceStatus);
		return "redirect:/scenery/listallscenery";
	}

	@PostMapping("/{sceneryId}/addimage")
	public String addSceneryImage(@PathVariable Integer sceneryId, @RequestParam("imageFile") MultipartFile imageFile)
			throws IOException {
		if (imageFile == null || imageFile.isEmpty()) {
			return "redirect:/scenery/" + sceneryId + "?error=NoFile";
		}
		sceneryService.addSceneryImage(sceneryId, imageFile);
		return "redirect:/scenery/" + sceneryId;
	}

	@GetMapping("/image/{id}")
	public ResponseEntity<byte[]> getSceneryImage(@PathVariable("id") Integer id) {
		SceneryImageVO img = sceneryImageRepository.findById(id).orElse(null);
		if (img == null || img.getSceneryImage() == null) {
			return ResponseEntity.notFound().build();
		}

		Byte[] imageBytes = img.getSceneryImage();
		byte[] bytes = new byte[imageBytes.length];
		for (int i = 0; i < imageBytes.length; i++) {
			bytes[i] = imageBytes[i];
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_JPEG);
		return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
	}
	
	@GetMapping("/deleteimage/{imageId}")
	public String deleteSceneryImage(@PathVariable Integer imageId) {
	    // Find the image by id
	    Optional<SceneryImageVO> imageOpt = sceneryImageRepository.findById(imageId);
	    if (imageOpt.isPresent()) {
	        SceneryImageVO image = imageOpt.get();
	        Integer sceneryId = image.getScenery().getSceneryId(); // Get the associated scenery id
	        
	        // Delete the image
	        sceneryImageRepository.delete(image);
	        
	        // Redirect back to the update scenery page
	        return "redirect:/scenery/updatescenery/" + sceneryId;
	    } else {
	        // Image not found - redirect somewhere safe or show error
	        return "redirect:/scenery/listallscenery";
	    }
	}
}
