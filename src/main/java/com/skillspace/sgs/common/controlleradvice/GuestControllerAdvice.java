package com.skillspace.sgs.common.controlleradvice;

import java.util.List;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.skillspace.sgs.admin.category.AdminCategoryService;
import com.skillspace.sgs.admin.category.CategoryTreeDTO;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice(basePackages = "com.skillspace.sgs.guest", 
				basePackageClasses = com.skillspace.sgs.HomeController.class)
public class GuestControllerAdvice {

	private final AdminCategoryService adminCategoryService;
	
	public GuestControllerAdvice(AdminCategoryService adminCategoryService) {
		this.adminCategoryService = adminCategoryService;
	}
	
	@ModelAttribute("categoryTreeByMenu")
	public List<CategoryTreeDTO> guestGlobalCategories(HttpServletRequest request){
		if (isAjaxRequest(request)) {
			return null;
		}
		return adminCategoryService.getAllCategoryTree();
	}

	/**
	 * 요청이 AJAX 요청인지 확인합니다.
	 * (X-Requested-With 헤더를 기준으로 판단)
	 */
	private boolean isAjaxRequest(HttpServletRequest request) {
		return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
	}
}
