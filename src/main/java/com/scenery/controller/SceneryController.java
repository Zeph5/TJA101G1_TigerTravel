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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.*;
import java.util.stream.Collectors;

import com.scenery.model.SceneryVO;
import com.scenery.model.SceneryService;

@Controller
@RequestMapping("/scenery")
public class SceneryController {

    @Autowired
    SceneryService sceSvc;

    @GetMapping("addscenery")
    public String addScenery(ModelMap model) {
        SceneryVO sceVO = new SceneryVO();
        model.addAttribute("SceneryVO", sceVO);
        return "scenery/addscenery";
    }

    @PostMapping("updatescenery")
    public String updatescenery(@RequestParam("sceneryId") String sceneryId, ModelMap model) {
        Optional<SceneryVO> sceneryVO = sceSvc.findBySceneryId(Integer.valueOf(sceneryId));
        model.addAttribute("SceneryVO", sceneryVO.orElse(null));  // unwrap Optional safely
        return "scenery/listonescenery"; 
    }
    
    @PostMapping("/update")
    public String updateScenery(@ModelAttribute("SceneryVO") SceneryVO sceneryVO, Model model) {
        // Save the updated data
        sceSvc.updateScenery(sceneryVO);

        // Reload the updated data and redirect to detail page
        model.addAttribute("SceneryVO", sceneryVO);
        return "scenery/listonescenery"; // ⬅️ Forward to show updated result
    }

    @ModelAttribute("sceneryListData")
    protected List<SceneryVO> referenceListData() {
        return sceSvc.findAllScenery();
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
    
    // New GET handler to show all scenery (no filters)
    @GetMapping("listallscenery")
    public String listAllSceneryGet(Model model) {
        List<SceneryVO> list = sceSvc.findAllScenery();
        model.addAttribute("sceneryListData", list);
        return "scenery/listallscenery";
    }

    // Existing POST handler for filtered search
    @PostMapping("listallscenery")
    public String listAllSceneryPost(HttpServletRequest req, Model model) {
        Map<String, String[]> map = req.getParameterMap();
        List<SceneryVO> list = sceSvc.findAllScenery(map);
        model.addAttribute("sceneryListData", list); 
        return "scenery/listallscenery";
    }
    
    @PostMapping("listonescenery")
    public String postOneScenery(@RequestParam("sceneryId") Integer sceneryId, Model model) {
        Optional<SceneryVO> sceneryVO = sceSvc.findBySceneryId(sceneryId);
        if (sceneryVO.isPresent()) {
            model.addAttribute("SceneryVO", sceneryVO.get());
            return "scenery/listonescenery";
        } else {
            model.addAttribute("errorMessage", "查無資料，景點編號: " + sceneryId);
            return "scenery/sceneryindex";  // or redirect to a 404 page
        }
    }
    
    @PostMapping("/showupdatescenery")
    public String showUpdateSceneryForm(@RequestParam("sceneryId") Integer sceneryId, Model model) {
        Optional<SceneryVO> sceneryVO = sceSvc.findBySceneryId(sceneryId);
        if (sceneryVO.isPresent()) {
            model.addAttribute("SceneryVO", sceneryVO.get());
            return "scenery/updatescenery"; // ✅ This view must exist
        } else {
            model.addAttribute("errorMessage", "查無資料，景點編號: " + sceneryId);
            return "scenery/sceneryindex";
        }
    }
    
    @PostMapping("/addscenery")
    public String insertScenery(@ModelAttribute("SceneryVO") SceneryVO sceneryVO, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "scenery/addscenery";
        }

        sceSvc.add(sceneryVO); // persist to DB
        model.addAttribute("SceneryVO", sceneryVO);
        return "scenery/addsuccess"; // ✅ show success page
    }

    
    @GetMapping("sceneryindex")
    public String showSceneryIndex() {
        return "scenery/sceneryindex";
    }
}
