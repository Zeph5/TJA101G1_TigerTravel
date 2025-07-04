package com.scenery.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scenery.model.TagsService;

@Controller
@RequestMapping("/tags")
public class TagsController {

	@Autowired
	TagsService tagsSvc;
}
