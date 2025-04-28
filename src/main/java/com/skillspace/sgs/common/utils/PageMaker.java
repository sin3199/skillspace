package com.skillspace.sgs.common.utils;

import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.ToString;

// 	  	  1  2  3  4  5  [next]
// [prev] 6  7  8  9  10
@ToString
public class PageMaker {

	private int 	totalCount;	// 테이블의 총 데이터 개수
	private int 	startPage;	// 각 블럭의 시작 페이지 값
	private int 	endPage;	// 각 블럭의 마지막 페이지 값
	private boolean prev;		// 이전 블럭 존재 여부
	private boolean next;		// 다음 블럭 존재 여부
	
	private int displayPageNum = 10;	// 블럭에 보여줄 페이지 번호 개수 1  2  3... 10
	
	public void setDisplayPageNum(int displayPageNum) {
		this.displayPageNum = displayPageNum;
	}

//	private Criteria cri;	// page, perPageNum
	private SearchCriteria cri;

	
	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;	// 테이블의 총 데이터 개수가 13개라고 가정
		
		calcData();
	}
	
	// 페이징 기능에 필요한 계산
	private void calcData() {
		
		// Math.ceil() : 소수점 올림 함수 Math.ceil(0.1) -> 1.0
		endPage = (int)(Math.ceil(cri.getPage() / (double)displayPageNum) * displayPageNum);
		
		startPage = (endPage - displayPageNum) + 1;
		
		// 위에는 페이지 블럭에 보여줄 페이지 번호 범위
		//----------------------------------------------------
		
		int tempEndPage = (int)(Math.ceil(totalCount / (double)cri.getPerPageNum()));	// 마지막 페이지 번호
		
		if(endPage > tempEndPage) endPage = tempEndPage;	// 마지막 블럭에 불필요한 페이지 번호를 안보여 주기 위해
		
		prev = startPage == 1 ? false : true;	// 블럭에 [이전] 표시 보여 줄지 말지
		
		next = endPage * cri.getPerPageNum() >= totalCount ? false : true; // 블럭에 [다음] 표시 보여 줄지 말지
	}

	public int getStartPage() {
		return startPage;
	}

	public int getEndPage() {
		return endPage;
	}

	public boolean isPrev() {
		return prev;
	}

	public boolean isNext() {
		return next;
	}

	public int getDisplayPageNum() {
		return displayPageNum;
	}

	/*
	public Criteria getCri() {
		return cri;
	}

	public void setCri(Criteria cri) {
		this.cri = cri;
	}
	*/
	
	public SearchCriteria getCri() {
		return cri;
	}
	
	public void setCri(SearchCriteria cri) {
		this.cri = cri;
	}
	
	
	// 페이징 기능만 사용시 필요한 파라미터 작업
	public String makeQuery(int page) {
		UriComponents uriComponents = 
				UriComponentsBuilder.newInstance()
				.queryParam("page", page)
				.queryParam("perPageNum", cri.getPerPageNum())
				.build();
		
		return uriComponents.toUriString();
	}

	// 페이징, 검색기능 사용시 필요한 파라미터 생성해 주는 기능.  쿼리스트링 만들어주는 메소드
	// ?page=2&perPageNum=10&searchType&keyword
	// 호스트 상품, 공간 목록에서 사용
	public String makeSearch(int page) {
		UriComponentsBuilder builder = 
				UriComponentsBuilder.newInstance()
				.queryParam("page", 			page)
				.queryParam("perPageNum", 		cri.getPerPageNum())
				.queryParam("orderBy", 		cri.getOrderBy());

				// 날짜 관련 파라미터
				if (cri.getStart_date() != null && !cri.getStart_date().isEmpty()) {
					builder.queryParam("start_date", cri.getStart_date());
				}
				if (cri.getEnd_date() != null && !cri.getEnd_date().isEmpty()) {
					builder.queryParam("end_date", cri.getEnd_date());
				}


				// 검색 관련 파라미터 (값이 있을 때만 추가)
				if (cri.getSearchType() != null && !cri.getSearchType().isEmpty()) {
					builder.queryParam("searchType", cri.getSearchType());
				}
				if (cri.getKeyword() != null && !cri.getKeyword().isEmpty()) {
					builder.queryParam("keyword", cri.getKeyword());
				}
		
				// 상품 관련 필터 파라미터
				if (cri.getVisible_status() != null && !cri.getVisible_status().isEmpty()) {
					builder.queryParam("visible_status", cri.getVisible_status());
				}
				
				// Q&A 관련 필터 파라미터
				if (cri.getAnswer_status() != null && !cri.getAnswer_status().isEmpty()) {
					builder.queryParam("answer_status", cri.getAnswer_status());
				}

				// space_id 필터 파라미터
				if (cri.getSpace_id() != null) {
					builder.queryParam("space_id", cri.getSpace_id());
				}
		
				UriComponents uriComponents = builder.build();
		
		return uriComponents.toUriString();
	}
	
}
