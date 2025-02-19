package com.skillspace.sgs.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/admin/*")
@RequiredArgsConstructor
@Controller
public class AdminController {

	private final AdminService adminService;
	
	@GetMapping("/")
	public String admin_login() {
		return "/admin/admin_login";
	}
	
	@GetMapping("/admin-index")
	public void admin_index() {
	}
	
}
