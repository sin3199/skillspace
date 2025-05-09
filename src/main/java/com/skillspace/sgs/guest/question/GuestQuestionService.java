package com.skillspace.sgs.guest.question;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.skillspace.sgs.common.utils.SearchCriteria;
import com.skillspace.sgs.guest.mypage.QnaWithSpaceDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
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

    // 질문 아이디로 조회
    public QuestionDTO getQuestionById(Integer question_id) {
        return guestQuestionMapper.getQuestionById(question_id);
    }

    // 질문 수정
    @Transactional
    public String modifyQuestion(QuestionDTO questionDTO, String sessionUserId) {
        

        // 1. 기존 질문 정보 조회 (DB에서 user_id, answer_status 등 확인)
        QuestionDTO existingQuestion = guestQuestionMapper.getQuestionById(questionDTO.getQuestion_id());

        if (existingQuestion == null) {
            log.warn("modifyQuestion - 질문을 찾을 수 없습니다. with ID: {}", questionDTO.getQuestion_id());
            return "not_found";
        }

        // 2. 수정 권한 확인 
        if (!existingQuestion.getUser_id().equals(sessionUserId)) {
            log.warn("modifyQuestion - 권한 실패. User {} attempted to modify question {} owned by {}",
                    sessionUserId, questionDTO.getQuestion_id(), existingQuestion.getUser_id());
            return "auth_fail";
        }

        // 3. 이미 답변된 질문인지 확인 (예: 'Y'가 답변 완료 상태)
        if ("Y".equals(existingQuestion.getAnswer_status())) {
            log.warn("modifyQuestion - ID : {}은 이미 답변이 완료된 질문 입니다", questionDTO.getQuestion_id());
            return "answered";
        }

        // 4. 질문 수정 실행
        int updatedRows = guestQuestionMapper.modifyQuestion(questionDTO);

        if (updatedRows > 0) {
            log.info("Question 수정이 완료. ID: {}", questionDTO.getQuestion_id());
            return "success";
        } else {
            log.error("modifyQuestion - ID: {}은 질문 수정 실패.", questionDTO.getQuestion_id());
            return "fail"; 
        }
    }

    // 질문 삭제
    public String deleteQuestion(Integer question_id, String sessionUserId) {

        // 1. 기존 질문 정보 조회 (DB에서 user_id, answer_status 등 확인)
        QuestionDTO existingQuestion = guestQuestionMapper.getQuestionById(question_id);

        if (existingQuestion == null) {
            log.warn("deleteQuestion - 질문을 찾을 수 없습니다. with ID: {}", question_id);
            return "not_found";
        }

        // 2. 수정 권한 확인 
        if (!existingQuestion.getUser_id().equals(sessionUserId)) {
            log.warn("deleteQuestion - 권한 실패. User {} attempted to modify question {} owned by {}",
                    sessionUserId, question_id, existingQuestion.getUser_id());
            return "auth_fail";
        }

        // 3. 질문 삭제 실행
        int deleteRows = guestQuestionMapper.deleteQuestion(question_id);

        if (deleteRows > 0) {
            log.info("Question 삭제 완료. ID: {}", question_id);
            return "success";
        } else {
            log.error("modifyQuestion - ID: {}은 질문 삭제 실패.", question_id);
            return "fail"; 
        }
    }
}
