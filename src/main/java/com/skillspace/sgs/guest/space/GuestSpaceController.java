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
import com.skillspace.sgs.common.utils.PageMaker;
import com.skillspace.sgs.common.utils.SearchCriteria;
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
	
	// 이미지 경로 구분자를 변경
	private List<HostSpaceDTO> separatorChange(List<HostSpaceDTO> spaceList) {
		spaceList.forEach(space_info -> {
			space_info.getImages().forEach(image_info -> {
				image_info.setImage_up_folder(image_info.getImage_up_folder().replace("\\", "/"));
			});
		});
		return spaceList;
	}
	
	
	
	
}
