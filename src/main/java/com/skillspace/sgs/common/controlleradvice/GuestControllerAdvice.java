package com.skillspace.sgs.common.controlleradvice;

import java.util.List;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.skillspace.sgs.admin.category.AdminCategoryService;
import com.skillspace.sgs.admin.category.CategoryTreeDTO;

@ControllerAdvice(basePackages = "com.skillspace.sgs.guest", 
				basePackageClasses = com.skillspace.sgs.HomeController.class)
public class GuestControllerAdvice {

	private final AdminCategoryService adminCategoryService;
	
	public GuestControllerAdvice(AdminCategoryService adminCategoryService) {
		this.adminCategoryService = adminCategoryService;
	}
	
	@ModelAttribute("categoryTreeByMenu")
	public List<CategoryTreeDTO> guestGlobalCategories(){
		return adminCategoryService.getAllCategoryTree();
	}
}
