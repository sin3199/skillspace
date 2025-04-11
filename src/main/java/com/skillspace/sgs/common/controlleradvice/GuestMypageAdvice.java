package com.skillspace.sgs.common.controlleradvice;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice(basePackages = "com.skillspace.sgs.guest.mypage")

public class GuestMypageAdvice {
    
    @ModelAttribute("currentUri")
    public String addCurrentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
