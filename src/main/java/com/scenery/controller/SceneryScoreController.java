package com.scenery.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.scenery.model.SceneryScoreService;
import com.scenery.model.SceneryScoreVO;

@Controller
@RequestMapping("/sceneryscore")
public class SceneryScoreController {

	 @Autowired
	    private SceneryScoreService scoreService;

	    @GetMapping("/findAll")
	    public String findAllScores(Model model) {
	        List<SceneryScoreVO> scores = scoreService.findAll();
	        model.addAttribute("scoreList", scores);
	        return "/scenery/findallsceneryscore"; 
	        
	    }
	    
	    @GetMapping("/search")
	    public String searchScores(
	            @RequestParam(required = false) String memberAccount,
	            @RequestParam(required = false) String sceneryName,
	            Model model) {

	        List<SceneryScoreVO> result = scoreService.searchScores(memberAccount, sceneryName);
	        model.addAttribute("scoreList", result);
	        return "/scenery/findallsceneryscore";
	    }
	    
	    @PostMapping("/sceneryscore/delete/{id}")
	    public String deleteScore(@PathVariable Integer id) {
	        scoreService.deleteById(id);
	        return "redirect:/sceneryscore/findAll"; 
	    }
	}
