package com.scenery.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.scenery.model.TagsdbService;
import com.scenery.model.TagsdbVO;

@Controller
@RequestMapping("/tagsdb")
public class TagsdbController {

    @Autowired
    private TagsdbService tagsdbService;

    @GetMapping("/listall")
    public String listAllTagsdb(Model model) {
        List<TagsdbVO> tagsList = tagsdbService.findAll();
        model.addAttribute("tagsdbList", tagsList);
        return "tags/listalltagsdb";
    }
	
    // 🔍 Search by Tag Name
    @GetMapping("/search")
    public String searchByTagName(@RequestParam("tagName") String tagName, Model model) {
        List<TagsdbVO> tags = tagsdbService.findByTagsNameContaining(tagName);
        model.addAttribute("tagsdbList", tags);
        model.addAttribute("searchType", "Tag Name: " + tagName);
        return "tags/tagsdbsearchresult";
    }
    
    // ➕ Show Add Form
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("tagsdb", new TagsdbVO());
        return "tags/addtagsdb";
    }

 // Handle Add
    @PostMapping("/add")
    public String addNewTag(@ModelAttribute TagsdbVO tagsdb, Model model) {
        try {
            tagsdbService.save(tagsdb);
            return "redirect:/tagsdb/addsuccess";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "tags/addtagsdb";
        } catch (Exception e) {
            model.addAttribute("error", "Add failed: " + e.getMessage());
            return "tags/addtagsdb";
        }
    }
    
    @GetMapping("/addsuccess")
    public String showAddSuccessPage() {
        return "tags/addtagsdbsuccess";
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        Optional<TagsdbVO> optional = tagsdbService.findById(id);
        if (optional.isEmpty()) {
            return "redirect:/tagsdb/listall";
        }
        model.addAttribute("tagsdb", optional.get());
        return "tags/updatetagsdb";
    }

 // Handle Update
    @PostMapping("/update")
    public String updateTag(@ModelAttribute TagsdbVO tagsdb, Model model) {
        try {
            tagsdbService.save(tagsdb);
            return "redirect:/tagsdb/updatesuccess";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "tags/updatetagsdb";
        } catch (Exception e) {
            model.addAttribute("error", "Update failed: " + e.getMessage());
            return "tags/updatetagsdb";
        }
    }

    // Update success page
    @GetMapping("/updatesuccess")
    public String showUpdateSuccessPage() {
        return "tags/updatetagsdbsuccess";
    }
    
    @GetMapping("/delete/{id}")
    public String deleteTag(@PathVariable("id") Integer id, Model model) {
        Optional<TagsdbVO> optional = tagsdbService.findById(id);
        if (optional.isPresent()) {
            tagsdbService.deleteById(id);
        }
        return "redirect:/tagsdb/listall";
    }
}
	
