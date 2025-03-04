package com.skillspace.sgs.guest.space;

import java.util.List;

import org.springframework.stereotype.Service;

import com.skillspace.sgs.common.utils.SearchCriteria;
import com.skillspace.sgs.host.space.HostSpaceDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class GuestSpaceService {
	
	private final GuestSpaceMapper guestSpaceMapper;
	
	// 카테아이디로 공간, 카테, 이미지 정보 조회
	public List<HostSpaceDTO> getHostSpaceWithCateAndImageByCateId(Integer cate_id, SearchCriteria cri) {
		return guestSpaceMapper.getHostSpaceWithCateAndImageByCateId(cate_id, cri);
	}

	public int getCountSpaceByCateId(Integer cate_id) {
		return guestSpaceMapper.getCountSpaceByCateId(cate_id);
	}

}
