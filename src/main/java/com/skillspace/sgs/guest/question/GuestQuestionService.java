package com.skillspace.sgs.guest.question;

import java.util.List;

import org.springframework.stereotype.Service;

import com.skillspace.sgs.common.utils.SearchCriteria;
import com.skillspace.sgs.guest.mypage.QnaWithSpaceDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class GuestQuestionService {
    
    private final GuestQuestionMapper guestQuestionMapper;
    
    // 질문 등록
    public void createQuestion(QuestionDTO questionDTO) {
        guestQuestionMapper.createQuestion(questionDTO);

    }

    // 질문 목록
    public List<QuestionDTO> getQuestionWithAnswerBySpaceId(Integer host_space_id, SearchCriteria cri) {
        return guestQuestionMapper.getQuestionWithAnswerBySpaceId(host_space_id, cri);
    }
    // 전체 질문 개수 조회
    public int countQuestionsBySpaceId(Integer host_space_id) {
        return guestQuestionMapper.countQuestionsBySpaceId(host_space_id);
    }


    // 유저 아이디로 조회하는 질문 목록
    public List<QnaWithSpaceDTO> getQuestionListByUserId(String user_id, SearchCriteria cri) {
        return guestQuestionMapper.getQuestionListByUserId(user_id, cri);
    }
    // 유저 아이디로 조회하는 질문 개수
    public int getCountQuestionListByUserId(String user_id) {
        return guestQuestionMapper.getCountQuestionListByUserId(user_id);
    }

}
