package com.skillspace.sgs.host.reply;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/host/reply")
@Controller
public class HostReplyController {

    private final HostReplyService hostReplyService;

    // 답변 등록
    @PostMapping("createReply")
    @ResponseBody
    public ResponseEntity<String> saveReply(@RequestBody ReplyDTO replyDTO) {
        
        try {
            hostReplyService.createReply(replyDTO);
        }catch(Exception e) {
            log.error("Error during create answer", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("답변 등록 중 서버 오류가 발생했습니다.");
        }
        return ResponseEntity.ok("success");
    }

    // 답변 수정
    @PostMapping("modifyReply")
    @ResponseBody
    public ResponseEntity<String> modifyReply(@RequestBody ReplyDTO replyDTO) {
        try {
            hostReplyService.modifyReply(replyDTO);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            log.error("Error during modify answer", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("답변 수정 중 서버 오류가 발생했습니다.");
        }
    }

    // 답변 삭제
    @PostMapping("deleteReply")
    @ResponseBody
    public ResponseEntity<String> deleteReply(@RequestBody ReplyDTO replyDTO) {
        try {
            hostReplyService.deleteReply(replyDTO);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            log.error("Error during delete answer", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("답변 삭제 중 서버 오류가 발생했습니다.");
        }
    }

}
