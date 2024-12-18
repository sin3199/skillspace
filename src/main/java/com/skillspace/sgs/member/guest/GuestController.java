package com.skillspace.sgs.member.guest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.skillspace.sgs.mail.EmailDTO;
import com.skillspace.sgs.mail.EmailService;

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
	// 이메일 기능
	private final EmailService emailService;
	
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
	
	// 회원 정보 수정 폼
	@GetMapping("/modify")
	public void modify(Model model, HttpSession session) {
		
		GuestVO vo = guestService.modify(((GuestVO)session.getAttribute("login_auth")).getUser_id());
		model.addAttribute("guestVO", vo);
	}
	
	// 회원 정보 수정 처리
	@PostMapping("/modify")
	public String modify_save(GuestVO vo) throws Exception {
		
		guestService.modify_save(vo);
		
		return "redirect:/guest/modify";
	}
	
	// 아이디 찾기 폼
	@GetMapping("/idsearch")
	public void idSearch() {}
	
	// 아이디 찾기 처리
	@GetMapping("/idsearch-ok")
	public ResponseEntity<String> idsearch_ok(String user_name, String user_email){
		
		ResponseEntity<String> entity = null;
		
		entity = new ResponseEntity<String>(guestService.idsearch(user_name, user_email), HttpStatus.OK);
		
		return entity;
	}
	
	// 비밀번호 찾기 폼
	@GetMapping("/pwsearch")
	public void pwSearch() {}
	
	// 임시 비밀번호 발급
	@GetMapping("/pwtemp")
	public ResponseEntity<String> pwtemp(String user_id, String user_email) {
		
		ResponseEntity<String> entity = null;
		
		String db_email = guestService.pwtemp_confirm(user_id, user_email);
		
		if(db_email != null) {
			
			String tmep_pw = emailService.createAuthCode();	// 8자리의 랜덤 조합
			guestService.pwchange(user_id, passwordEncoder.encode(tmep_pw));
			
			EmailDTO emailDTO = new EmailDTO();
			
			emailDTO.setReceiverMail(db_email);
			emailDTO.setSubject("SkillSpace 에서 임시 비밀번호를 보내드립니다.");
			
			emailService.sendMail("/mail/pwtemp", emailDTO, tmep_pw);
		}
		entity = new ResponseEntity<String>(db_email, HttpStatus.OK);
		
		return entity;
	}
	
	// 비밀번호 확인
	@PostMapping("/check-password")
	public ResponseEntity<String> checkPassword(String password, String user_pw){
		
		ResponseEntity<String> entity = null;
		String msg = "";
		
		if(passwordEncoder.matches(password, user_pw)) {
			msg = "success";
		}else {
			msg = "fail";
		}
		
		entity = new ResponseEntity<String>(msg, HttpStatus.OK);
		
		return entity;
	}
	
}
