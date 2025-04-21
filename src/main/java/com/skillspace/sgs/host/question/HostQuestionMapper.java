package com.skillspace.sgs.host.question;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.skillspace.sgs.common.utils.SearchCriteria;
import com.skillspace.sgs.guest.answer.AnswerDTO;
import com.skillspace.sgs.guest.mypage.QnaWithSpaceDTO;

public interface HostQuestionMapper {

    // 유저 아이디로 조회하는 질문목록
    List<QnaWithSpaceDTO> getQuestionListByUserId(
            @Param("user_id") String user_id,
            @Param("cri") SearchCriteria cri);

    // 유저 아이디로 조회하는 질문 개수
    int getCountQuestionListByUserId(
            @Param("user_id") String user_id, 
            @Param("cri") SearchCriteria cri);

    // 질문 ID로 해당 질문이 속한 공간의 소유자 user_id 조회
    String getOwnerUserIdByQuestionId(int question_id);

    // 질문 ID로 질문 삭제
    int deleteQuestionById(int question_id);

    // 여러개의 질문 삭제
    void deleteQuestionsByIdsAndOwner(
            @Param("questionIds") List<Integer> questionIds, 
            @Param("user_id") String user_id);

    // 답변 등록
    void createAnswer(AnswerDTO answerDTO);

    // answer_status 변경
    void setAnswerStatus(
            @Param("question_id") Integer question_id, 
            @Param("answer_status") String answer_status);

    // 답변 수정
    void modifyAnswer(AnswerDTO answerDTO);

    // 답변 삭제
    void deleteAnswer(Integer answer_id);
}
