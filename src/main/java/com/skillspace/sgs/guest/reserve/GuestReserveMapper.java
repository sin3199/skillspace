package com.skillspace.sgs.guest.reserve;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.skillspace.sgs.common.utils.SearchCriteria;

public interface GuestReserveMapper {

    // 예약 생성
    void reservation_create(ReservationDTO dto);

    // 예약 결과 조회
    Map<String, Object> getReservationResultByReservationId(Integer reservation_id);

    // 유저 아이디로 예약 목록 조회
    List<Map<String, Object>> getReservationListByUserId(
                            @Param("user_id") String user_id, 
                            @Param("cri")SearchCriteria cri);

    // 유저 아이디로 목록 개수 조회
    int getCountReservationListByUserId(String user_id);

    // 예약 아이디로 예약 조회
    ReservationDTO getReservationById(Integer reservation_id);
    
    
    // 예약 상태 변경
    int updateReservationStatus(
        @Param("reservation_id") Integer reservation_id,
        @Param("status") String status
    );

    // 리뷰작성 상태 변경
    int updateReviewStatus(
        @Param("reservation_id") Integer reservation_id,
        @Param("is_review") String is_review
    );

    


}
