package com.skillspace.sgs.common.utils;

// 페이징 기능을 위한 클래스

public class Criteria {

	private int page;		// 사용자가 선택한 페이지 번호
	private int perPageNum;	// 페이지 별로 출력한 게시물 개수
	private String orderBy;	// 페이지 정렬
	
	public Criteria() {
		this.page = 1;			// 페이지 초기값 1페이지
		this.perPageNum = 5;
		this.orderBy = "desc";
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		
		if(page <= 0) {
			this.page = 1;
			return;
		}
		
		this.page = page;
	}

	public void setPerPageNum(int perPageNum) {
		
		if(perPageNum <= 0 || perPageNum > 100) {
			this.perPageNum = 10;
			return;
		}
		
		this.perPageNum = perPageNum;
	}

	// 새로 만든 메서드. mybatis mapper 파일에서 참조가 되려면, getter()메서드 문법 형식을 갖추고 있어야 한다.
	// MySQL에서 Limit의 시작 인덱스 번호를 구하는 값
	public int getPageStart() {
		return (this.page - 1) * perPageNum;
	}

	// mybatis mapper 파일에서 참조
	public int getPerPageNum() {
		return perPageNum;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	@Override
	public String toString() {
		return "Criteria [page=" + page + ", perPageNum=" + perPageNum + ", orderBy=" + orderBy + "]";
	}
	
	
	
	
	
}
