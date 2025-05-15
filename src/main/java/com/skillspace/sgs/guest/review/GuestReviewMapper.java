package com.skillspace.sgs.guest.review;

import java.util.List;

import com.skillspace.sgs.common.utils.SearchCriteria;

public interface GuestReviewMapper {

    // 리뷰저장
    int createReview(ReviewDTO reviewDTO);

    // 유저 리뷰목록 조회
    List<ReviewWithImageDTO> getReviewListByUserId(String user_id, SearchCriteria cri);

    // 유저 리뷰 목록 갯수
    int getCountReviewListByUserId(String user_id);

    // 리뷰 수정
    void modifyReview(ReviewDTO dto);

    // 리뷰 아이디로 리뷰 조회
    ReviewDTO getReviewById(Integer reviewId);

    // 리뷰 삭제
    void deleteReview(Integer review_id);

}
