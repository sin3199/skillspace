package com.skillspace.sgs.guest.mypage;

import java.time.LocalDateTime;
import java.util.Map;

import com.skillspace.sgs.guest.answer.AnswerDTO;
import com.skillspace.sgs.host.space.HostSpaceDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MyPageQnaDTO {
	private Integer 		question_id;		// 질문 아이디
	private Integer			host_space_id;		// 공간 아이디
	private String 			user_id;			// 게스트 유저 아이디
	private String 			user_nick;			// 게스트 유저 닉네임
	private String 			question_content;	// 질문 내용
	private String 			question_status;	// 답변 상태 Y,N
	private LocalDateTime 	question_created_at;// 생성일
	
	private Map<String, Object> 		answer;				// 답변
	private Map<String, Object> 	hostSpace;			// 공간 정보

}
