package com.skillspace.sgs.guest.payment;

public interface GuestPaymentMapper {

    // 결제 생성
    void payment_create(PaymentDTO paymentDTO);

}
