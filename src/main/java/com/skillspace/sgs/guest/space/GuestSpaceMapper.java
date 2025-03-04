package com.skillspace.sgs.guest.space;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.skillspace.sgs.common.utils.SearchCriteria;
import com.skillspace.sgs.host.space.HostSpaceDTO;

public interface GuestSpaceMapper {
	
	// 카테아이디로 공간, 카테, 이미지 정보 조회
	List<HostSpaceDTO> getHostSpaceWithCateAndImageByCateId(
			@Param("cate_id") Integer cate_id, 
			@Param("cri") SearchCriteria cri);

	int getCountSpaceByCateId(Integer cate_id);

}
