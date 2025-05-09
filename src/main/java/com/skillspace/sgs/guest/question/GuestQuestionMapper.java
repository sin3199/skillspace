package com.skillspace.sgs.guest.question;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.skillspace.sgs.common.utils.SearchCriteria;
import com.skillspace.sgs.guest.mypage.QnaWithSpaceDTO;

public interface GuestQuestionMapper {

    //질문 등록
    void createQuestion(QuestionDTO questionDTO);

    // 질문 목록(답변 포함)
    List<QuestionDTO> getQuestionWithAnswerBySpaceId(
            @Param("host_space_id") Integer host_space_id,
            @Param("cri") SearchCriteria cri);

    // 전체 질문 개수 조회
    int countQuestionsBySpaceId(Integer host_space_id);


    // 유저 아이디로 조회하는 질문목록
    List<QnaWithSpaceDTO> getQuestionListByUserId(
            @Param("user_id") String user_id,
            @Param("cri") SearchCriteria cri);

    // 유저 아이디로 조회하는 질문 개수
    int getCountQuestionListByUserId(String user_id);

	// 질문 아이디로 조회
	QuestionDTO getQuestionById(Integer question_id);

	// 질문 수정
	int modifyQuestion(QuestionDTO questionDTO);

	// 질문 삭제
    int deleteQuestion(Integer question_id);

}
