package com.skillspace.sgs.guest.product;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/guest/product/*")
@Controller
public class GuestProductController {
	
	@GetMapping("/pro_list")
	public void pro_list(Integer cate_id, Model model) throws Exception {
		
		// cate_id 에 해당되는 상품목록 
		model.addAttribute("cate_id", cate_id);
	}

}
