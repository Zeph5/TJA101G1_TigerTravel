package com.scenery.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scenery.model.SceneryScoreService;

@Controller
@RequestMapping("/score")
public class SceneryScoreController {

	@Autowired
	SceneryScoreService scescoreSvc;
}
