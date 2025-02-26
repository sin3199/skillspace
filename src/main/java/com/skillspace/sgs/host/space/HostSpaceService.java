package com.skillspace.sgs.host.space;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
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
public class HostSpaceService {
	
	@Value("${com.sgs.upload.path}")
	private String uploadPath;	// 파일 업로드할 기본 폴더
	
	private final HostSpaceMapper hostSpaceMapper;
	private final ImagesService imagesService;
	
	// 공간 등록
	@Transactional
	public void create(HostSpaceDTO dto, List<MultipartFile> imageFiles) {
		
		// 1) 호스트 공간 정보 저장
		// mybatis의 useGeneratedKeys를 이용해 auto_increment id 값 가져오기
		hostSpaceMapper.create(dto);
		
		// 2) 이미지 업로드 및 db 저장
		if(imageFiles != null && !imageFiles.isEmpty()) {
			for(MultipartFile imageFile : imageFiles) {
				if(!imageFile.isEmpty()) {
					log.info("이미지 이름 : " + imageFile.getOriginalFilename());
					imagesService.image_upload(null, "host_space", dto.getHost_space_id(), imageFile);
				}
			}
		}
	}
	
	// 공간 아이디로 조회
	public HostSpaceDTO getHostSpaceWithCateAndImagesById(Integer host_space_id) {
		return hostSpaceMapper.getHostSpaceWithCateAndImagesById(host_space_id);
	}
	
	// 공간 목록 조회
	public List<HostSpaceDTO> sapceList(String user_id, SearchCriteria cri) {
		return hostSpaceMapper.spaceList(user_id, cri);
	}
	
	// 전체 공간 게시물 갯수 조회
	public int getCountSpaceByUser_id(String user_id, SearchCriteria cri) {
		return hostSpaceMapper.getCountSpaceByUser_id(user_id, cri);
	}

	// 호스트 공간 수정
	@Transactional
	public void modifyHostSpace(HostSpaceDTO dto, List<MultipartFile> imageFiles, Integer[] loadImages,
			Integer[] changeImages) {
		
		// 1) 호스트 공간 수정
		hostSpaceMapper.modifyHostSpace(dto);
		
		// 2) 기존 이미지 변경 시 삭제
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
					imagesService.image_upload(null, "host_space", dto.getHost_space_id(), imageFile);
				}
			}
		}
	}

	// 호스트 공간 삭제
	@Transactional
	public void deleteHostSpace(Integer host_space_id) {
		// 1) 공간 삭제
		hostSpaceMapper.deleteHostSpace(host_space_id);
		// 2) 이미지 삭제
		imagesService.deleteImageByEntity("host_space", host_space_id);
	}

	// 선택한 공간 삭제
	@Transactional
	public void selectedDelete(int[] host_space_id_arr) {
		
		// 1) 공간 삭제
		hostSpaceMapper.selectedDelete(host_space_id_arr);
		// 2) 이미지 삭제
		for(int host_space_id : host_space_id_arr) {
			imagesService.deleteImageByEntity("host_space", host_space_id);
		}
	}

	public List<HostSpaceDTO> getHostSpaceByUserId(String user_id) {
		return hostSpaceMapper.getHostSpaceByUserId(user_id);
	}

	// 공간의 유저 정보
	public String getHostSpaceUserIdById(int host_space_id) {
		return hostSpaceMapper.getHostSpaceUserIdById(host_space_id);
	}

}
