package com.skillspace.sgs.guest.answer;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AnswerDTO {

	private Integer 		answer_id;		// 답변 아이디
	private Integer 		question_id;	// 질문 아이디
	private String			answer_content;	// 답변 내용
	private LocalDateTime 	created_at;		// 생성일
}
