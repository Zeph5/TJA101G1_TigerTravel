package com.scenery.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scenery.model.TagsdbService;

@Controller
@RequestMapping("/tagsdb")
public class TagsdbController {

	@Autowired
	TagsdbService tdbSvc;
	
	
}
