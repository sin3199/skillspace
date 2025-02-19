package com.skillspace.sgs.host.space;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.skillspace.sgs.common.utils.SearchCriteria;

public interface HostSpaceMapper {

	// 호스트 공간 정보 저장
	void create(HostSpaceDTO dto);

	// 공간 아이디로 조회 
	HostSpaceDTO getHostSpaceById(Integer host_space_id);

	// 공간 리스트
	List<HostSpaceDTO> spaceList(@Param("user_id") String user_id, @Param("cri") SearchCriteria cri);
	
	// 공간 리스트 총 갯수
	int getCountSpaceByUser_id(String user_id);

	// 호스트 공간 수정
	void modifyHostSpace(HostSpaceDTO dto);

	// 호스트 공간 삭제
	void deleteHostSpace(Integer host_space_id);
	
	// 선택 공간 삭제
	void selectedDelete(int[] host_space_id_arr);

}
