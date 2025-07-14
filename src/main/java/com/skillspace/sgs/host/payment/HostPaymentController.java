package com.skillspace.sgs.host.payment;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.skillspace.sgs.common.utils.SearchCriteria;
import com.skillspace.sgs.guest.GuestDTO;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RequestMapping("/host/payments")
@Slf4j
@Controller
public class HostPaymentController {

    private final HostPaymentService hostPaymentService;

    private final String RESERVATION_LIST_URL = "redirect:/host/reserves/reservationList";

    // 결제상태 변경
    @PostMapping("/updateStatus")
    public String updateStatus(Integer payment_id,
                String status,
                SearchCriteria cri,
                HttpSession session,
                RedirectAttributes rttr,
                @RequestHeader(value = "Referer", required = false) String referer) throws Exception {
        
        // 1. 파라미터 및 세션 유효성 검사
        if (payment_id == null || payment_id <= 0) {
            rttr.addFlashAttribute("errorMessage", "잘못된 요청입니다.");
            addSearchAndPagingParams(cri, rttr);
            return referer != null && !referer.isEmpty() ? "redirect:" + referer : RESERVATION_LIST_URL;
        }

        GuestDTO loginUser = (GuestDTO) session.getAttribute("login_auth");
        if (loginUser == null) {
            rttr.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/guest/login";
        }
        String loggedInUserId = loginUser.getUser_id();

        try {
            // 2. 공간 소유자 확인
            String ownerUserId = hostPaymentService.getPaymentSpaceOwnerUserId(payment_id);

            // 3. 권한 비교 및 수정 처리
            if (ownerUserId != null && ownerUserId.equals(loggedInUserId)) {
                // 권한 있음 -> 수정 시도
                boolean updated = hostPaymentService.updatePaymentStatus(payment_id, status);
                if (updated) {
                    rttr.addFlashAttribute("successMessage", "결제상태가 성공적으로 수정되었습니다.");
                    log.info("User '{}' updated payment_id: {} status: {}", loggedInUserId, payment_id, status);
                } else {
                    // 수정 실패 (이미 삭제되었거나 DB 오류 등)
                    rttr.addFlashAttribute("errorMessage", "결제상태 수정 중 오류가 발생했습니다.");
                    log.warn("Failed to update payment_id: {} by user '{}' status: {}", payment_id, loggedInUserId, status);
                }
            } else {
                // 권한 없음 또는 예약 없음
                rttr.addFlashAttribute("errorMessage", "수정 권한이 없거나 결제를 찾을 수 없습니다.");
                log.warn("Unauthorized update attempt for payment_id: {} by user '{}'. Owner is '{}'",
                         payment_id, loggedInUserId, ownerUserId);
            }

        } catch (Exception e) {
            log.error("Error deleting payment_id: {}", payment_id, e);
            rttr.addFlashAttribute("errorMessage", "결제상태 수정 중 예외가 발생했습니다.");
        }

        // 4. 목록 페이지로 리다이렉트 (검색/페이징 조건 유지)
        addSearchAndPagingParams(cri, rttr);

        return referer != null && !referer.isEmpty() ? "redirect:" + referer : RESERVATION_LIST_URL;
    }

    // 검색 조건 및 페이징 정보 유지
    private void addSearchAndPagingParams(SearchCriteria cri, RedirectAttributes rttr) {
        rttr.addAttribute("page", cri.getPage());
        rttr.addAttribute("perPageNum", cri.getPerPageNum());
        rttr.addAttribute("orderBy", cri.getOrderBy());
        rttr.addAttribute("searchType", cri.getSearchType());
        rttr.addAttribute("keyword", cri.getKeyword());
        rttr.addAttribute("start_date", cri.getStart_date());
        rttr.addAttribute("end_date", cri.getEnd_date());
        rttr.addAttribute("reservation_status", cri.getReservation_status());
        rttr.addAttribute("payment_status", cri.getPayment_status());
        rttr.addAttribute("space_id", cri.getSpace_id());
    }

}
