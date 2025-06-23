package com.skillspace.sgs.host.review;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.skillspace.sgs.common.utils.PageMaker;
import com.skillspace.sgs.common.utils.SearchCriteria;
import com.skillspace.sgs.guest.GuestDTO;
import com.skillspace.sgs.host.space.HostSpaceDTO;
import com.skillspace.sgs.host.space.HostSpaceService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/host/review")
@Controller
public class HostReviewController {

    private final HostReviewService hostReviewService;
    private final HostSpaceService hostSpaceService;

    private final String REVIEW_LIST_URL = "redirect:/host/review/reviewList";

    @GetMapping("/reviewList")
    public void reviewList(@ModelAttribute("cri") SearchCriteria cri, Model model, HttpSession session) throws Exception {
        log.info("Host Q&A List Page Request - Criteria: {}", cri);

        String user_id = ((GuestDTO)session.getAttribute("login_auth")).getUser_id();

        if(cri.getPerPageNum() == 0) {
            cri.setPerPageNum(5);
        }

        PageMaker pageMaker = new PageMaker();
        pageMaker.setDisplayPageNum(3);
        pageMaker.setCri(cri);
        pageMaker.setTotalCount(hostReviewService.getCountreviewListByUserId(user_id, cri));

        List<ReviewHostDTO> reviewList = hostReviewService.getreviewListByUserId(user_id, cri);
        
        List<HostSpaceDTO> spaceList = hostSpaceService.getHostSpaceByUserId(user_id);

        model.addAttribute("reviewList", reviewList);
        model.addAttribute("pageMaker", pageMaker);
        model.addAttribute("spaceList", spaceList);
    }

    // 이용후기 1개 삭제
    @PostMapping("/deleteReview")
    public String deleteReview(Integer review_id, SearchCriteria cri, 
                                 HttpSession session, RedirectAttributes rttr) {

        // 1. 파라미터 및 세션 유효성 검사
        if (review_id == null || review_id <= 0) {
            rttr.addFlashAttribute("errorMessage", "잘못된 요청입니다.");
            addSearchAndPagingParams(cri, rttr);
            return REVIEW_LIST_URL;
        }

        GuestDTO loginUser = (GuestDTO) session.getAttribute("login_auth");
        if (loginUser == null) {
            rttr.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/guest/login";
        }
        String loggedInUserId = loginUser.getUser_id();

        try {
            // 2. 질문 삭제 권한 확인
            String ownerUserId = hostReviewService.getReviewOwnerUserId(review_id);

            // 3. 권한 비교 및 삭제 처리
            if (ownerUserId != null && ownerUserId.equals(loggedInUserId)) {
                // 권한 있음 -> 삭제 시도
                boolean deleted = hostReviewService.deleteReview(review_id);
                if (deleted) {
                    rttr.addFlashAttribute("successMessage", "질문이 성공적으로 삭제되었습니다.");
                    log.info("User '{}' deleted review_id: {}", loggedInUserId, review_id);
                } else {
                    // 삭제 실패 (이미 삭제되었거나 DB 오류 등)
                    rttr.addFlashAttribute("errorMessage", "질문 삭제 중 오류가 발생했습니다.");
                    log.warn("Failed to delete review_id: {} by user '{}'", review_id, loggedInUserId);
                }
            } else {
                // 권한 없음 또는 질문 없음
                rttr.addFlashAttribute("errorMessage", "삭제 권한이 없거나 질문을 찾을 수 없습니다.");
                log.warn("Unauthorized delete attempt for review_id: {} by user '{}'. Owner is '{}'",
                         review_id, loggedInUserId, ownerUserId);
            }

        } catch (Exception e) {
            log.error("Error deleting review_id: {}", review_id, e);
            rttr.addFlashAttribute("errorMessage", "질문 삭제 중 예외가 발생했습니다.");
        }

        // 4. 목록 페이지로 리다이렉트 (검색/페이징 조건 유지)
        addSearchAndPagingParams(cri, rttr);
        return REVIEW_LIST_URL;
    }
    

    // 검색 조건 및 페이징 정보 유지
    private void addSearchAndPagingParams(SearchCriteria cri, RedirectAttributes rttr) {
        rttr.addAttribute("page", cri.getPage());
        rttr.addAttribute("perPageNum", cri.getPerPageNum());
        rttr.addAttribute("searchType", cri.getSearchType());
        rttr.addAttribute("keyword", cri.getKeyword());
        rttr.addAttribute("start_date", cri.getStart_date());
        rttr.addAttribute("end_date", cri.getEnd_date());
        rttr.addAttribute("answer_status", cri.getAnswer_status());
        rttr.addAttribute("space_id", cri.getSpace_id());
    }

}
