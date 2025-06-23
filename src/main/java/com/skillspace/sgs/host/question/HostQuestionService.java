package com.skillspace.sgs.host.question;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skillspace.sgs.common.utils.SearchCriteria;
import com.skillspace.sgs.guest.answer.AnswerDTO;
import com.skillspace.sgs.guest.mypage.QnaWithSpaceDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class HostQuestionService {

    private final HostQuestionMapper hostQuestionMapper;

    // 유저 아이디로 조회하는 질문 목록
    public List<QnaWithSpaceDTO> getQuestionListByUserId(String user_id, SearchCriteria cri) {
        return hostQuestionMapper.getQuestionListByUserId(user_id, cri);
    }

    // 유저 아이디로 조회하는 질문 개수
    public int getCountQuestionListByUserId(String user_id, SearchCriteria cri) {
        return hostQuestionMapper.getCountQuestionListByUserId(user_id, cri);
    }

    // 질문 ID로 소유자 User ID 조회
    public String getQuestionOwnerUserId(int question_id) {
        return hostQuestionMapper.getOwnerUserIdByQuestionId(question_id);
    }

    // 질문 삭제
    public boolean deleteQuestion(int question_id) {
        int deletedRows = hostQuestionMapper.deleteQuestionById(question_id);
        if (deletedRows > 0) {
            log.info("Question deleted successfully: question_id={}", question_id);
            return true;
        } else {
            log.warn("Failed to delete question or question not found: question_id={}", question_id);
            return false;
        }
    }

    // 여러개의 질문 삭제
    public void deleteQuestionsByIdsAndOwner(List<Integer> questionIds, String userId) {
        hostQuestionMapper.deleteQuestionsByIdsAndOwner(questionIds, userId);
    }

    // 답변 등록
    @Transactional
    public void createAnswer(AnswerDTO answerDTO) {
        // 1) 답변 등록
        hostQuestionMapper.createAnswer(answerDTO);
        // 2) 질문 테이블의 answer_status 변경
        String answer_status = "Y";
        hostQuestionMapper.setAnswerStatus(answerDTO.getQuestion_id(), answer_status);
    }

    // 답변 수정
    public void modifyAnswer(AnswerDTO answerDTO) {
        hostQuestionMapper.modifyAnswer(answerDTO);
    }

    // 답변 삭제
    public void deleteAnswer(AnswerDTO answerDTO) {
        // 1) 답변 삭제
        hostQuestionMapper.deleteAnswer(answerDTO.getAnswer_id());
        // 2) 질문 테이블의 answer_status 변경
        String answer_status = "N";
        hostQuestionMapper.setAnswerStatus(answerDTO.getQuestion_id(), answer_status);
    }

}
