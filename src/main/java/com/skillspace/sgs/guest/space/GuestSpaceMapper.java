package com.skillspace.sgs.guest.space;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.skillspace.sgs.common.utils.SearchCriteria;
import com.skillspace.sgs.host.product.HostProductDTO;
import com.skillspace.sgs.host.space.HostSpaceDTO;

public interface GuestSpaceMapper {
	
	// 카테아이디로 공간, 카테, 이미지 정보 조회
	List<HostSpaceDTO> getHostSpaceWithCateAndImageByCateId(
			@Param("cate_id") Integer cate_id, 
			@Param("cri") SearchCriteria cri);

	// 공강 목록 총 게시물 갯수
	int getCountSpaceByCateId(Integer cate_id);

	/* 공간 상세 */
	// 공간 정보 조회
	HostSpaceDTO getHostSpaceWithImages(Integer host_space_id);

	// 상품 목록 조회(이미지 포함)
	List<HostProductDTO> getProductsWithImages(Integer host_space_id);

}
