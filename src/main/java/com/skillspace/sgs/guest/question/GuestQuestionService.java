package com.skillspace.sgs.guest.question;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class GuestQuestionService {
    
    private final GuestQuestionMapper guestQuestionMapper;
    
    // 질문 등록
    public void createQuestion(QuestionDTO questionDTO) {
        guestQuestionMapper.createQuestion(questionDTO);

    }

    // 질문 목록 (답변 목록 나중에 추가)
    public List<QuestionDTO> getQuestionWithAnswerBySpaceId(Integer host_space_id) {
        return guestQuestionMapper.getQuestionWithAnswerBySpaceId(host_space_id);
    }

}
