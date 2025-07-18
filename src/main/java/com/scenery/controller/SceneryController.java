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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Base64;

import com.scenery.model.SceneryVO;
import com.scenery.model.SceneryScoreVO;
import com.scenery.model.SceneryScoreRepository;
import com.scenery.model.DTO.SceneryDTO;
import com.scenery.model.SceneryImageRepository;
import com.scenery.model.SceneryImageVO;
import com.scenery.model.SceneryService;
import com.member.model.memVO;
import com.member.service.FavoriteSceneryService;
import com.member.service.MemberService; // Fixed import path

@Controller
@RequestMapping("/scenery")
public class SceneryController {

	@Autowired
	private SceneryService sceneryService;

	@Autowired
	private SceneryImageRepository sceneryImageRepository;

	@Autowired
	private SceneryScoreRepository sceneryScoreRepository;

	@Autowired
	private MemberService memberService;
	

	// ===== ADMIN/BACKEND SCENERY MANAGEMENT =====

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
		vo.setSceneryStatus(1); // Set default status to active

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

	@GetMapping("/deleteimage/{imageId}")
	public String deleteSceneryImage(@PathVariable Integer imageId) {
	    Optional<SceneryImageVO> imageOpt = sceneryImageRepository.findById(imageId);
	    if (imageOpt.isPresent()) {
	        SceneryImageVO image = imageOpt.get();
	        Integer sceneryId = image.getScenery().getSceneryId();
	        sceneryImageRepository.delete(image);
	        return "redirect:/scenery/updatescenery/" + sceneryId;
	    } else {
	        return "redirect:/scenery/listallscenery";
	    }
	}

	// ===== FRONTEND SCENERY VIEWING =====

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

	// Note: Frontend scenery detail page with comments is now handled by IndexController
	// Route: /frontend/scenery/detail/{id} → IndexController.showSceneryDetail()
	// Route: /frontend/scenery/detail/{id}/add-comment → IndexController.addComment()

	// ===== IMAGE SERVING ENDPOINTS =====

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

	// ===== DEBUG ENDPOINT =====

	@GetMapping("/debug/scenery/{id}")
	@ResponseBody
	public String debugScenery(@PathVariable("id") Integer id) {
		try {
			StringBuilder debug = new StringBuilder();
			debug.append("=== SCENERY DEBUG INFO ===<br>");
			
			// Test basic scenery loading
			SceneryVO scenery = sceneryService.getById(id);
			if (scenery == null) {
				return "Scenery not found for ID: " + id;
			}
			
			debug.append("Scenery ID: ").append(scenery.getSceneryId()).append("<br>");
			debug.append("Scenery Name: ").append(scenery.getSceneryName()).append("<br>");
			debug.append("Scenery Status: ").append(scenery.getSceneryStatus()).append("<br>");
			
			// Test score loading
			try {
				List<SceneryScoreVO> scores = sceneryScoreRepository.findByScenery_SceneryIdOrderByCreateTimeDesc(id);
				debug.append("Scores found: ").append(scores.size()).append("<br>");
				
				for (int i = 0; i < Math.min(3, scores.size()); i++) {
					SceneryScoreVO score = scores.get(i);
					debug.append("Score ").append(i+1).append(": ");
					debug.append("Rating=").append(score.getScore());
					
					try {
						memVO member = score.getMember();
						if (member != null) {
							debug.append(", Member Account=").append(member.getMemberAccount());
							debug.append(", Member Name=").append(member.getMemberName() != null ? member.getMemberName() : "NULL");
						} else {
							debug.append(", Member=null");
						}
					} catch (Exception e) {
						debug.append(", Member=ERROR: ").append(e.getMessage());
					}
					
					debug.append(", Comment=").append(score.getSceneryComment() != null ? "YES" : "NO");
					debug.append("<br>");
				}
			} catch (Exception e) {
				debug.append("ERROR loading scores: ").append(e.getMessage()).append("<br>");
				e.printStackTrace();
			}
			
			// Test rating calculation
			try {
				Double rating = scenery.getRating();
				debug.append("Calculated rating: ").append(rating).append("<br>");
			} catch (Exception e) {
				debug.append("ERROR calculating rating: ").append(e.getMessage()).append("<br>");
				e.printStackTrace();
			}
			
			// Test cached rating fields
			debug.append("Cached Total Score: ").append(scenery.getSceneryTotalScore()).append("<br>");
			debug.append("Cached Score Count: ").append(scenery.getSceneryTotalScoreCount()).append("<br>");
			
			debug.append("=== END DEBUG ===");
			return debug.toString();
			
		} catch (Exception e) {
			e.printStackTrace();
			return "ERROR: " + e.getMessage() + "<br>Check console for full stack trace";
		}
	}
	
	
}