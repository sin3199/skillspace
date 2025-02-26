package com.skillspace.sgs.host.product;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.skillspace.sgs.common.utils.SearchCriteria;
import com.skillspace.sgs.common.utils.SearchItem;

public interface HostProductMapper {
	
	// 상품 목록 총 갯수
	int getCountProductByUser_id(
			@Param("user_id") String user_id,
			@Param("cri") SearchCriteria cri);

	// 상품 목록 조회
	List<HostProductDTO> productList(
			@Param("user_id") String user_id,
			@Param("cri") SearchCriteria cri);

	// 상품 등록
	void create(HostProductDTO dto);

	// 상품 아이디로 상품과 이미지 조회
	HostProductDTO getProductWithImagesById(Integer product_id);

	// 상품 수정
	void modifyHostProduct(HostProductDTO dto);

	// 상품의 유저 정보
	String getProductUserIdById(Integer product_id);

	// 상품 삭제
	void deleteHostProduct(Integer product_id);

	// 체크박스 선택 삭제
	void selectedDelete(int[] product_id_arr);

}
