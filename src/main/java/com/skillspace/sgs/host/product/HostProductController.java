package com.skillspace.sgs.host.product;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import com.skillspace.sgs.common.utils.PageMaker;
import com.skillspace.sgs.common.utils.SearchCriteria;
import com.skillspace.sgs.common.utils.SearchItem;
import com.skillspace.sgs.guest.GuestDTO;
import com.skillspace.sgs.host.space.HostSpaceDTO;
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

    // 상품 목록 페이지
    @GetMapping("/productList")
    public void productList(SearchCriteria cri,
        @ModelAttribute("searchItem") SearchItem searchItem,
        Model model, HttpSession session) throws Exception {
        
        // 1) 유저 공간 정보
		String user_id = ((GuestDTO)session.getAttribute("login_auth")).getUser_id();
		// 2) 상품 리스트 정보 가져오기(이미지 계층형 데이터로)
		PageMaker pageMaker = new PageMaker();
		pageMaker.setDisplayPageNum(2);
		pageMaker.setCri(cri);
		pageMaker.setTotalCount(hostProductService.getCountProductByUser_id(user_id));
		
		List<HostProductDTO> productList = hostProductService.productList(user_id, cri, searchItem);
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
    	
    	return "redirect:/host/product/productList";
    }

}
