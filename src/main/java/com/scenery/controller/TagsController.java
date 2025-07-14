package com.scenery.controller;

import java.util.List;
import java.util.Optional;

import com.scenery.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.scenery.model.SceneryRepository;
import com.scenery.model.TagsService;
import com.scenery.model.TagsdbRepository;
import com.scenery.model.DTO.TagsForm;

@Controller
@RequestMapping("/tags")
public class TagsController {

	 @Autowired
	    private TagsService tagsService;

	    @Autowired
	    private TagsdbRepository tagsdbRepository;

	    @Autowired
	    private SceneryRepository sceneryRepository;
	    
	    @Autowired
	    private TagsRepository tagsRepository;

	    @GetMapping("/add")
	    public String showAddTagForm(Model model) {
	        model.addAttribute("tagsForm", new TagsForm());

	        List<TagsdbVO> allTagsdb = tagsdbRepository.findAll();
	        List<SceneryVO> allScenery = sceneryRepository.findAll();

	        model.addAttribute("tagsdbList", allTagsdb);
	        model.addAttribute("sceneryList", allScenery);

	        return "tags/addtags";
	    }

	    @PostMapping("/add")
	    public String handleAddTag(@ModelAttribute("tagsForm") TagsForm tagsForm,
	                               BindingResult result,
	                               Model model) {
	        if (result.hasErrors()) {
	            return "tags/addtags";
	        }

	        String sceneryName = tagsForm.getSceneryName();

	        if (sceneryName == null || sceneryName.isBlank()) {
	            model.addAttribute("error", "Please enter or select a scenery.");
	            // reload lists for form
	            model.addAttribute("tagsdbList", tagsdbRepository.findAll());
	            model.addAttribute("sceneryList", sceneryRepository.findAll());
	            return "tags/addtags";
	        }
	        
	        Optional<SceneryVO> optionalScenery = sceneryRepository.findBySceneryName(sceneryName.trim());
	        if (optionalScenery.isEmpty()) {
	            model.addAttribute("error", "No scenery found with name: " + sceneryName);
	            model.addAttribute("tagsdbList", tagsdbRepository.findAll());
	            model.addAttribute("sceneryList", sceneryRepository.findAll());
	            return "tags/addtags";
	        }

	        Integer sceneryId = optionalScenery.get().getSceneryId();

	        try {
	            tagsService.addTag(tagsForm.getTagsdbId(), sceneryId);
	        } catch (IllegalArgumentException e) {
	            model.addAttribute("error", e.getMessage());
	            model.addAttribute("tagsdbList", tagsdbRepository.findAll());
	            model.addAttribute("sceneryList", sceneryRepository.findAll());
	            return "tags/addtags";
	        }

	        return "redirect:/tags/tagaddsuccess";
	    }

	    
	    @GetMapping("/tagaddsuccess")
	    public String showSuccessPage() {
	        return "tags/tagaddsuccess";
	    }
	    
	    @GetMapping("/list")
	    public String listAllTags(Model model) {
	        List<TagsVO> allTags = tagsRepository.findAll();
	        model.addAttribute("tagsList", allTags);
	        return "tags/listalltags";
	    }

	    @GetMapping("/searchByTagName")
	    public String searchByTagName(@RequestParam("tagName") String tagName, Model model) {
	        List<TagsVO> tags = tagsRepository.findByTagsdb_TagsName(tagName);
	        model.addAttribute("tagsList", tags);
	        model.addAttribute("searchType", "Tag Name: " + tagName);
	        return "tags/tagsearchresult";
	    }

	    @GetMapping("/searchBySceneryName")
	    public String searchBySceneryName(@RequestParam("sceneryName") String sceneryName, Model model) {
	        List<TagsVO> tags = tagsRepository.findByScenery_SceneryName(sceneryName);
	        model.addAttribute("tagsList", tags);
	        model.addAttribute("searchType", "Scenery Name: " + sceneryName);
	        return "tags/tagsearchresult";
	    }
	    
	    @GetMapping("/edit/{id}")
	    public String showEditForm(@PathVariable("id") Integer id, Model model) {
	        Optional<TagsVO> optionalTag = tagsService.findById(id);
	        if (optionalTag.isEmpty()) {
	            return "redirect:/tags/list";
	        }

	        TagsVO tag = optionalTag.get();
	        TagsForm form = new TagsForm();
	        form.setTagsId(tag.getTagsId());
	        form.setTagsdbId(tag.getTagsdb().getTagsdbId());
	        form.setSceneryId(tag.getScenery().getSceneryId());

	        model.addAttribute("tagsForm", form);
	        model.addAttribute("tagsdbList", tagsdbRepository.findAll());
	        model.addAttribute("sceneryList", sceneryRepository.findAll());

	        return "tags/updatetagsuccess";
	    }

	    @PostMapping("/update")
	    public String updateTag(@ModelAttribute("tagsForm") TagsForm form, Model model) {
	        try {
	            tagsService.updateTag(form.getTagsId(), form.getTagsdbId(), form.getSceneryId());
	            return "redirect:/tags/list";
	        } catch (Exception e) {
	            model.addAttribute("error", "Update failed: " + e.getMessage());
	            model.addAttribute("tagsdbList", tagsdbRepository.findAll());
	            model.addAttribute("sceneryList", sceneryRepository.findAll());
	            return "tags/updatetagsuccess";
	        }
	    }
	   
}
