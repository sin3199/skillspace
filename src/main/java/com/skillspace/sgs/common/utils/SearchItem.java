package com.skillspace.sgs.common.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SearchItem {

	private String start_date;		// 날짜 조건 시작 날짜
	private String end_date;		// 날짜 조건 끝나는 날짜
	private String visible_status;	// 화면 노출 상태
}
