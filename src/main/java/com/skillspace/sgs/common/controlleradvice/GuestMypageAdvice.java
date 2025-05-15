package com.skillspace.sgs.common.controlleradvice;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice(basePackages = "com.skillspace.sgs.guest.mypage")

public class GuestMypageAdvice {
    
    @ModelAttribute("currentUri")
    public String addCurrentUri(HttpServletRequest request) {
        if (isAjaxRequest(request)) {
            return null;
        }
        return request.getRequestURI();
    }

    /**
	 * 요청이 AJAX 요청인지 확인합니다.
	 * (X-Requested-With 헤더를 기준으로 판단)
	 */
	private boolean isAjaxRequest(HttpServletRequest request) {
		return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
	}
}
