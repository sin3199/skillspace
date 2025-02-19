package com.skillspace.sgs.admin.images;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface ImagesMapper {

	// 이미지 업로드
	void image_upload(ImagesDTO dto);

	// entity_type, id 로 이미지 조회
	List<ImagesDTO> getImagesByEntity(@Param("entity_type") String entity_type, @Param("entity_id") int entity_id);
	
	// image_id로 이미지 조회
	ImagesDTO getImageById(Integer image_id);

	// 이미지 삭제
	void deleteImage(Integer image_id);

	// 항목별 이미지 전체 삭제
	void deleteImageByEntity(@Param("entity_type") String entity_type, @Param("entity_id") int entity_id);

}
