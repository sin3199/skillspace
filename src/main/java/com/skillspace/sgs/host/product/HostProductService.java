package com.skillspace.sgs.host.product;

import java.util.List;

import org.springframework.stereotype.Service;
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
	public int getCountProductByUser_id(String user_id) {
		return hostProductMapper.getCountProductByUser_id(user_id);
	}

	// 상품 목록 조회
	public List<HostProductDTO> productList(String user_id, SearchCriteria cri, SearchItem searchItem) {
		return hostProductMapper.productList(user_id, cri, searchItem);
	}

	// 상품 등록
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

}
