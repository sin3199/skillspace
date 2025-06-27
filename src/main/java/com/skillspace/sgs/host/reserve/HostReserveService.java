package com.skillspace.sgs.host.reserve;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.skillspace.sgs.common.utils.SearchCriteria;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class HostReserveService {

    private final HostReserveMapper hostReserveMapper;

    // 유저 아이디로 조회하는 예약 개수
    public int getCountReservationListByUserId(String user_id, SearchCriteria cri) {
        return hostReserveMapper.getCountReservationListByUserId(user_id, cri);
    }

    // 유저 아이디로 조회하는 예약 목록
    public List<ReservManageDTO> getReservationListByUserId(String user_id, SearchCriteria cri) {
        return hostReserveMapper.getReservationListByUserId(user_id, cri);
    }

    // 유저 아이디로 조회하는 예약대기 목록
    public List<Map<String, Object>> getReservationWaitListByUserId(String user_id, SearchCriteria cri) {
        return hostReserveMapper.getReservationWaitListByUserId(user_id, cri);
    }

    // 유저 아이디로 조회하는 예약대기 개수
    public int getCountReservationWaitListByUserId(String user_id, SearchCriteria cri) {
        return hostReserveMapper.getCountReservationWaitListByUserId(user_id, cri);
    }

    // 예약 ID로 공간 소유자 유저 조회
    public String getReservationSpaceOwnerUserId(Integer reservation_id) {
        return hostReserveMapper.getReservationSpaceOwnerUserId(reservation_id);

    }

    // 예약 취소
    public boolean deleteReservation(Integer reservation_id) {
        return hostReserveMapper.deleteReservation(reservation_id);
    }

    // 여러개 예약 취소
    public void deleteReservationByIdsAndOwner(List<Integer> selectedIds, String loggedInUserId) {
        hostReserveMapper.deleteReservationByIdsAndOwner(selectedIds, loggedInUserId);
    }

    // 예약상태 변경
    public boolean updateReservationStatus(Integer reservation_id, String status) {
        return hostReserveMapper.updateReservationStatus(reservation_id, status);
    }
}
