package com.skillspace.sgs.host.reply;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class HostReplyService {

    private final HostReplyMapper hostReplyMapper;

    // 답변 등록
    @Transactional
    public void createReply(ReplyDTO replyDTO) {
        hostReplyMapper.createReply(replyDTO);
    }

    // 답변 수정
    public void modifyReply(ReplyDTO replyDTO) {
        hostReplyMapper.modifyReply(replyDTO);
    }

    // 답변 삭제
    public void deleteReply(ReplyDTO replyDTO) {
        hostReplyMapper.deleteReply(replyDTO.getReply_id());
    }
}
