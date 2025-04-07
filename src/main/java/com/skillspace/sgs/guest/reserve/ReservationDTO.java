package com.skillspace.sgs.guest.reserve;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReservationDTO {
	
	private Integer 		reservation_id; 	// 예약 ID
    private String 			user_id; 			// 게스트 회원 아이디
    private Integer 		product_id; 		// 상품 ID
    private BigDecimal 		total_payment;		// 총 결제 금액
    private String 			status; 			// 예약 상태 (Pending, Completed, Cancelled)
    private LocalDate 		reservation_date; 	// 예약 날짜
    private LocalTime 		start_time; 		// 시작 시간
    private LocalTime 		end_time; 			// 종료 시간
    private Integer 		headcount; 			// 예약 인원 수
    private String 			reservation_name; 	// 예약자 이름
    private String 			reservation_phone; 	// 예약자 전화번호
    private String 			reservation_email; 	// 예약자 이메일
    private String 			is_review; 			// 리뷰 작성 여부 ('Y' 또는 'N')
    private LocalDateTime 	created_at; 		// 생성 날짜
    private LocalDateTime 	updated_at; 		// 수정 날짜
    
}
