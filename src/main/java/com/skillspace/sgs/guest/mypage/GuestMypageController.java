package com.skillspace.sgs.guest.mypage;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.skillspace.sgs.common.utils.PageMaker;
import com.skillspace.sgs.common.utils.SearchCriteria;
import com.skillspace.sgs.guest.GuestDTO;
import com.skillspace.sgs.guest.GuestService;
import com.skillspace.sgs.guest.question.GuestQuestionService;
import com.skillspace.sgs.guest.reserve.GuestReserveService;

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
	private final GuestReserveService guestReserveService;
    

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

	@GetMapping("/reservations")
	public void reservations(
							SearchCriteria cri, 
							HttpSession session, 
							Model model) {

		// 1) 로그인 유저 조회
		String user_id = ((GuestDTO)session.getAttribute("login_auth")).getUser_id();

		// 2) 페이지메이커
		PageMaker pageMaker = new PageMaker();
		if(cri.getPerPageNum() == 0) {
			cri.setPerPageNum(3);
		}
		pageMaker.setDisplayPageNum(5);
		pageMaker.setCri(cri);
		pageMaker.setTotalCount(guestReserveService.getCountReservationListByUserId(user_id));

		// 3) 예약 목록 조회
		List<Map<String, Object>> reservationList = guestReserveService.getReservationListByUserId(user_id, cri);

		// reservationList.reservation_date 의 자료형을 LocalDate로 변경
		for(Map<String, Object> reservation : reservationList) {
			if(reservation.get("reservation_date") instanceof java.sql.Date) {
				java.sql.Date sqlDate = (java.sql.Date) reservation.get("reservation_date");
				reservation.put("reservation_date", sqlDate.toLocalDate());
			}
		}
		
		model.addAttribute("reservationList", reservationList);
		model.addAttribute("pageMaker", pageMaker);

		
	}

	// 예약 취소
	@PostMapping("/reservations/cancel/{reservation_id}")
	public String cancelReservation(@PathVariable("reservation_id") Integer reservation_id,
						HttpSession session,
						RedirectAttributes rttr) {

		log.info("예약 취소 요청 수신 - 예약 ID: {}", reservation_id);

		// 0) 로그인 사용자 정보 확인 (필수!)
		GuestDTO loginUser = (GuestDTO) session.getAttribute("login_auth");
		if (loginUser == null) {
			log.warn("로그인되지 않은 사용자의 예약 취소 시도.");
			rttr.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
			// 실제 로그인 페이지 경로로 수정하세요. 예: "redirect:/member/login"
			return "redirect:/login";
		}
		String loggedInUserId = loginUser.getUser_id();
		log.debug("요청 사용자 ID: {}", loggedInUserId);

		try {
			// 서비스 계층에 예약 취소 로직 위임
			guestReserveService.cancelReservation(reservation_id, loggedInUserId);

			// 성공 메시지 설정
			rttr.addFlashAttribute("successMessage", "예약이 성공적으로 취소되었습니다.");
			log.info("사용자 '{}'에 의해 예약 ID '{}' 취소 성공", loggedInUserId, reservation_id);

		} catch (NoSuchElementException e) {
			// 예약 정보를 찾을 수 없는 경우
			log.warn("존재하지 않는 예약 ID '{}' 취소 시도 (사용자: '{}')", reservation_id, loggedInUserId, e);
			rttr.addFlashAttribute("errorMessage", "취소할 예약 정보를 찾을 수 없습니다.");
		} catch (IllegalArgumentException e) {
			// 예약자와 로그인 사용자가 다른 경우 (권한 없음)
			log.warn("사용자 '{}'의 예약 ID '{}' 무단 취소 시도", loggedInUserId, reservation_id, e);
			rttr.addFlashAttribute("errorMessage", "예약을 취소할 권한이 없습니다.");
		} catch (IllegalStateException e) {
			// 이미 취소되었거나 취소 불가능한 상태인 경우
			log.warn("취소 불가능한 예약 ID '{}' 취소 시도 (사용자: '{}', 사유: {})", reservation_id, loggedInUserId, e.getMessage(), e);
			rttr.addFlashAttribute("errorMessage", e.getMessage()); 
		} catch (RuntimeException e) {
			// 그 외 예상치 못한 오류 발생 시 (DB 오류 등)
			log.error("예약 ID '{}' 취소 처리 중 오류 발생 (사용자: '{}')", reservation_id, loggedInUserId, e);
			rttr.addFlashAttribute("errorMessage", "예약 취소 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
		}


		return "redirect:/guest/mypage/reservations";
	}
}
