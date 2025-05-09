package com.skillspace.sgs.guest.question;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
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

	// 질문 수정
	@PostMapping("/modify")
	public ResponseEntity<String> modifyQuestion(QuestionDTO questionDTO, HttpSession session) {

		GuestDTO loginUser = (GuestDTO) session.getAttribute("login_auth");
		if (loginUser == null) {
			log.warn("modifyQuestion - User not logged in.");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("login_required");
		}
		String sessionUserId = loginUser.getUser_id();
		log.info("modifyQuestion - Session User ID: {}", sessionUserId);

		String result = guestQuestionService.modifyQuestion(questionDTO, sessionUserId);

		if ("success".equals(result)) {
			return ResponseEntity.ok("success");
		} else if ("auth_fail".equals(result)) {
			log.warn("modifyQuestion - 수정 권한이 없습니다. question_id: {}", questionDTO.getQuestion_id());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
		} else if ("not_found".equals(result)) {
			log.warn("modifyQuestion - 질문을 찾을 수 없습니다. question_id: {}", questionDTO.getQuestion_id());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
		} else if ("answered".equals(result)) {
			log.warn("modifyQuestion - 이미 답변이 완료된 질문입니다. question_id: {}", questionDTO.getQuestion_id());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
		} else {
			// "fail" 또는 기타 예상치 못한 결과
			log.error("modifyQuestion - Failed for question_id: {}. Reason: {}", questionDTO.getQuestion_id(), result);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("fail"); 
		}
	}

	// 질문 삭제
	@PostMapping("/delete")
	public ResponseEntity<String> deleteQuestion(
				Integer question_id, 
				HttpSession session) {

		GuestDTO loginUser = (GuestDTO) session.getAttribute("login_auth");
		if (loginUser == null) {
			log.warn("modifyQuestion - User not logged in.");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("login_required");
		}
		String sessionUserId = loginUser.getUser_id();
		log.info("modifyQuestion - Session User ID: {}", sessionUserId);

		String result = guestQuestionService.deleteQuestion(question_id, sessionUserId);

		if ("success".equals(result)) {
			return ResponseEntity.ok("success");
		} else if ("auth_fail".equals(result)) {
			log.warn("deleteQuestion - 삭제 권한이 없습니다. question_id: {}", question_id);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
		} else if ("not_found".equals(result)) {
			log.warn("deleteQuestion - 질문을 찾을 수 없습니다. question_id: {}", question_id);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
		} else {
			// "fail" 또는 기타 예상치 못한 결과
			log.error("deleteQuestion - Failed for question_id: {}. Reason: {}", question_id, result);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("fail"); 
		}
		

		

	}

}
