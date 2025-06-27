package com.skillspace.sgs.guest.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PaymentDTO {
    private Integer         payment_id;
    private Integer         reservation_id;
    private String          payment_method;
    private BigDecimal      amount;
    private String          status;         // 결제완료, 입금대기, 결제취소, 환불대기, 환불완료
    private LocalDateTime   created_at;
    private LocalDateTime   updated_at;
}
