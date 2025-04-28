package com.skillspace.sgs.guest.reserve;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skillspace.sgs.common.utils.SearchCriteria;
import com.skillspace.sgs.guest.payment.GuestPaymentMapper;
import com.skillspace.sgs.guest.payment.PaymentDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class GuestReserveService {

    private final GuestReserveMapper guestReserveMapper;
    private final GuestPaymentMapper guestPaymentMapper;

    
    // 예약 처리
    @Transactional
    public void reservation_process(ReservationDTO dto, String payment_method) {

        // 1) Reverations DB 저장

        if(payment_method.contains("계좌이체")) {
            dto.setStatus("예약대기");
        }else {
            dto.setStatus("예약완료");
        }
        
        guestReserveMapper.reservation_create(dto);

        // 2) payments DB 저장
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setReservation_id(dto.getReservation_id());

        if(payment_method.contains("계좌이체")) {
            paymentDTO.setPayment_method(payment_method);
            paymentDTO.setStatus("입금대기");
        }else if (payment_method.equals("카카오페이")) {
            paymentDTO.setPayment_method("카카오페이");
            paymentDTO.setStatus("결제완료");
        }
        
        paymentDTO.setAmounts(dto.getTotal_payment());

        guestPaymentMapper.payment_create(paymentDTO);
            
    }

    // 예약 결과 조회
    Map<String, Object> getReservationResultByReservationId(Integer reservation_id) {
        return guestReserveMapper.getReservationResultByReservationId(reservation_id);
    }

    // 유저 아이디로 예약 목록 조회
    public List<Map<String, Object>> getReservationListByUserId(String user_id, SearchCriteria cri) {
        return guestReserveMapper.getReservationListByUserId(user_id, cri);
    }

    // 유저 아이디로 목록 개수 조회
    public int getCountReservationListByUserId(String user_id) {
        return guestReserveMapper.getCountReservationListByUserId(user_id);
    }


    // 예약 취소
    @Transactional
    public void cancelReservation(Integer reservation_id, String loggedInUserId) {
        log.info("예약 취소 서비스 시작 - 예약 ID: {}, 사용자 ID: {}", reservation_id, loggedInUserId);

		// 1) reservation_id 로 예약 정보 조회 (user_id, status, 예약 시작일 등 포함)
		// Mapper에 해당 메소드 필요 (findReservationForCancelById 등)
		ReservationDTO reservation = guestReserveMapper.getReservationById(reservation_id);

		// 1-1) 예약 정보 존재 여부 확인
		if (reservation == null) {
			log.warn("취소할 예약 정보를 찾을 수 없음 - 예약 ID: {}", reservation_id);
			throw new NoSuchElementException("취소할 예약 정보를 찾을 수 없습니다.");
		}

		// 1-2) 예약자와 로그인 사용자 일치 여부 확인
		if (!reservation.getUser_id().equals(loggedInUserId)) {
			log.warn("예약 취소 권한 없음 - 예약 ID: {}, 예약자: {}, 요청자: {}", reservation_id, reservation.getUser_id(), loggedInUserId);
			throw new IllegalArgumentException("예약을 취소할 권한이 없습니다.");
		}

		// 1-3) 취소 가능 상태 및 조건 확인 (추가 조언)
		String currentStatus = reservation.getStatus(); // DB에서 가져온 현재 상태
		if ("예약취소".equals(currentStatus)) {
			log.warn("이미 취소된 예약 - 예약 ID: {}", reservation_id);
			throw new IllegalStateException("이미 취소된 예약입니다.");
		}
		if ("이용완료".equals(currentStatus)) { // 예시: 이용완료 상태는 취소 불가
			log.warn("이용 완료된 예약은 취소 불가 - 예약 ID: {}", reservation_id);
			throw new IllegalStateException("이용이 완료된 예약은 취소할 수 없습니다.");
		}
		// --- 날짜 기반 취소 가능 여부 확인 (예시: 이용 시작일 전날까지만 가능) ---
		if (reservation.getReservation_date() != null && reservation.getReservation_date().isBefore(LocalDate.now().plusDays(1))) {
		    log.warn("이용일 임박으로 취소 불가 - 예약 ID: {}, 이용 시작일: {}", reservation_id, reservation.getReservation_date());
		    throw new IllegalStateException("이용일이 임박하여 예약을 취소할 수 없습니다.");
		}

		// 2) reservations 테이블의 status 를 '예약취소'로 변경
		int updatedReservationRows = guestReserveMapper.updateReservationStatus(reservation_id, "예약취소");

		if (updatedReservationRows == 0) {
			log.error("예약 상태 변경 실패 - 예약 ID: {}", reservation_id);
			throw new RuntimeException("예약 상태를 변경하는 중 오류가 발생했습니다.");
		}
		log.info("예약 상태 '예약취소'로 변경 완료 - 예약 ID: {}", reservation_id);

		// 3) payments 테이블의 reservation_id 로 결제 정보 조회
		PaymentDTO payment = guestPaymentMapper.getPaymentByReservationId(reservation_id);

		if (payment != null) {
			log.debug("조회된 결제 정보: {}", payment);
            String status = "";
			// 3-1) 결제 상태가 '결제완료' 인 경우 '결제취소'로 변경
			if ("결제완료".equals(payment.getStatus())) {
                if(payment.getPayment_method().contains("계좌이체")) status = "환불대기";
                status = "결제취소";
			} 
            int updatedPaymentRows = guestPaymentMapper.updatePaymentStatus(payment.getPayment_id(), status);

            if (updatedPaymentRows == 0) {
                log.error("결제 상태 변경 실패 - 결제 ID: {}, 예약 ID: {}", payment.getPayment_id(), reservation_id);
                throw new RuntimeException("결제 상태를 변경하는 중 오류가 발생했습니다.");
            }
            log.info("결제 상태 '{}'로 변경 완료 - 결제 ID: {}, 예약 ID: {}",status, payment.getPayment_id(), reservation_id);

            if ("결제완료".equals(payment.getStatus())) {
                // 3-2) !!! 실제 구현시 환불 처리 로직 !!!
                log.info("실제 환불 처리 로직 호출 필요 - 결제 ID: {}", payment.getPayment_id());
            }
		} else {
			log.info("해당 예약 ID '{}'에 대한 결제 정보 없음", reservation_id);
		}

		log.info("예약 취소 서비스 성공 종료 - 예약 ID: {}", reservation_id);
    }
    

}
