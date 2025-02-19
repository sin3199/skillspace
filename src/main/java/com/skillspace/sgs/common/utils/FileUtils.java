package com.skillspace.sgs.common.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import net.coobird.thumbnailator.Thumbnails;

// 파일업로드와 관련된 작업.
// FileUtils 클래스를 service 패키지에서 관리하여 사용할 수도 있고,
// 아니면 아래처럼 @Component 어노테이션을 적용하여 사용할 수도 있다.  
@Component // 스프링부트가 시작되면 bean으로 생성되어 진다.
public class FileUtils {

	// 기본 : 파일업로드시 날짜 폴더를 생성하여 업로드 한다.

	// 파일업로드시 날짜폴더이름 형식
	public String getDateFolder() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Date date = new Date(); // 현재 날짜와 시간
		String str = sdf.format(date); // "2024-11-18"

		// File.separator : 현재 운영체제의 파일경로 구분자
		// 1) 리눅스 / : "2024/11/18" 2) 윈도우즈 \ : "2024\11\18
		return str.replace("-", File.separator);
	}

	// File 클래스 : 파일 또는 폴더 작업할 때 사용
	// 업로드되는 파일구분(이미지파일 or 일반파일)
	public boolean checkImageType(File saveFile) {
		boolean isImageType = false;

		try {
			// 업로드 되는 파일의 Mine Type 정보
			// text/html, text/plain, image/jpeg, image/jpg, image/png ...
			String contentType = Files.probeContentType(saveFile.toPath());
			// contentType 변수안에 내용이 image 라는 문자열로 되어있으면 true 아니면 false
			isImageType = contentType.startsWith("image");
		} catch (Exception ex) {

		}

		return isImageType;
	}

	// 파일업로드 작업 및 실제 업로드에 사용한 파일명을 리턴
	public String uploadFile(String uploadFolder, String dateFolder, MultipartFile uploadFile) {

		String realUploadFileName = "";

		// 업로드 할 폴더 file 객체.
		File file = new File(uploadFolder, dateFolder); // 예> "C:\\Dev\\upload\\pds" " 2024\11\18"

		// "C:\\Dev\\upload\\pds\\2024\\11\\18" 폴더
		if (file.exists() == false) {
			file.mkdirs(); // 하위폴더까지 폴더 생성 "C:\\Dev\\upload\\pds\\2024\\11\\18"
		}

		// 클라이언트에서 보낸 파일명
		String clientFileName = uploadFile.getOriginalFilename(); // abc.gif
		UUID uuid = UUID.randomUUID(); // 2f48f241-9d64-4d16-bf56-70b9d4e0e79a

		// 2f48f241-9d64-4d16-bf56-70b9d4e0e79a_abc.gif
		realUploadFileName = uuid.toString() + "_" + clientFileName;

		// 파일업로드 작업
		try {
			File saveFile = new File(file, realUploadFileName);
			uploadFile.transferTo(saveFile); // 파일 복사

			// 썸네일 작업을 위한 이미지 파일체크
			if (checkImageType(saveFile)) {
				File thumnailFile = new File(file, "s_" + realUploadFileName);

				BufferedImage bo_img = ImageIO.read(saveFile);
				double ratio = 3;
				int width = (int) (bo_img.getWidth() / ratio);
				int height = (int) (bo_img.getHeight() / ratio);

				Thumbnails.of(saveFile).size(width, height).toFile(thumnailFile);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return realUploadFileName;
	}

	// 업로드된 파일이 프로젝트 내부에 존재하는 것이 아니라, 외부의 일반 폴더에 있을 때,
	// 보안적인 이슈가 있어, 일반폴더에 있는 파일들을 바이트 배열로 읽어서 클라이언트로 전송한다.
	// 프로젝트 내부의 이미지 파일. <img src="abc.gif">
	// 프로젝트 외부(일반폴더)의 이미지 파일. <img src="매핑주소">
	public ResponseEntity<byte[]> getFile(String uploadPath, String fileName) throws Exception {
		ResponseEntity<byte[]> entity = null;

		File file = new File(uploadPath, fileName);

		if (!file.exists()) {
			return entity;
		}

		// 서버에 파일을 클라이언트로 보낼 때 파일에 대한 MIME TYPE 정보를 헤더에 추가하는 작업
		HttpHeaders headers = new HttpHeaders();
		// Files.probeContentType(file.toPath()) -> MIME TYPE 정보 예> image/jpeg,
		// image/gif ...
		headers.add("Content-Type", Files.probeContentType(file.toPath()));

		entity = new ResponseEntity<byte[]>(FileCopyUtils.copyToByteArray(file), headers, HttpStatus.OK);
		// Byte 데 이 터 , 헤더 , 200번 상태 코드

		return entity;
	}

	// uploadpath : C:\\Dev\\upload\\pds
	// dateFolderName : 2024\11\19
	// fileName : bb714db2-5b3b-45b7-91cc-e0bff2302359_kotlin.jpg
	// type : image 체크
	public void delete(String uploadpath, String dateFolderName, String fileName, String type) {

		// 아래 코드를 실행하는 운영체제에 따라서 구분자를 사용하기 위한 목적.
		// 윈도우즈 : \ 리눅스 : /
		// 원본 파일 삭제
		File file1 = new File((uploadpath + "\\" + dateFolderName + "\\" + fileName).replace("\\", File.separator));
		if (file1.exists())
			file1.delete();

		// "s_" + fileName = s_bb714db2-5b3b-45b7-91cc-e0bff2302359_kotlin.jpg
		if (type.equals("image")) { // 썸네일 이미지 파일 삭제
			File file2 = new File(
					(uploadpath + "\\" + dateFolderName + "\\" + "s_" + fileName).replace("\\", File.separator));
			if (file2.exists())
				file2.delete();

		}
	}

}
