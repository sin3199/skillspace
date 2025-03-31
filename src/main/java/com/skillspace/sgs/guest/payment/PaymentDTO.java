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
    private BigDecimal      amounts;
    private String          status;
    private LocalDateTime   created_at;
    private LocalDateTime   updated_at;
}
