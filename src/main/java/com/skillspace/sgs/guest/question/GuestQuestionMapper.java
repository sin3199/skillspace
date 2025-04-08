package com.skillspace.sgs.guest.question;

import java.util.List;

public interface GuestQuestionMapper {

    //질문 등록
    void createQuestion(QuestionDTO questionDTO);

    // 질문 목록(답변 나중에 추가)
    List<QuestionDTO> getQuestionWithAnswerBySpaceId(Integer host_space_id);

}
