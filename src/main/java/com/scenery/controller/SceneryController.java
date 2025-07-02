package com.scenery.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.scenery.model.SceneryVO;
import com.scenery.model.SceneryService;

@Controller
@RequestMapping("/scenery")
public class SceneryController {

	@Autowired
	SceneryService sceSvc;


	@GetMapping("addScenery")
	public String addScenery(ModelMap model) {
		SceneryVO sceVO = new SceneryVO();
		model.addAttribute("SceneryVO", sceVO);
		return "scenery/addScenery";
	}

	
	@PostMapping("updatescenery")
	public String updatescenery(@RequestParam("sceneryId") String sceneryId, ModelMap model) {

		Optional<SceneryVO> sceneryVO = sceSvc.findBySceneryId(Integer.valueOf(sceneryId));

		model.addAttribute("SceneryVO", sceneryVO);
		return "scenery/listonescenery"; 
	}

	@ModelAttribute("sceneryListData")
	protected List<SceneryVO> referenceListData() {
		List<SceneryVO> list = sceSvc.findAllScenery();
		return list;
	}

	public BindingResult removeFieldError(SceneryVO sceVO, BindingResult result, String removedFieldname) {
		List<FieldError> errorsListToKeep = result.getFieldErrors().stream()
				.filter(fieldname -> !fieldname.getField().equals(removedFieldname))
				.collect(Collectors.toList());
		result = new BeanPropertyBindingResult(sceVO, "SceneryVO");
		for (FieldError fieldError : errorsListToKeep) {
			result.addError(fieldError);
		}
		return result;
	}
	
	@PostMapping("listallscenery")
	public String listAllEmp(HttpServletRequest req, Model model) {
		Map<String, String[]> map = req.getParameterMap();
		List<SceneryVO> list = sceSvc.findAllScenery(map);
		model.addAttribute("sceneryListData", list); 
		return "scenery/listallscenery";
	}
	
	@GetMapping("sceneryindex")
	public String showSceneryIndex() {
	    return "scenery/sceneryindex";
	}
}
