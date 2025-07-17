package com.member.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice  // ⭐ 關鍵！這讓它全局抓錯
public class ErrorController {

    // 專門抓參數缺失錯
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public String handleMissingParams(MissingServletRequestParameterException ex, Model model) {
        model.addAttribute("error", "缺少必要參數：" + ex.getParameterName() + "，請從正確頁面進入！");
        return "error-page";  // 導到你的錯誤頁
    }

    // 加這個抓其他一般錯誤（取代舊的handleError）
    @ExceptionHandler(Exception.class)
    public String handleGeneralError(Exception ex, Model model) {
        model.addAttribute("error", "系統出錯啦：" + ex.getMessage() + "，請聯絡管理員！");
        return "error_page";
    }
}	