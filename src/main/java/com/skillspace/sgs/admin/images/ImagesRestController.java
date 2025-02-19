package com.skillspace.sgs.admin.images;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.skillspace.sgs.common.utils.FileUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/api/image")
@RequiredArgsConstructor
@RestController
public class ImagesRestController {
	
	private final ImagesService imagesService;
	
	// 이미지 관련 작업기능
	private final FileUtils fileUtils;
	
	@Value("${com.sgs.upload.path}")
	private String uploadPath;
	
	// 이미지 저장
	@PostMapping("/upload")
	public ResponseEntity<String> image_upload(String entity_type, Integer entity_id, 
			Integer image_id, MultipartFile image_file) throws Exception {
		
		imagesService.image_upload(image_id, entity_type, entity_id, image_file);
		
		return new ResponseEntity<String>("success", HttpStatus.CREATED);
	}
	
	// 이미지 파일 조회
	@GetMapping("/{entity_type}/{entity_id}")
    public ResponseEntity<List<ImagesDTO>> getImages(
    		@PathVariable String entity_type,
            @PathVariable int entity_id) {
		
		log.info("이미지 파일 조회 : " + entity_id + ", " + entity_type);
		List<ImagesDTO> image_list = imagesService.getImagesByEntity(entity_type, entity_id);
		
		// 폴더 역슬래쉬를 슬래쉬로 변경
		image_list.forEach((image_info)->{
			image_info.setImage_up_folder(image_info.getImage_up_folder().replace("\\", "/"));
		});
		
		
        return new ResponseEntity<List<ImagesDTO>>(image_list, HttpStatus.OK);
    }
	
	// 이미지 삭제
	@DeleteMapping("/{image_id}")
	public ResponseEntity<String> deleteImage(@PathVariable Integer image_id) throws Exception {
		
		imagesService.deleteImage(image_id);
		return new ResponseEntity<String>("success", HttpStatus.OK);
	}
	
	// 이미지 출력
	@GetMapping("/display/{entity_type}/{year}/{month}/{day}/{fileName}")
	public ResponseEntity<byte[]> image_display(
			@PathVariable String entity_type, 
			@PathVariable String year, 
			@PathVariable String month, 
			@PathVariable String day, 
			@PathVariable String fileName) throws Exception {
		
		String folderName = uploadPath + File.separator + 
							entity_type + File.separator + 
							year + File.separator + 
							month + File.separator + 
							day;
		
		return fileUtils.getFile(folderName, fileName);
	}
}
