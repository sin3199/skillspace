package com.skillspace.sgs.kakaopay;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.skillspace.sgs.guest.GuestDTO;
import com.skillspace.sgs.guest.reserve.GuestReserveService;
import com.skillspace.sgs.guest.reserve.ReservationDTO;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/kakao/")
@RequiredArgsConstructor
@Controller
public class KakaopayController {
	
	private final KakaopayService kakaopayService;
	private final GuestReserveService guestReserveService;

	private String user_id;
	private ReservationDTO reservationDTO;
	
	@PostMapping("/kakaoPay")
	public ResponseEntity<ReadyResponse> kakaoPay(
			ReservationDTO dto, String payment_method, String item_name, HttpSession session) {

		user_id = ((GuestDTO)session.getAttribute("login_auth")).getUser_id();

		dto.setUser_id(user_id);
		this.reservationDTO = dto;

		log.info("카카오페이 예약DTO : {}", dto);
		log.info("상품명 : {}", item_name);
		
		// 나중 가맹점 관리에서 구분해야 하므로, 회원아이디및주문날자 정보 조합사용.
		String partner_order_id = "skillspace[" + user_id + "] - " + new Date().toString();

		Integer order_total_price = dto.getTotal_payment().intValue();

		ReadyResponse readyResponse = kakaopayService.ready(partner_order_id, user_id, item_name, order_total_price, 0);

		return new ResponseEntity<ReadyResponse>(readyResponse, HttpStatus.OK);
	}

	@GetMapping("/approval")
	public String approval(String pg_token, RedirectAttributes rttr) {
		
		log.info("pg_token: " + pg_token);
		// 결제승인요청
		ApproveResponse response = kakaopayService.approve(pg_token);
		
		// 결제승인요청의 성공 응답파라미터로 aid를 확인
		if(response.getAid() != null) {
			//OrderService 파일에서 주문관련작업
			guestReserveService.reservation_process(this.reservationDTO, "카카오페이");
		}
		
		rttr.addAttribute("reservation_id", this.reservationDTO.getReservation_id());
		
		// /order/order_result?ord_code=주문번호
		return "redirect:/guest/reserve/reserve_result";
	}

	// 결제가 취소
	@GetMapping("/cancel")
	public String cancel() {
		
		return "/guest/reserve/reserve_cancel";
	}
	
	// 결제가 실패
	@GetMapping("/fail")
	public String fail() {
		
		return "/guest/reserve/reserve_fail";
	}
}
