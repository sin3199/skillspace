package com.skillspace.sgs.host.question;

import java.util.List;

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
import com.skillspace.sgs.guest.answer.AnswerDTO;
import com.skillspace.sgs.guest.mypage.QnaWithSpaceDTO;
import com.skillspace.sgs.host.space.HostSpaceDTO;
import com.skillspace.sgs.host.space.HostSpaceService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@RequestMapping("/host/question")
@Controller
public class HostQuestionController {

    private final HostQuestionService hostQuestionService;
    private final HostSpaceService hostSpaceService;

    private final String QUESTION_LIST_URL = "redirect:/host/question/questionList";

    @GetMapping("/questionList")
    public void questionList(@ModelAttribute("cri") SearchCriteria cri, Model model, HttpSession session) throws Exception {
        log.info("Host Q&A List Page Request - Criteria: {}", cri);

        String user_id = ((GuestDTO)session.getAttribute("login_auth")).getUser_id();

        if(cri.getPerPageNum() == 0) {
            cri.setPerPageNum(5);
        }

        PageMaker pageMaker = new PageMaker();
        pageMaker.setDisplayPageNum(3);
        pageMaker.setCri(cri);
        pageMaker.setTotalCount(hostQuestionService.getCountQuestionListByUserId(user_id, cri));

        List<QnaWithSpaceDTO> questionList = hostQuestionService.getQuestionListByUserId(user_id, cri);
        
        List<HostSpaceDTO> spaceList = hostSpaceService.getHostSpaceByUserId(user_id);

        model.addAttribute("questionList", questionList);
        model.addAttribute("pageMaker", pageMaker);
        model.addAttribute("spaceList", spaceList);
    }

    // 개별 삭제
    @PostMapping("/deleteQuestion")
    public String deleteQuestion(Integer question_id, SearchCriteria cri, 
                                 HttpSession session, RedirectAttributes rttr) {

        // 1. 파라미터 및 세션 유효성 검사
        if (question_id == null || question_id <= 0) {
            rttr.addFlashAttribute("errorMessage", "잘못된 요청입니다.");
            addSearchAndPagingParams(cri, rttr);
            return QUESTION_LIST_URL;
        }

        GuestDTO loginUser = (GuestDTO) session.getAttribute("login_auth");
        if (loginUser == null) {
            rttr.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
            return "redirect:/guest/login";
        }
        String loggedInUserId = loginUser.getUser_id();

        try {
            // 2. 질문 소유자 확인
            String ownerUserId = hostQuestionService.getQuestionOwnerUserId(question_id);

            // 3. 권한 비교 및 삭제 처리
            if (ownerUserId != null && ownerUserId.equals(loggedInUserId)) {
                // 권한 있음 -> 삭제 시도
                boolean deleted = hostQuestionService.deleteQuestion(question_id);
                if (deleted) {
                    rttr.addFlashAttribute("successMessage", "질문이 성공적으로 삭제되었습니다.");
                    log.info("User '{}' deleted question_id: {}", loggedInUserId, question_id);
                } else {
                    // 삭제 실패 (이미 삭제되었거나 DB 오류 등)
                    rttr.addFlashAttribute("errorMessage", "질문 삭제 중 오류가 발생했습니다.");
                    log.warn("Failed to delete question_id: {} by user '{}'", question_id, loggedInUserId);
                }
            } else {
                // 권한 없음 또는 질문 없음
                rttr.addFlashAttribute("errorMessage", "삭제 권한이 없거나 질문을 찾을 수 없습니다.");
                log.warn("Unauthorized delete attempt for question_id: {} by user '{}'. Owner is '{}'",
                         question_id, loggedInUserId, ownerUserId);
            }

        } catch (Exception e) {
            log.error("Error deleting question_id: {}", question_id, e);
            rttr.addFlashAttribute("errorMessage", "질문 삭제 중 예외가 발생했습니다.");
        }

        // 4. 목록 페이지로 리다이렉트 (검색/페이징 조건 유지)
        addSearchAndPagingParams(cri, rttr);
        return QUESTION_LIST_URL;
    }

    // 체크박스 선택 삭제
    @PostMapping("/selectedDelete")
    @ResponseBody
    public ResponseEntity<String> selectedDelete(@RequestBody List<Integer> selectedIds, HttpSession session) {

        String loggedInUserId = ((GuestDTO)session.getAttribute("login_auth")).getUser_id();
        if (loggedInUserId == null || loggedInUserId.trim().isEmpty()) {
             log.warn("User ID is empty in session for bulk delete.");
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자 정보를 확인할 수 없습니다.");
        }
        try {
            hostQuestionService.deleteQuestionsByIdsAndOwner(selectedIds, loggedInUserId);

            return ResponseEntity.ok("success");

        } catch (Exception e) {
            // 서비스 또는 DB 처리 중 예외 발생 시
            log.error("Error during bulk delete for user '{}', IDs: {}", loggedInUserId, selectedIds, e);
            // 500 Internal Server Error 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("질문 삭제 중 서버 오류가 발생했습니다.");
        }
    }

    // 답변 등록
    @PostMapping("createAnswer")
    @ResponseBody
    public ResponseEntity<String> saveReply(@RequestBody AnswerDTO answerDTO) {
        
        try {
            hostQuestionService.createAnswer(answerDTO);
        }catch(Exception e) {
            log.error("Error during create answer", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("답변 등록 중 서버 오류가 발생했습니다.");
        }
        return ResponseEntity.ok("success");
    }

    // 답변 수정
    @PostMapping("modifyAnswer")
    @ResponseBody
    public ResponseEntity<String> modifyAnswer(@RequestBody AnswerDTO answerDTO) {
        try {
            hostQuestionService.modifyAnswer(answerDTO);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            log.error("Error during modify answer", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("답변 수정 중 서버 오류가 발생했습니다.");
        }
    }

    // 답변 삭제
    @PostMapping("deleteAnswer")
    @ResponseBody
    public ResponseEntity<String> deleteAnswer(@RequestBody AnswerDTO answerDTO) {
        try {
            hostQuestionService.deleteAnswer(answerDTO);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            log.error("Error during delete answer", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("답변 삭제 중 서버 오류가 발생했습니다.");
        }
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
