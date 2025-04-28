package com.skillspace.sgs.guest.payment;

import org.apache.ibatis.annotations.Param;

public interface GuestPaymentMapper {

    // 결제 생성
    void payment_create(PaymentDTO paymentDTO);

    // reservation_id 로 결제 정보 조회
    PaymentDTO getPaymentByReservationId(Integer reservation_id);

    // 결제상태 변경
    int updatePaymentStatus(
        @Param("payment_id")Integer payment_id, 
        @Param("status") String status);



}
