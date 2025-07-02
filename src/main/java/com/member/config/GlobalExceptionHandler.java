package com.member.config;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {
        ex.printStackTrace(); // 強制印出錯誤
        model.addAttribute("errorMessage", ex.getMessage());
        return "custom-error"; // 你可以建立 templates/custom-error.html
    }
}