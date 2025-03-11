package com.skillspace.sgs.guest.space;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.skillspace.sgs.admin.category.AdminCategoryService;
import com.skillspace.sgs.admin.images.ImagesDTO;
import com.skillspace.sgs.common.utils.PageMaker;
import com.skillspace.sgs.common.utils.SearchCriteria;
import com.skillspace.sgs.host.product.HostProductDTO;
import com.skillspace.sgs.host.space.HostSpaceDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/guest/space")
@Controller
public class GuestSpaceController {
	
	private final GuestSpaceService guestSpaceService;
	private final AdminCategoryService adminCategoryService;
	
	// 게스트 공간 목록 페이지
	@GetMapping("/spaceList")
	public void spaceList(Integer cate_id, SearchCriteria cri, Model model) throws Exception {
		
		log.info("게스트 공간목록 cri : " + cri);
		if(cri.getPerPageNum() == 0) { // perPageNum이 0 인 경우 (초기 요청)
			cri.setPerPageNum(3);	// 페이지 관리 상수값
		}
		PageMaker pageMaker = new PageMaker();
		pageMaker.setDisplayPageNum(1);
		pageMaker.setCri(cri);
		pageMaker.setTotalCount(guestSpaceService.getCountSpaceByCateId(cate_id));
		
		List<HostSpaceDTO> spaceList = guestSpaceService.getHostSpaceWithCateAndImageByCateId(cate_id, cri);
		spaceList = separatorChange(spaceList);
		
		model.addAttribute("spaceList", spaceList);
		model.addAttribute("pageMaker", pageMaker);
		model.addAttribute("cateInfo", adminCategoryService.getCategoryById(cate_id));
	}
	
	// 게스트 공간 목록에 공간 목록 추가
	@GetMapping("/loadMore")
	public ResponseEntity<Map<String, Object>> listMore (Integer cate_id,
			SearchCriteria cri, Integer totalCount, Integer displayPageNum) throws Exception {
		
		log.info("추가로드 cri : " + cri);
		log.info("토탈카운트 : " + totalCount);
		log.info("디스플레이넘 : " + displayPageNum);
		
		List<HostSpaceDTO> spaceList = guestSpaceService.getHostSpaceWithCateAndImageByCateId(cate_id, cri);
		spaceList = separatorChange(spaceList);
		
		PageMaker pageMaker = new PageMaker();
		pageMaker.setDisplayPageNum(displayPageNum);
		pageMaker.setCri(cri);
		pageMaker.setTotalCount(totalCount);
		
		Map<String, Object> map = new HashMap<>();
		map.put("spaceList", spaceList);
		map.put("pageMaker", pageMaker);
		return ResponseEntity.ok(map);
	}
	
	// 게스트 공간 상세 페이지
	@GetMapping("/spaceDetail")
	public void detail(Integer host_space_id, Model model) throws Exception {
		
		// 1) 공간 상세 정보(이미지 포함)
		HostSpaceDTO hostSpaceDTO = guestSpaceService.getHostSpaceWithImages(host_space_id);
		
		List<ImagesDTO> images = hostSpaceDTO.getImages();
		if(!images.isEmpty()) {
			images.forEach(this::changeImageUpFolder);
			hostSpaceDTO.setImages(images);
		}
		
		// 2) 공간의 상품 목록(이미지 포함)
		List<HostProductDTO> productList = guestSpaceService.getProductsWithImages(host_space_id);
		separatorChange(productList);
		
		model.addAttribute("spaceInfo", hostSpaceDTO);
		model.addAttribute("productList", productList);
	}
	
	
	
	
	
	
	
	
	
	
	
	// 이미지 경로 구분자를 변경
	private <T> List<T> separatorChange(List<T> dtoList) {
	    dtoList.forEach(dto -> {
	        if (dto instanceof HostSpaceDTO) {
	            ((HostSpaceDTO) dto).getImages().forEach(this::changeImageUpFolder);
	        } else if (dto instanceof HostProductDTO) {
	            ((HostProductDTO) dto).getImages().forEach(this::changeImageUpFolder);
	        }
	    });
	    return dtoList;
	}
	private void changeImageUpFolder(ImagesDTO imageInfo) {
	    	imageInfo.setImage_up_folder(imageInfo.getImage_up_folder().replace("\\", "/"));
	}

}
