package com.skillspace.sgs.guest.reserve;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReserveDetailDTO {

	private Integer product_id;
	private String reserveDate;
	private String reserveTime;
	private Integer headcount;
	private BigDecimal total_payment;
	
}
