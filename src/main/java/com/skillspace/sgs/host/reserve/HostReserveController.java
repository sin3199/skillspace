package com.skillspace.sgs.host.reserve;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.skillspace.sgs.common.utils.PageMaker;
import com.skillspace.sgs.common.utils.SearchCriteria;
import com.skillspace.sgs.guest.GuestDTO;
import com.skillspace.sgs.host.space.HostSpaceDTO;
import com.skillspace.sgs.host.space.HostSpaceService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequiredArgsConstructor
@RequestMapping("/host/reserve")
@Controller
public class HostReserveController {

    private final HostReserveService hostReserveService;
    private final HostSpaceService hostSpaceService;

    private final String RESERVATION_LIST_URL = "redirect:/host/reserve/reservationWaitList";

    // 예약대기 관리
    @GetMapping("/reservationWaitList")
    public void reservationWait(
                @ModelAttribute("cri") SearchCriteria cri,
                Model model,
                HttpSession session) throws Exception {

    String user_id = ((GuestDTO)session.getAttribute("login_auth")).getUser_id();


    List<Map<String, Object>> reservationWaitList = hostReserveService.getReservationWaitListByUserId(user_id, cri);

    if(cri.getPerPageNum() == 0) {
        cri.setPerPageNum(5);
    }

    PageMaker pageMaker = new PageMaker();
    pageMaker.setDisplayPageNum(3);
    pageMaker.setCri(cri);
    pageMaker.setTotalCount(hostReserveService.getCountReservationWaitListByUserId(user_id, cri));

    model.addAttribute("reservationWaitList", reservationWaitList);
    model.addAttribute("pageMaker", pageMaker);

    }

    // 예약 취소
    @PostMapping("/deleteReservation")
    public String deleteReservation(
                Integer reservation_id,
                SearchCriteria cri,
                HttpSession session,
                RedirectAttributes rttr) throws Exception {

        // 1. 파라미터 및 세션 유효성 검사
        if (reservation_id == null || reservation_id <= 0) {
            rttr.addFlashAttribute("errorMessage", "잘못된 요청입니다.");
            addSearchAndPagingParams(cri, rttr);
            return RESERVATION_LIST_URL;
        }

        GuestDTO loginUser = (GuestDTO) session.getAttribute("login_auth");
        if (loginUser == null) {
            rttr.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/guest/login";
        }
        String loggedInUserId = loginUser.getUser_id();

        try {
            // 2. 공간 소유자 확인
            String ownerUserId = hostReserveService.getReservationSpaceOwnerUserId(reservation_id);

            // 3. 권한 비교 및 취소 처리
            if (ownerUserId != null && ownerUserId.equals(loggedInUserId)) {
                // 권한 있음 -> 취소 시도
                boolean deleted = hostReserveService.deleteReservation(reservation_id);
                if (deleted) {
                    rttr.addFlashAttribute("successMessage", "예약이 성공적으로 취소되었습니다.");
                    log.info("User '{}' deleted reservation_id: {}", loggedInUserId, reservation_id);
                } else {
                    // 취소 실패 (이미 삭제되었거나 DB 오류 등)
                    rttr.addFlashAttribute("errorMessage", "예약 취소 중 오류가 발생했습니다.");
                    log.warn("Failed to delete reservation_id: {} by user '{}'", reservation_id, loggedInUserId);
                }
            } else {
                // 권한 없음 또는 예약 없음
                rttr.addFlashAttribute("errorMessage", "취소 권한이 없거나 예약을 찾을 수 없습니다.");
                log.warn("Unauthorized delete attempt for reservation_id: {} by user '{}'. Owner is '{}'",
                         reservation_id, loggedInUserId, ownerUserId);
            }

        } catch (Exception e) {
            log.error("Error deleting reservation_id: {}", reservation_id, e);
            rttr.addFlashAttribute("errorMessage", "예약 취소 중 예외가 발생했습니다.");
        }

        // 4. 목록 페이지로 리다이렉트 (검색/페이징 조건 유지)
        addSearchAndPagingParams(cri, rttr);

        return RESERVATION_LIST_URL;
    }

    // 체크박스 선택 취소
    @PostMapping("/selectedDelete")
    @ResponseBody
    public ResponseEntity<String> selectedDelete(@RequestBody List<Integer> selectedIds, HttpSession session) {

        String loggedInUserId = ((GuestDTO)session.getAttribute("login_auth")).getUser_id();
        if (loggedInUserId == null || loggedInUserId.trim().isEmpty()) {
             log.warn("User ID is empty in session for bulk delete.");
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자 정보를 확인할 수 없습니다.");
        }
        try {
            hostReserveService.deleteReservationByIdsAndOwner(selectedIds, loggedInUserId);

            return ResponseEntity.ok("success");

        } catch (Exception e) {
            // 서비스 또는 DB 처리 중 예외 발생 시
            log.error("Error during bulk delete for user '{}', IDs: {}", loggedInUserId, selectedIds, e);
            // 500 Internal Server Error 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("예약 취소 중 서버 오류가 발생했습니다.");
        }
    }

    // 예약상태 변경
    @PostMapping("/updateStatus")
    public String updateStatus(Integer reservation_id,
                String status,
                SearchCriteria cri,
                HttpSession session,
                RedirectAttributes rttr) throws Exception {
        
        // 1. 파라미터 및 세션 유효성 검사
        if (reservation_id == null || reservation_id <= 0) {
            rttr.addFlashAttribute("errorMessage", "잘못된 요청입니다.");
            addSearchAndPagingParams(cri, rttr);
            return RESERVATION_LIST_URL;
        }

        GuestDTO loginUser = (GuestDTO) session.getAttribute("login_auth");
        if (loginUser == null) {
            rttr.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/guest/login";
        }
        String loggedInUserId = loginUser.getUser_id();

        try {
            // 2. 공간 소유자 확인
            String ownerUserId = hostReserveService.getReservationSpaceOwnerUserId(reservation_id);

            // 3. 권한 비교 및 수정 처리
            if (ownerUserId != null && ownerUserId.equals(loggedInUserId)) {
                // 권한 있음 -> 수정 시도
                boolean updated = hostReserveService.updateReservationStatus(reservation_id, status);
                if (updated) {
                    rttr.addFlashAttribute("successMessage", "예약이 성공적으로 수정되었습니다.");
                    log.info("User '{}' updated reservation_id: {} status: {}", loggedInUserId, reservation_id, status);
                } else {
                    // 수정 실패 (이미 삭제되었거나 DB 오류 등)
                    rttr.addFlashAttribute("errorMessage", "예약 수정 중 오류가 발생했습니다.");
                    log.warn("Failed to update reservation_id: {} by user '{}' status: {}", reservation_id, loggedInUserId, status);
                }
            } else {
                // 권한 없음 또는 예약 없음
                rttr.addFlashAttribute("errorMessage", "수정 권한이 없거나 예약을 찾을 수 없습니다.");
                log.warn("Unauthorized update attempt for reservation_id: {} by user '{}'. Owner is '{}'",
                         reservation_id, loggedInUserId, ownerUserId);
            }

        } catch (Exception e) {
            log.error("Error deleting reservation_id: {}", reservation_id, e);
            rttr.addFlashAttribute("errorMessage", "예약 수정 중 예외가 발생했습니다.");
        }

        // 4. 목록 페이지로 리다이렉트 (검색/페이징 조건 유지)
        addSearchAndPagingParams(cri, rttr);

        return RESERVATION_LIST_URL;
    }

    // 예약목록 관리
    @GetMapping("/reservationList")
    public void reservationList(
                @ModelAttribute("cri") SearchCriteria cri, 
                Model model, 
                HttpSession session) throws Exception {
        log.info("Host Reservation List Page Request - Criteria: {}", cri);

        String user_id = ((GuestDTO)session.getAttribute("login_auth")).getUser_id();

        if(cri.getPerPageNum() == 0) {
            cri.setPerPageNum(5);
        }

        PageMaker pageMaker = new PageMaker();
        pageMaker.setDisplayPageNum(3);
        pageMaker.setCri(cri);
        pageMaker.setTotalCount(hostReserveService.getCountReservationListByUserId(user_id, cri));

        List<ReservManageDTO> reservationList = hostReserveService.getReservationListByUserId(user_id, cri);
        
        List<HostSpaceDTO> spaceList = hostSpaceService.getHostSpaceByUserId(user_id);

        model.addAttribute("reservationList", reservationList);
        model.addAttribute("pageMaker", pageMaker);
        model.addAttribute("spaceList", spaceList);
    }

    // 검색 조건 및 페이징 정보 유지
    private void addSearchAndPagingParams(SearchCriteria cri, RedirectAttributes rttr) {
        rttr.addAttribute("orderBy", cri.getOrderBy());
    }


}
