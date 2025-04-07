package com.skillspace.sgs.guest.reserve;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.skillspace.sgs.guest.GuestDTO;
import com.skillspace.sgs.guest.GuestService;
import com.skillspace.sgs.host.product.HostProductDTO;
import com.skillspace.sgs.host.product.HostProductService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/guest/reserve")
@RequiredArgsConstructor
@Controller
public class GuestReserveController {
	
	private final GuestService guestService;
	private final HostProductService hostProductService;
	private final GuestReserveService guestReserveService;
	
	@GetMapping("/reserveDetail")
	public void reserveDetail(
			@ModelAttribute("reservDetailDTO") ReserveDetailDTO dto,
			HttpSession session,
			Model model) throws Exception {
		
		// 로그인 유저 전화번호, 이메일 정보
		String user_id = ((GuestDTO)session.getAttribute("login_auth")).getUser_id();
		GuestDTO phoneAndEmail = guestService.getPhoneAndEmailById(user_id);
		
		// 예약 공간 정보
		HostProductDTO productDTO = hostProductService.getProductWithImagesById(dto.getProduct_id());
		
		model.addAttribute("phoneAndEmail", phoneAndEmail);
		model.addAttribute("productDTO", productDTO);
	}
	
	@PostMapping("reservation")
	public String reservation(
			ReservationDTO dto,
			String payment_method, String account_transfer, String sender,
			HttpSession session,
			RedirectAttributes rttr) throws Exception {
		
		// 예약 처리
		dto.setUser_id(((GuestDTO)session.getAttribute("login_auth")).getUser_id());
		payment_method = payment_method + "/" + account_transfer + "/" + sender;
		
		guestReserveService.reservation_process(dto, payment_method);

		log.info("예약 dto : " + dto);
		log.info("payment_method : " + payment_method);

		rttr.addAttribute("reservation_id", dto.getReservation_id());
		

		return "redirect:/guest/reserve/reserve_result";
	}

	@GetMapping("reserve_result")
	public void reserve_result(Integer reservation_id, Model model) throws Exception {
		
		log.info("예약결과에서 reservation_id : {}", reservation_id);
		// 예약정보 조회
		Map<String, Object> reservationResult = guestReserveService.getReservationResultByReservationId(reservation_id);
		model.addAttribute("reservationResult", reservationResult);
	}
	
}
