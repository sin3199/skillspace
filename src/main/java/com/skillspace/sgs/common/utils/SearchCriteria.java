package com.skillspace.sgs.common.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// 페이징 기능, 검색 기능을 위한 클래스
@Getter
@Setter
@ToString
public class SearchCriteria extends Criteria {
	// 디폴트 생성자 자동 생성.

	private String searchType;			// 검색 유형 (제목, 내용, 제목+내용 ...)
	private String keyword;				// 검색어 
	
	private String start_date;		// 날짜 조건 시작 날짜
	private String end_date;		// 날짜 조건 끝나는 날짜
	private String visible_status;	// 화면 노출 상태
	
}
