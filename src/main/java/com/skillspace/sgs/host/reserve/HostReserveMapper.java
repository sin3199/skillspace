package com.skillspace.sgs.host.reserve;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.skillspace.sgs.common.utils.SearchCriteria;

public interface HostReserveMapper {

    // 유저 아이디로 조회하는 예약 개수
    int getCountReservationListByUserId(
                @Param("user_id") String user_id, 
                @Param("cri") SearchCriteria cri);

    // 유저 아이디로 조회하는 예약 목록
    List<ReservManageDTO> getReservationListByUserId(
                @Param("user_id") String user_id, 
                @Param("cri") SearchCriteria cri);

    // 유저 아이디로 조회하는 예약대기 목록
    List<Map<String, Object>> getReservationWaitListByUserId(
                @Param("user_id") String user_id, 
                @Param("cri") SearchCriteria cri);

    // 유저 아이디로 조회하는 예약대기 개수
    int getCountReservationWaitListByUserId(
                @Param("user_id") String user_id, 
                @Param("cri") SearchCriteria cri);

    // 예약 ID로 공간 소유자 유저 조회
    String getReservationSpaceOwnerUserId(Integer reservation_id);

    // 예약 취소
    boolean deleteReservation(Integer reservation_id) ;

    void deleteReservationByIdsAndOwner(
                @Param("selectedIds") List<Integer> selectedIds, 
                @Param("loggedInUserId") String loggedInUserId);

    // 예약상태 변경
    boolean updateReservationStatus(Integer reservation_id, String status);

}
