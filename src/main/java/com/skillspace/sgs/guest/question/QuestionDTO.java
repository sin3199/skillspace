package com.skillspace.sgs.guest.question;

import java.time.LocalDateTime;

import com.skillspace.sgs.guest.answer.AnswerDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class QuestionDTO {
	private Integer 		question_id;		// 질문 아이디
	private Integer			host_space_id;		// 공간 아이디
	private String 			user_id;			// 게스트 유저 아이디
	private String 			user_nick;			// 게스트 유저 닉네임
	private String 			question_content;	// 질문 내용
	private LocalDateTime 	created_at;			// 생성일
	
	private AnswerDTO 		answer;				// 답변

}
