package com.skillspace.sgs.guest.mypage;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.skillspace.sgs.common.utils.PageMaker;
import com.skillspace.sgs.common.utils.SearchCriteria;
import com.skillspace.sgs.guest.GuestDTO;
import com.skillspace.sgs.guest.GuestService;
import com.skillspace.sgs.guest.question.GuestQuestionService;
import com.skillspace.sgs.guest.question.QuestionDTO;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RequestMapping("/guest/mypage")
@Slf4j
@Controller
public class GuestMypageController {

    private final GuestService guestService;
    private final PasswordEncoder passwordEncoder;
    private final GuestQuestionService guestQuestionService;
    

    // 회원 정보 수정 폼
	@GetMapping("/modify")
	public void modify(Model model, HttpSession session) {
		
		GuestDTO dto = guestService.modify(((GuestDTO)session.getAttribute("login_auth")).getUser_id());
		model.addAttribute("guestDTO", dto);
	}
	
	// 회원 정보 수정 처리
	@PostMapping("/modify")
	public String modify_save(GuestDTO dto) throws Exception {
		
		guestService.modify_save(dto);
		
		return "redirect:/guest/modify";
	}

    // 내 Q&A 목록 폼
    @GetMapping("/question")
    public void question(HttpSession session, SearchCriteria cri, Model model) throws Exception {

        String user_id = ((GuestDTO)session.getAttribute("login_auth")).getUser_id();
        
        if(cri.getPerPageNum() == 0) {			
            cri.setPerPageNum(5); 	// 나중에 상수값으로 페이지 관리 
        }
        PageMaker pageMaker = new PageMaker();
        pageMaker.setDisplayPageNum(3);
        pageMaker.setCri(cri);
        pageMaker.setTotalCount(guestQuestionService.getCountQuestionListByUserId(user_id));
        
        List<QnaWithSpaceDTO> questionList = guestQuestionService.getQuestionListByUserId(user_id, cri);
        
        model.addAttribute("questionList", questionList);
        model.addAttribute("pageMaker", pageMaker);
    }

    @GetMapping("/pwchange")
	public void pwchange() {
		
	}
	
	@PostMapping("/pwchange")
	public ResponseEntity<String> pwchange(@RequestParam("cur_pw") String user_pw, 
											String new_pw, HttpSession session) {
		
		log.info("user_pw : " + user_pw + " new_pw : " + new_pw); 
		ResponseEntity<String> entity = null;
		String result = "";
		GuestDTO db_dto = (GuestDTO)session.getAttribute("login_auth");
		
		if(passwordEncoder.matches(user_pw, db_dto.getUser_pw())) {
			// 신규 비밀번호 60바이트 암호화.
			String encoding_new_pw = passwordEncoder.encode(new_pw);
			
			// 암호화 된 비밀번호 db 변경
			guestService.pwchange(db_dto.getUser_id(), encoding_new_pw);
			
			db_dto.setUser_pw(encoding_new_pw);
			session.setAttribute("login_auth", db_dto);
			
			result = "success";
		}else result = "fail";
		
		entity = new ResponseEntity<String>(result, HttpStatus.OK);
		
		return entity;
	}
}
