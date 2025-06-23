package com.skillspace.sgs.host.reply;

public interface HostReplyMapper {

    // 답변 등록
    void createReply(ReplyDTO replyDTO);

    // 답변 수정
    void modifyReply(ReplyDTO replyDTO);

    // 답변 삭제
    void deleteReply(Integer reply_id);

}
