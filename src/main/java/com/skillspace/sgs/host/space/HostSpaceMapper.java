package com.skillspace.sgs.host.space;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.skillspace.sgs.common.utils.SearchCriteria;

public interface HostSpaceMapper {

	// 호스트 공간 정보 저장
	void create(HostSpaceDTO dto);

	// 공간 아이디로 이미지 카테고리 함꼐 조회 
	HostSpaceDTO getHostSpaceWithCateAndImagesById(Integer host_space_id);

	// 공간 리스트
	List<HostSpaceDTO> spaceList(
			@Param("user_id") String user_id, 
			@Param("cri") SearchCriteria cri);
	
	// 공간 리스트 총 갯수
	int getCountSpaceByUser_id(
			@Param("user_id") String user_id, 
			@Param("cri") SearchCriteria cri);

	// 호스트 공간 수정
	void modifyHostSpace(HostSpaceDTO dto);

	// 호스트 공간 삭제
	void deleteHostSpace(Integer host_space_id);
	
	// 선택 공간 삭제
	void selectedDelete(int[] host_space_id_arr);

	// 회원 아이디로 공간 목록 조회
	List<HostSpaceDTO> getHostSpaceByUserId(String user_id);

	// 공간 유저 정보
	String getHostSpaceUserIdById(int host_space_id);

}
