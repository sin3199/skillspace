package com.skillspace.sgs.guest.space;

import java.util.List;

import org.springframework.stereotype.Service;

import com.skillspace.sgs.common.utils.SearchCriteria;
import com.skillspace.sgs.host.product.HostProductDTO;
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
	
	// 공간 목록의 전체 게시물 갯수
	public int getCountSpaceByCateId(Integer cate_id) {
		return guestSpaceMapper.getCountSpaceByCateId(cate_id);
	}

	/* 공간 디테일 페이지 */
	// 공간 정보 조회
	public HostSpaceDTO getHostSpaceWithImages(Integer host_space_id) {
		return guestSpaceMapper.getHostSpaceWithImages(host_space_id);
	}

	// 상품 목록 조회(이미지 포함)
	public List<HostProductDTO> getProductsWithImages(Integer host_space_id) {
		return guestSpaceMapper.getProductsWithImages(host_space_id);
	}

}
