package com.skillspace.sgs.guest.reserve;

import java.util.Map;

public interface GuestReserveMapper {

    // 예약 생성
    void reservation_create(ReservationDTO dto);

    // 예약 결과 조회
    Map<String, Object> getReservationResultByReservationId(Integer reservation_id);
    


}
