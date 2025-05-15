package com.skillspace.sgs.admin.images;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.skillspace.sgs.common.utils.FileUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ImagesService {
	
	private final ImagesMapper imagesMapper;
	
	private final FileUtils fileUtils;
	
	@Value("${com.sgs.upload.path}")
	private String uploadPath;
	
	// 이미지 업로드
	@Transactional
	public void image_upload(Integer image_id, String entity_type, Integer entity_id, MultipartFile image_file) {
		
		// 기존 이미지 있을 경우 (기존 이미지 삭제 후 이미지 업로드)
		if(image_id != null) {
			deleteImage(image_id);
		}
		
		// 1) 이미지 파일 업로드 작업
		String dateFolder   = fileUtils.getDateFolder();	// 상품 이미지 업로드 되는 날짜 폴더 이름 2025/01/01
		dateFolder = entity_type + File.separator + dateFolder;
		String saveFileName = fileUtils.uploadFile(uploadPath, dateFolder, image_file);	// 파일 복사 및 썸네일 이미지 생성
		
		// 2) db 저장
		ImagesDTO dto = new ImagesDTO();
		dto.setEntity_type(entity_type);
		dto.setEntity_id(entity_id);
		dto.setImage_up_folder(dateFolder);
		dto.setImage_name(saveFileName);
		
		imagesMapper.image_upload(dto);
		
	}
	
	

	// 항목별 이미지 목록 조회
	public List<ImagesDTO> getImagesByEntity(String entity_type, int entity_id) {
		return imagesMapper.getImagesByEntity(entity_type, entity_id);
	}

	// 이미지 삭제
	public void deleteImage(Integer image_id) {
		ImagesDTO cur_imageDTO = imagesMapper.getImageById(image_id);
		fileUtils.delete(uploadPath, cur_imageDTO.getImage_up_folder(), cur_imageDTO.getImage_name(), "image");
		imagesMapper.deleteImage(image_id);
	}
	
	// 항목별 이미지 전체 삭제
	@Transactional
	public void deleteImageByEntity(String entity_type, int entity_id) {
		// 1) 저장소에 삭제할 이미지 아이디 조회
		List<ImagesDTO> images = imagesMapper.getImagesByEntity(entity_type, entity_id);
		
		// 2) 이미지 존재 하면 삭제
		if(!images.isEmpty()) {
			imagesMapper.deleteImageByEntity(entity_type, entity_id);
			for(ImagesDTO image : images ) {
				fileUtils.delete(uploadPath, image.getImage_up_folder(), image.getImage_name(), "image");
			}
		}
	}
	
	

}
