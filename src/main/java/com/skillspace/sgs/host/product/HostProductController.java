package com.skillspace.sgs.host.product;

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

import com.skillspace.sgs.common.utils.PageMaker;
import com.skillspace.sgs.common.utils.SearchCriteria;
import com.skillspace.sgs.guest.GuestDTO;
import com.skillspace.sgs.host.space.HostSpaceService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/host/product")
@RequiredArgsConstructor
@Controller
public class HostProductController {

    private final HostProductService hostProductService;
    private final HostSpaceService hostSpaceService;
    
    private String productListUrl = "/host/product/productList";
    
    // 상품 목록 페이지
    @GetMapping("/productList")
    public void productList(SearchCriteria cri,
        Model model, HttpSession session) throws Exception {
        
        // 1) 유저 공간 정보
		String user_id = ((GuestDTO)session.getAttribute("login_auth")).getUser_id();
		// 2) 상품 리스트 정보 가져오기(이미지 계층형 데이터로)
		
		if (cri.getPerPageNum() == 0) { // perPageNum이 0 인 경우 (초기 요청)
	        cri.setPerPageNum(5);	// 페이지 관리 상수값
	    }
		PageMaker pageMaker = new PageMaker();
		pageMaker.setDisplayPageNum(5);
		pageMaker.setCri(cri);
		pageMaker.setTotalCount(hostProductService.getCountProductByUser_id(user_id, cri));
		
		List<HostProductDTO> productList = hostProductService.productList(user_id, cri);
		productList.forEach(product_info -> {
			product_info.getImages().forEach(image_info -> {
				image_info.setImage_up_folder(image_info.getImage_up_folder().replace("\\", "/"));
			});
		});
		
		model.addAttribute("productList", productList);
		model.addAttribute("pageMaker", pageMaker);
    }
    
    // 상품 등록 폼
    @GetMapping("/createProduct")
    public void createProduct(Model model, HttpSession session) throws Exception {
    	
    	String user_id = ((GuestDTO)session.getAttribute("login_auth")).getUser_id();
    	model.addAttribute("spaceList", hostSpaceService.getHostSpaceByUserId(user_id));
    }
    
    // 상품 등록 
    @PostMapping("/createProduct")
    public String createProduct(HostProductDTO dto, List<MultipartFile> imageFiles,
    		HttpSession session) throws Exception {
    	
    	// 1) 로그인 호스트
		GuestDTO guestDTO = ((GuestDTO)session.getAttribute("login_auth"));
		dto.setUser_id(guestDTO.getUser_id());
		
		// 2) 상품 등록
		hostProductService.create(dto, imageFiles);
    	
    	return "redirect:" + productListUrl;
    }
    
    // 상품 수정 폼
    @GetMapping("/modifyProduct")
    public void modifyProduct(Integer product_id, Model model, HttpSession session,
    		@ModelAttribute("cri") SearchCriteria cri) throws Exception {
    	
    	String user_id = ((GuestDTO)session.getAttribute("login_auth")).getUser_id();
    	model.addAttribute("productDTO", hostProductService.getProductWithImagesById(product_id));
    	model.addAttribute("spaceList", hostSpaceService.getHostSpaceByUserId(user_id));
    }
    
    // 상품 수정
    @PostMapping("/modifyProduct")
    public String modifyProduct(HostProductDTO dto, SearchCriteria cri,
    		List<MultipartFile> imageFiles,	Integer[] loadImages, Integer[] changeImages, 
    		HttpSession session, RedirectAttributes rttr) throws Exception {
    	
    	// 1) 권한 비교
		if(!isProductOwner(dto.getProduct_id(), session)) {
			rttr.addFlashAttribute("errorMessage", "수정 권한이 없습니다.");
    		return "redirect:" + productListUrl;
		}
		
		// 2) 상품 수정
		hostProductService.modifyHostProduct(dto, imageFiles, loadImages, changeImages);
		
		// 3) 검색 조건 및 페이징 정보 유지
		addSearchAndPagingParams(cri, rttr);
    	
    	return "redirect:" + productListUrl;
    }
    
    // 상품 삭제
    @GetMapping("/deleteProduct")
    public String deleteProduct(Integer product_id, SearchCriteria cri,
    		HttpSession session, RedirectAttributes rttr) throws Exception {
    	
    	if(!isProductOwner(product_id, session)) {
			rttr.addFlashAttribute("errorMessage", "삭제 권한이 없습니다.");
    		return "redirect:" + productListUrl;
		}
    	
    	hostProductService.deleteHostProduct(product_id);
    	
    	addSearchAndPagingParams(cri, rttr);
    	
    	return "redirect:" + productListUrl;
    }
    
    // 체크박스 선택 상품 삭제
    @PostMapping("/selectedDelete")
    @ResponseBody
    public ResponseEntity<Object> selectedDelete(@RequestBody int[] product_id_arr) {
		
		log.info("선택된 아이디 : " + Arrays.toString(product_id_arr));
		hostProductService.selectedDelete(product_id_arr);
		return new ResponseEntity<Object>(HttpStatus.OK);
	} 
    
    // 상품 유저 아이디와 로그인 유저 아이디 비교
    private boolean isProductOwner(int product_id, HttpSession session) throws Exception {
		GuestDTO guestDTO = ((GuestDTO)session.getAttribute("login_auth"));
		String product_user_id = hostProductService.getProductUserIdById(product_id);
		return guestDTO.getUser_id().equals(product_user_id);
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
