package com.skillspace.sgs.admin.category;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("/admin/category/*")
@Slf4j
@RequiredArgsConstructor
@Controller
public class AdminCategoryController {
	
	private final AdminCategoryService adminCategoryService;
	
	// 카테고리 관리 페이지
	@GetMapping("admin-category")
	public void admin_category(Model model) {
		List<CategoryDTO> first_cate_list = adminCategoryService.getFirstCategory();
		
		model.addAttribute("first_cate_list", first_cate_list);
		
	}
	

	
	

}
