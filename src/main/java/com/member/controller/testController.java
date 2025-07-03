package com.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class testController {
    @GetMapping("/test")
    public String testPage() {
        return "test"; // 對應 templates/test.html
    }
}
