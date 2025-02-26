package com.skillspace.sgs.host.product;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.skillspace.sgs.admin.images.ImagesService;
import com.skillspace.sgs.common.utils.SearchCriteria;
import com.skillspace.sgs.common.utils.SearchItem;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class HostProductService {

    private final HostProductMapper hostProductMapper;
    private final ImagesService imagesService;

    // 전체 상품 갯수
	public int getCountProductByUser_id(String user_id, SearchCriteria cri) {
		return hostProductMapper.getCountProductByUser_id(user_id, cri);
	}

	// 상품 목록 조회
	public List<HostProductDTO> productList(String user_id, SearchCriteria cri) {
		return hostProductMapper.productList(user_id, cri);
	}

	// 상품 등록
	@Transactional
	public void create(HostProductDTO dto, List<MultipartFile> imageFiles) {
		
		// 1) 상품 등록
		// mybatis의 useGeneratedKeys를 이용해 auto_increment id 값 가져오기
		hostProductMapper.create(dto);
		
		// 2) 이미지 업로드 및 db 저장
		if(imageFiles != null && !imageFiles.isEmpty()) {
			for(MultipartFile imageFile : imageFiles) {
				if(!imageFile.isEmpty()) {
					log.info("이미지 이름 : " + imageFile.getOriginalFilename());
					imagesService.image_upload(null, "products", dto.getProduct_id(), imageFile);
				}
			}
		}
		
	}

	// 상품 아이디로 상품과 이미지 조회
	public HostProductDTO getProductWithImagesById(Integer product_id) {
		return hostProductMapper.getProductWithImagesById(product_id);
	}

	// 상품 수정
	@Transactional
	public void modifyHostProduct(HostProductDTO dto, List<MultipartFile> imageFiles, 
			Integer[] loadImages, Integer[] changeImages) {
		
		// 1) 상품 수정
		hostProductMapper.modifyHostProduct(dto);
		
		// 2) 기존 이미지 변경 시 삭제
		log.info("기존 이미지 " + Arrays.toString(loadImages));
		log.info("수정 이미지 " + Arrays.toString(changeImages));
		for(int i=0; i<changeImages.length; i++) {
			if(loadImages[i] != null && !loadImages[i].equals(changeImages[i])) {
				imagesService.deleteImage(loadImages[i]);
			}
		}
		
		// 3) 이미지 업로드 및 db 저장
		if(imageFiles != null && !imageFiles.isEmpty()) {
			for(MultipartFile imageFile : imageFiles) {
				if(!imageFile.isEmpty()) {
					log.info("이미지 이름 : " + imageFile.getOriginalFilename());
					imagesService.image_upload(null, "products", dto.getProduct_id(), imageFile);
				}
			}
		}
	}

	// 상품의 유저 정보
	public String getProductUserIdById(Integer product_id) {
		return hostProductMapper.getProductUserIdById(product_id);
	}

	// 상품 삭제
	@Transactional
	public void deleteHostProduct(Integer product_id) {
		hostProductMapper.deleteHostProduct(product_id);
		imagesService.deleteImageByEntity("products", product_id);
	}

	// 체크박스 선택 삭제
	@Transactional
	public void selectedDelete(int[] product_id_arr) {
		// 1) 상품 삭제
		hostProductMapper.selectedDelete(product_id_arr);
		// 2) 이미지 삭제
		for(int product_id : product_id_arr) {
			imagesService.deleteImageByEntity("products", product_id);
		}
	}

}
