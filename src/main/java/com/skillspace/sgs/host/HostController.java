package com.skillspace.sgs.host;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.skillspace.sgs.guest.GuestDTO;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/host")
@Controller
public class HostController {
	
	private final HostService hostService;
	
	// 호스트 회원가입 폼
	@GetMapping("/join")
	public void join() {
		
	}
	
	// 호스트 회원가입 처리
	@PostMapping("/join")
	public String join(HostDTO dto, HttpSession session) throws Exception {
		
		log.info("호스트 정보 : " + dto);
		
		// 게스트 아이디 조회
		String user_id = ((GuestDTO)session.getAttribute("login_auth")).getUser_id();
		dto.setUser_id(user_id);
		
		// 호스트 회원가입 처리
		hostService.join(dto);
		
		return "redriect:/";
	}
	
	@GetMapping("/management")
	public void management() {
		
	}
	
	

}
