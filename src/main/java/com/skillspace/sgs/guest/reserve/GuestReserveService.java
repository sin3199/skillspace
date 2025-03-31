package com.skillspace.sgs.guest.reserve;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skillspace.sgs.guest.payment.GuestPaymentMapper;
import com.skillspace.sgs.guest.payment.PaymentDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class GuestReserveService {

    private final GuestReserveMapper guestReserveMapper;
    private final GuestPaymentMapper guestPaymentMapper;

    
    // 예약 처리
    @Transactional
    public void reservation_process(ReservationDTO dto, String payment_method) {

        // 1) Reverations DB 저장
		dto.setStatus("대기");
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

}
