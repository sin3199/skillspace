package com.skillspace.sgs.guest.review;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.skillspace.sgs.admin.images.ImagesService;
import com.skillspace.sgs.common.utils.SearchCriteria;
import com.skillspace.sgs.guest.reserve.GuestReserveMapper;
import com.skillspace.sgs.guest.reserve.ReservationDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
public class GuestReviewService {

    private final GuestReviewMapper guestReviewMapper;
    private final GuestReserveMapper guestReserveMapper;
    private final ImagesService imagesService;


    // 리뷰 등록 process
    @Transactional
    public void createReview(ReviewDTO reviewDTO, List<MultipartFile> imageFiles, String loggedInUserId)
            throws IOException, IllegalArgumentException, IllegalStateException, NoSuchElementException {

        // 1. 예약 정보 조회
        Integer reservation_id = reviewDTO.getReservation_id();
        ReservationDTO reservation = guestReserveMapper.getReservationById(reservation_id);
        if (reservation == null) {
            throw new NoSuchElementException("리뷰를 작성할 예약 정보를 찾을 수 없습니다. ID: " + reviewDTO.getReservation_id());
        }

        // 2. 예약자와 리뷰 작성자 일치 여부 확인
        if (!reservation.getUser_id().equals(loggedInUserId)) {
            throw new IllegalArgumentException("해당 예약에 대한 리뷰를 작성할 권한이 없습니다.");
        }

        // 3. 예약 상태 확인 (예: '이용완료' 상태인지)
        if (!"이용완료".equals(reservation.getStatus())) {
            throw new IllegalStateException("리뷰는 이용완료 상태에서만 작성 가능합니다.");
        }

        // 4. 이미 리뷰가 작성되었는지 확인
        if ("Y".equalsIgnoreCase(reservation.getIs_review())) { 
            throw new IllegalStateException("이미 리뷰가 작성된 예약입니다.");
        }

        // 5. Review 정보 DB 저장

        // insert 메소드가 생성된 review_id를 DTO에 설정하도록 XML Mapper 설정 필요 (useGeneratedKeys)
        int insertedReviewCount = guestReviewMapper.createReview(reviewDTO);
        if (insertedReviewCount == 0) {
            throw new RuntimeException("리뷰 정보 저장에 실패했습니다.");
        }
        Integer savedReviewId = reviewDTO.getReview_id(); // ReviewDTO에 reviewId 필드 및 getter/setter 필요
        if (savedReviewId == null) {
            throw new RuntimeException("리뷰 ID를 가져오는데 실패했습니다. (MyBatis 설정 확인 필요)");
        }
        log.info("리뷰 저장 완료. Review ID: {}", savedReviewId);


        // 6. 이미지 파일 저장 및 ReviewImage 정보 DB 저장
        if (imageFiles != null && !imageFiles.isEmpty()) {
            for (MultipartFile imageFile : imageFiles) {
                if (imageFile != null && !imageFile.isEmpty()) {
                    imagesService.image_upload(null, "reviews", savedReviewId, imageFile);
                    log.info("리뷰 이미지 저장 완료: {}", imageFile.getOriginalFilename());
                }
            }
        }

        // 7. Reservation 상태 업데이트 
        int updatedRows = guestReserveMapper.updateReviewStatus(reservation_id, "Y");
        if (updatedRows == 0) {
            throw new RuntimeException("예약 상태 업데이트에 실패했습니다.");
        }
        log.info("예약 정보 업데이트 완료 (리뷰 작성됨). Reservation ID: {}", reservation_id);
    }

    // 유저 리뷰 목록 조회
    public List<ReviewWithImageDTO> getReviewListByUserId(String user_id, SearchCriteria cri) {
        return guestReviewMapper.getReviewListByUserId(user_id, cri);
    }

    // 유저 리뷰 목록 갯수
    public int getCountReviewListByUserId(String user_id) {
        return guestReviewMapper.getCountReviewListByUserId(user_id);
    }

    // 리뷰 수정
    @Transactional
    public void modifyReview(ReviewDTO reviewDTO, List<MultipartFile> new_images, List<Integer> delete_image_ids, String loggedInUserId)
            throws IOException, IllegalArgumentException, NoSuchElementException {

        // 0. 리뷰 ID 가져오기
        Integer reviewId = reviewDTO.getReview_id();
        if (reviewId == null) {
            throw new IllegalArgumentException("수정할 리뷰 ID가 없습니다.");
        }

        // 1. 기존 리뷰 정보 조회 (작성자 확인 및 존재 여부 확인용)
        ReviewDTO existingReview = guestReviewMapper.getReviewById(reviewId);
        if (existingReview == null) {
            throw new NoSuchElementException("수정할 리뷰를 찾을 수 없습니다. ID: " + reviewId);
        }

        // 2. 리뷰 작성자와 현재 로그인 사용자 일치 여부 확인
        ReservationDTO reservation = guestReserveMapper.getReservationById(existingReview.getReservation_id());
        if (reservation == null || !reservation.getUser_id().equals(loggedInUserId)) {
            throw new IllegalArgumentException("해당 리뷰를 수정할 권한이 없습니다.");
        }

        // 3) 리뷰 수정
        guestReviewMapper.modifyReview(reviewDTO);
        log.info("리뷰 내용 수정 완료. Review ID: {}", reviewId);

        // 4) 이미지 삭제
        if(delete_image_ids != null && !delete_image_ids.isEmpty()) {
            for(Integer image_id : delete_image_ids) {
                imagesService.deleteImage(image_id);
                log.info("리뷰 이미지 삭제 완료. Image ID: {}, Review ID: {}", image_id, reviewId);
            }
        }

        // 5) 새로운 이미지 저장
        if(new_images != null && !new_images.isEmpty()) {
            for(MultipartFile imageFile : new_images) {
                if(imageFile != null && !imageFile.isEmpty()) {
                    imagesService.image_upload(null, "reviews", reviewId, imageFile);
                    log.info("새로운 리뷰 이미지 저장 완료: {}, Review ID: {}", imageFile.getOriginalFilename(), reviewId);
                }
            }
        }
    }

    // 리뷰 삭제
    @Transactional
    public void deleteReview(Integer review_id, String loggedInUserId) 
        throws IllegalArgumentException, NoSuchElementException {

        // 0. 리뷰 ID 가져오기
        if (review_id == null) {
            log.warn("리뷰 삭제 시도: 리뷰 ID가 null입니다.");
            throw new IllegalArgumentException("삭제할 리뷰 ID가 없습니다.");
        }
        log.info("리뷰 삭제 서비스 시작 - 리뷰 ID: {}, 사용자 ID: {}", review_id, loggedInUserId);

        // 1. 기존 리뷰 정보 조회 (작성자 확인 및 존재 여부 확인용)
        ReviewDTO existingReview = guestReviewMapper.getReviewById(review_id);
        if (existingReview == null) {
            log.warn("삭제할 리뷰를 찾을 수 없음 - 리뷰 ID: {}", review_id);
            throw new NoSuchElementException("삭제할 리뷰를 찾을 수 없습니다. ID: " + review_id);
        }

        // 2. 리뷰 작성자와 현재 로그인 사용자 일치 여부 확인
        ReservationDTO reservation = guestReserveMapper.getReservationById(existingReview.getReservation_id());
        if (reservation == null || !reservation.getUser_id().equals(loggedInUserId)) {
            log.warn("리뷰 삭제 권한 없음 - 리뷰 ID: {}, 예약자: {}, 요청자: {}", review_id, (reservation != null ? reservation.getUser_id() : "N/A"), loggedInUserId);
            throw new IllegalArgumentException("해당 리뷰를 삭제할 권한이 없습니다.");
        }

        // 3. 리뷰 삭제
        guestReviewMapper.deleteReview(review_id);
        log.info("리뷰 DB 삭제 완료 - 리뷰 ID: {}", review_id);

        // 4. 이미지 삭제
        imagesService.deleteImageByEntity("reviews", review_id);
        log.info("리뷰 관련 이미지 삭제 완료 - Entity Type: reviews, Entity ID: {}", review_id);

        // 5. 해당 예약의 is_review 상태를 'N'으로 변경
        int updatedRows = guestReserveMapper.updateReviewStatus(existingReview.getReservation_id(), "N");
        if (updatedRows == 0) {
            log.warn("리뷰 삭제 후 예약의 is_review 상태 업데이트 실패 - 예약 ID: {}", existingReview.getReservation_id());
        } else {
            log.info("예약의 is_review 상태 'N'으로 업데이트 완료 - 예약 ID: {}", existingReview.getReservation_id());
        }
        log.info("리뷰 삭제 서비스 성공 종료 - 리뷰 ID: {}", review_id);
    }

    // 공간 리뷰 목록 수 조회
    public int countReviewsBySpaceId(Integer host_space_id) {
        return guestReviewMapper.countReviewsBySpaceId(host_space_id);
    }

    // 공간 리뷰 목록 조회
    public List<ReviewResponseDTO> getReviewsBySpaceId(Integer host_space_id, SearchCriteria cri) {
        return guestReviewMapper.getReviewsBySpaceId(host_space_id, cri);
    }

}
