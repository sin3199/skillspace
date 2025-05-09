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



}
