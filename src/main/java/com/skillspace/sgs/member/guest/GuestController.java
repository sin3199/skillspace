package com.skillspace.sgs.member.guest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/guest/*")
@Controller
public class GuestController {
	
	private final GuestService guestService;
	
	// 비밀번호 암호화 기능.
	private final PasswordEncoder passwordEncoder;
	
	// 회원가입 폼
	@GetMapping("/join")
	public void join() {}
	
	// 회원가입 처리
	@PostMapping("/join")
	public String join(GuestVO vo) {
		
		log.info("회원정보 비밀번호 암호화 전 : " + vo);
		// 비밀번호 암호화
		vo.setUser_pw(passwordEncoder.encode(vo.getUser_pw()));
		
//		log.info("회원정보 비밀번호 암호화 후 : " + vo);
		
		// DB 저장
		guestService.join(vo);
		
		return "redirect:/guest/login";
	}
	
	
	// 아이디 중복 체크
	@GetMapping("/idCheck")
	public ResponseEntity<String> idCheck(String user_id) throws Exception {
		ResponseEntity<String> entity = null;
		
		String isUse = "";
		
		if(guestService.idCheck(user_id) != null) {
			isUse = "no";	// 아이디 사용 불가능
		}else {
			isUse = "yes";	// 아이디 사용가능
		}
		
		entity = new ResponseEntity<String>(isUse, HttpStatus.OK);
		
		return entity;
	}
	
	// 닉네임 중복체크
	@GetMapping("/nickCheck")
	public ResponseEntity<String> nickCheck(String user_nick) throws Exception {
		ResponseEntity<String> entity = null;
		
		String isUse = "";
		
		if(guestService.nickCheck(user_nick) != null) {
			isUse = "no";	// 닉네임 사용 불가능
		}else {
			isUse = "yes";	// 닉네임 사용가능
		}
		
		entity = new ResponseEntity<String>(isUse, HttpStatus.OK);
		
		return entity;
	}
	
	// 로그인 폼
	@GetMapping("/login")
	public void login() {}
	
	// 로그인 처리
	@PostMapping("/login")
	public String loginProcess(LoginDTO dto, HttpSession session, RedirectAttributes rttr) throws Exception {
		
		String url = "";
		String status = "";
		GuestVO vo = guestService.login(dto.getUser_id());
		
		if(vo != null) {	// 아이디가 일치하면
			if(passwordEncoder.matches(dto.getUser_pw(), vo.getUser_pw())) {	// 비밀번호 일치하면
				session.setAttribute("login_auth", vo);
				url = "/";
			}else {	// 비밀번호가 틀릴 경우
				url = "/guest/login";
				status = "pwFail";
			}
		}else {	// 아이디가 틀릴 경우
			url = "/guest/login";
			status = "idFail";
		}
		
		rttr.addFlashAttribute("status", status);
		
		return "redirect:" + url;
	}
	
	// 로그아웃
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		
		return "redirect:/";
	}
	
	// 아이디 찾기 폼
	@GetMapping("/idsearch")
	public void idSearch() {}
	
	// 아이디 찾기 처리
	@GetMapping("/idsearch-ok")
	public ResponseEntity<String> idsearch_ok(String user_name, String user_email){
		
		ResponseEntity<String> entity = null;
		String user_id = guestService.idsearch(user_name, user_email);
		
		entity = new ResponseEntity<String>(user_id, HttpStatus.OK);
		
		return entity;
	}
	
	@GetMapping("/pwsearch")
	public void pwSearch() {}
	
	
}
