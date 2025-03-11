package com.skillspace.sgs.guest.reserve;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@RequestMapping("/guest/reserve")
@RequiredArgsConstructor
@Controller
public class GuestReserveController {
	
	@GetMapping("/reserveDetail")
	public void reserveDetail(@ModelAttribute("reservDetailDTO") ReserveDetailDTO dto) {
		
	}
	
}
