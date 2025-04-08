package com.skillspace.sgs.guest.question;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.skillspace.sgs.guest.GuestDTO;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RequestMapping("/guest/question")
@Slf4j
@Controller
public class GuestQuestionController {
	
	private final GuestQuestionService guestQuestionService;
	
	// 질문 등록
	@PostMapping("/create")
	public ResponseEntity<String> createQuestion(QuestionDTO questionDTO, HttpSession session) {
		
		log.info("createQuestion : {}", questionDTO);
		String user_id = ((GuestDTO)session.getAttribute("login_auth")).getUser_id();
		if(!user_id.equals(questionDTO.getUser_id())) return ResponseEntity.ok("userID_fail");
		
		guestQuestionService.createQuestion(questionDTO);
		return ResponseEntity.ok("success");
	}
	 

}
