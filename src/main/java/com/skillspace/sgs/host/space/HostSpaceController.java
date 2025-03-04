package com.skillspace.sgs.host.space;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.skillspace.sgs.admin.category.AdminCategoryService;
import com.skillspace.sgs.common.utils.PageMaker;
import com.skillspace.sgs.common.utils.SearchCriteria;
import com.skillspace.sgs.guest.GuestDTO;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/host/space")
@RequiredArgsConstructor
@Controller
public class HostSpaceController {
	
	private final AdminCategoryService adminCategoryService;
	private final HostSpaceService hostSpaceService;
	
	private String spaceListUrl = "/host/space/spaceList";
	
	// 호스트 공간 목록
	@GetMapping("/spaceList")
	public void spaceList(SearchCriteria cri, 
			Model model, 
			HttpSession session) throws Exception {
		
		log.info("검색 정보 : " + cri);
		
		// 1) 유저 공간 정보
		String user_id = ((GuestDTO)session.getAttribute("login_auth")).getUser_id();
		
		// 2) 공간 리스트 정보 가져오기(이미지 계층형 데이터로)
		if(cri.getPerPageNum() == 0) { // perPageNum이 0 인 경우 (초기 요청)
			cri.setPerPageNum(5);	// 페이지 관리 상수값
	    }
		PageMaker pageMaker = new PageMaker();
		pageMaker.setDisplayPageNum(2);
		pageMaker.setCri(cri);
		pageMaker.setTotalCount(hostSpaceService.getCountSpaceByUser_id(user_id, cri));
		
		List<HostSpaceDTO> spaceList = hostSpaceService.sapceList(user_id, cri);
		spaceList.forEach(space_info -> {
			space_info.getImages().forEach(image_info -> {
				image_info.setImage_up_folder(image_info.getImage_up_folder().replace("\\", "/"));
			});
		});
		
		model.addAttribute("spaceList", spaceList);
		model.addAttribute("pageMaker", pageMaker);
		
		
	}
	
	// 호스트 공간 등록 폼
	@GetMapping("/createSpace")
	public void createSpace(@ModelAttribute("cri") SearchCriteria cri, Model model) throws Exception{
		
		model.addAttribute("firstCateList", adminCategoryService.getFirstCategory());
	}
	
	// 호스트 공간 등록 저장
	@PostMapping("/createSpace")
	public String createSpace(HostSpaceDTO dto, List<MultipartFile> imageFiles,
			HttpSession session) throws Exception {
		
		// 1) 로그인 호스트
		GuestDTO guestDTO = ((GuestDTO)session.getAttribute("login_auth"));
		
		dto.setUser_id(guestDTO.getUser_id());
		// 2) 호스트 공간 저장
		hostSpaceService.create(dto, imageFiles);
//		if(guestDTO.getRole().equals("H")) {
//		}
		
		return "redirect:" + spaceListUrl;
	}
	
	// 호스트 공간 정보 수정 폼
	@GetMapping("/modifySpace")
	public void modifySpace(Integer host_space_id, @ModelAttribute("cri") SearchCriteria cri, 
			Model model) throws Exception {
		
		HostSpaceDTO hostSpaceDTO = hostSpaceService.getHostSpaceWithCateAndImagesById(host_space_id);
		hostSpaceDTO.getImages().forEach(image_info -> {
			image_info.setImage_up_folder(image_info.getImage_up_folder().replace("\\", "/"));
		});
		Integer cate_prtcode = hostSpaceDTO.getCategory().getCate_prtcode();
		
		model.addAttribute("firstCateList", adminCategoryService.getFirstCategory());
		model.addAttribute("subCateList", adminCategoryService.getSubCategory(cate_prtcode));
		model.addAttribute("hostSpaceDTO", hostSpaceDTO);
	}
	
	// 호스트 공간 수정
	@PostMapping("/modifySpace")
	public String modifySpace(HostSpaceDTO dto, SearchCriteria cri, List<MultipartFile> imageFiles,
			Integer[] loadImages, Integer[] changeImages, HttpSession session,
			RedirectAttributes rttr) throws Exception {
		
		// 1) 로그인 호스트
		if(!isProductOwner(dto.getHost_space_id(), session)) {
			rttr.addFlashAttribute("errorMessage", "수정 권한이 없습니다.");
    		return "redirect:" + spaceListUrl;
		}
		
		// 2) 호스트 정보 수정
		hostSpaceService.modifyHostSpace(dto, imageFiles, loadImages, changeImages);
		
		// 원래 상태의 목록으로 주소 이동 작업.
		addSearchAndPagingParams(cri, rttr);
		return "redirect:" + spaceListUrl;
	}
	
	// 호스트 공간 삭제
	@GetMapping("/deleteSpace")
	public String deleteSpace(Integer host_space_id, SearchCriteria cri, 
			HttpSession session, RedirectAttributes rttr) throws Exception {
		
		if(!isProductOwner(host_space_id, session)) {
			rttr.addFlashAttribute("errorMessage", "삭제 권한이 없습니다.");
    		return "redirect:" + spaceListUrl;
		}
		
		hostSpaceService.deleteHostSpace(host_space_id);
		
		addSearchAndPagingParams(cri, rttr);
		return "redirect:" + spaceListUrl;
	}
	
	// 체크박스 선택된 공간 삭제
	@PostMapping("/selectedDelete")
	@ResponseBody
	public ResponseEntity<Object> selectedDelete(@RequestBody int[] host_space_id_arr) {
		
		log.info("선택된 아이디 : " + Arrays.toString(host_space_id_arr));
		hostSpaceService.selectedDelete(host_space_id_arr);
		return new ResponseEntity<Object>(HttpStatus.OK);
	}
	
	// 상품 유저 아이디와 로그인 유저 아이디 비교
    private boolean isProductOwner(int host_space_id, HttpSession session) throws Exception {
		GuestDTO guestDTO = ((GuestDTO)session.getAttribute("login_auth"));
		String space_user_id = hostSpaceService.getHostSpaceUserIdById(host_space_id);
		return guestDTO.getUser_id().equals(space_user_id);
    }

    // 검색 조건 및 페이징 정보 유지
    private void addSearchAndPagingParams(SearchCriteria cri, RedirectAttributes rttr) {
		rttr.addAttribute("page", cri.getPage());
		rttr.addAttribute("perPageNum", cri.getPerPageNum());
		rttr.addAttribute("searchType", cri.getSearchType());
		rttr.addAttribute("keyword", cri.getKeyword());
		rttr.addAttribute("start_date", cri.getStart_date());
		rttr.addAttribute("end_date", cri.getEnd_date());
		rttr.addAttribute("visible_status", cri.getVisible_status());
    }
	

	
}
