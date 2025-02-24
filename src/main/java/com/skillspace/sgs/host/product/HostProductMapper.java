package com.skillspace.sgs.host.product;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.skillspace.sgs.common.utils.SearchCriteria;
import com.skillspace.sgs.common.utils.SearchItem;

public interface HostProductMapper {
	
	// 상품 목록 총 갯수
	int getCountProductByUser_id(String user_id);

	// 상품 목록 조회
	List<HostProductDTO> productList(@Param("user_id") String user_id,
									@Param("cri") SearchCriteria cri,
									@Param("searchItem") SearchItem searchItem);

	// 상품 등록
	void create(HostProductDTO dto);

}
