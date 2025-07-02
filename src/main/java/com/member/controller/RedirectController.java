package com.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectController {

    // 使用者若輸入 /register，轉跳至正式註冊頁
    @GetMapping("/register")
    public String redirectToMemberRegister() {
        return "redirect:/member/register";
    }
}