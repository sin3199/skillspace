package com.skillspace.sgs.guest.mypage;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.skillspace.sgs.common.utils.PageMaker;
import com.skillspace.sgs.common.utils.SearchCriteria;
import com.skillspace.sgs.guest.GuestDTO;
import com.skillspace.sgs.guest.GuestService;
import com.skillspace.sgs.guest.question.GuestQuestionService;
import com.skillspace.sgs.guest.reserve.GuestReserveService;
import com.skillspace.sgs.guest.review.GuestReviewService;
import com.skillspace.sgs.guest.review.ReviewDTO;
import com.skillspace.sgs.guest.review.ReviewWithImageDTO;

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
	private final GuestReviewService guestReviewService;
    

    // 회원 정보 수정 폼
	@GetMapping("/modify")
	public void modify(
			Model model, 
			HttpSession session) {
		
		GuestDTO dto = guestService.modify(((GuestDTO)session.getAttribute("login_auth")).getUser_id());
		model.addAttribute("guestDTO", dto);
	}
	
	// 회원 정보 수정 처리
	@PostMapping("/modify")
	public String modify_save(
			GuestDTO dto) throws Exception {
		
		guestService.modify_save(dto);
		
		return "redirect:/guest/modify";
	}

    // 내 Q&A 목록 폼
    @GetMapping("/question")
    public void question(
			HttpSession session, 
			SearchCriteria cri, 
			Model model) throws Exception {

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
	public ResponseEntity<String> pwchange(
			@RequestParam("cur_pw") String user_pw, 
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
			cri.setPerPageNum(5);
		}
		pageMaker.setDisplayPageNum(3);
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
	public String cancelReservation(
			@PathVariable("reservation_id") Integer reservation_id,
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

	// 리뷰쓰기
	@PostMapping("/reviews/write")
	public ResponseEntity<?> writeReview(
			@ModelAttribute("reviewDTO") ReviewDTO reviewDTO,
			@RequestParam(value = "images", required = false) List<MultipartFile> imageFiles,
			HttpSession session) {

		log.info("리뷰 작성 요청 수신 (MyBatis): {}", reviewDTO);
		// imageFiles 로그 출력
		if (imageFiles != null) {
			for (MultipartFile image : imageFiles) {
				log.info("이미지 파일 이름: {}, 크기: {}", image.getOriginalFilename(), image.getSize());
			}
		}

		// 로그인 유저 정보
		GuestDTO loginUser = (GuestDTO) session.getAttribute("login_auth");
		if (loginUser == null) {
			log.warn("로그인되지 않은 사용자의 리뷰 작성 시도.");
			return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
		}
		String loggedInUserId = loginUser.getUser_id();
		log.debug("요청 사용자 ID: {}", loggedInUserId);

		if (reviewDTO.getReservation_id() == null || reviewDTO.getRating() < 1 || reviewDTO.getRating() > 5 || reviewDTO.getReview_text() == null || reviewDTO.getReview_text().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "별점과 내용은 필수 입력 항목입니다."));
        }
        if (imageFiles != null && imageFiles.size() > 3) {
            return ResponseEntity.badRequest().body(Map.of("message", "이미지는 최대 3개까지만 첨부할 수 있습니다."));
        }

		try {
            guestReviewService.createReview(reviewDTO, imageFiles, loggedInUserId);

            log.info("사용자 '{}'의 예약 ID '{}'에 대한 리뷰 등록 성공 (MyBatis)", loggedInUserId, reviewDTO.getReservation_id());
            return ResponseEntity.ok(Map.of("message", "리뷰가 성공적으로 등록되었습니다."));

        } catch (NoSuchElementException | IllegalArgumentException | IllegalStateException e) {
            // 서비스에서 발생시킨 예외 처리 (이전 답변과 동일)
            log.warn("리뷰 등록 실패 (MyBatis): {}", e.getMessage());
            // 상태 코드 조정 가능
            if (e instanceof NoSuchElementException) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
            } else if (e instanceof IllegalArgumentException) {
				return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
            } else { // IllegalStateException
				return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
            }
        } catch (IOException e) {
            log.error("리뷰 이미지 파일 저장 중 오류 발생 (MyBatis, 예약 ID: {})", reviewDTO.getReservation_id(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "이미지 파일 저장 중 오류가 발생했습니다."));
        } catch (Exception e) {
            log.error("리뷰 등록 중 예상치 못한 오류 발생 (MyBatis, 예약 ID: {})", reviewDTO.getReservation_id(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "리뷰 등록 중 오류가 발생했습니다."));
        }

	}

	// 마이페이지 내 이용후기
	@GetMapping("/reviews")
	public void reviews(
			SearchCriteria cri,
			Model model, 
			HttpSession session) {

		// 1) 로그인 유저 정보
		String user_id = ((GuestDTO)session.getAttribute("login_auth")).getUser_id();

		// 2) 페이지메이커
		PageMaker pageMaker = new PageMaker();
		if(cri.getPerPageNum() == 0) {
			cri.setPerPageNum(5);
		}
		pageMaker.setDisplayPageNum(3);
		pageMaker.setCri(cri);
		
		// 3) 리뷰 목록 조회
		try {
			pageMaker.setTotalCount(guestReviewService.getCountReviewListByUserId(user_id));
			List<ReviewWithImageDTO> reviewList = guestReviewService.getReviewListByUserId(user_id, cri);
			model.addAttribute("reviewList", reviewList);
			model.addAttribute("pageMaker", pageMaker);
		} catch (Exception e) {
			log.error("사용자 {}의 리뷰 목록 조회 중 오류 발생", user_id, e);
			model.addAttribute("errorMessage", "리뷰 목록을 불러오는 중 오류가 발생했습니다.");
		}

	}

	// 리뷰 수정
	@PostMapping("/reviews/modify")
	public ResponseEntity<String> modifyReview(
			ReviewDTO dto,
			@RequestParam(value = "new_images", required = false) List<MultipartFile> new_images,
			@RequestParam(value = "delete_image_ids[]", required = false) List<Integer> delete_image_ids,
			HttpSession session) {

		// 넘어온 데이터 출력
		log.info("dto : " + dto);
		log.info("new_images 개수: " + (new_images != null ? new_images.size() : 0));
		log.info("delete_image_ids : " + delete_image_ids);

		GuestDTO loginUser = (GuestDTO) session.getAttribute("login_auth");
		if (loginUser == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
		}
		String loggedInUserId = loginUser.getUser_id();

		try {
			guestReviewService.modifyReview(dto, new_images, delete_image_ids, loggedInUserId); 
			return ResponseEntity.ok("success");
		} catch (NoSuchElementException e) {
			log.warn("리뷰 수정 실패: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (IllegalArgumentException e) {
			log.warn("리뷰 수정 실패: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage()); // 권한 없음
		} catch (IOException e) {
			log.error("리뷰 수정 중 파일 처리 오류 발생, Review ID: {}", dto.getReview_id(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 처리 중 오류가 발생했습니다.");
		} catch (Exception e) {
			log.error("리뷰 수정 중 예상치 못한 오류 발생, Review ID: {}", dto.getReview_id(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("리뷰 수정 중 오류가 발생했습니다.");
		}

	}

	// 리뷰 삭제
	@DeleteMapping("/reviews/{review_id}")
	public ResponseEntity<String> deleteReview(
			@PathVariable("review_id") Integer review_id,
			HttpSession session) {

		log.info("삭제할 리뷰아이디: {}", review_id);

		GuestDTO loginUser = (GuestDTO) session.getAttribute("login_auth");
		if (loginUser == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
		}
		String loggedInUserId = loginUser.getUser_id();

		try {
			guestReviewService.deleteReview(review_id, loggedInUserId);
		} catch (NoSuchElementException e) {
			log.warn("리뷰 삭제 실패: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		} catch (IllegalArgumentException e) {
			log.warn("리뷰 삭제 실패: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage()); // 권한 없음
		} catch (Exception e) {
			log.error("리뷰 삭제 중 예상치 못한 오류 발생, Review ID: {}", review_id, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("리뷰 삭제 중 오류가 발생했습니다.");
		}
		return ResponseEntity.ok("success");
	}

	
}
