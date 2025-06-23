package com.skillspace.sgs.host.review;

import java.util.List;

import org.springframework.stereotype.Service;

import com.skillspace.sgs.common.utils.SearchCriteria;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class HostReviewService {

    private final HostReviewMapper hostReviewMapper;

    // 유저 아이디로 조회하는 이용후기 목록
    public List<ReviewHostDTO> getreviewListByUserId(String user_id, SearchCriteria cri) {
        return hostReviewMapper.getReviewListByUserId(user_id, cri);
    }

    // 유저 아이디로 조회하는 이용후기 개수
    public int getCountreviewListByUserId(String user_id, SearchCriteria cri) {
        return hostReviewMapper.getCountReviewListByUserId(user_id, cri);
    }

    // 질문 ID로 소유자 User ID 조회
    public String getReviewOwnerUserId(Integer review_id) {
        return hostReviewMapper.getReviewOwnerUserId(review_id);
    }

    // 질문 삭제
    public boolean deleteReview(Integer review_id) {
        int deletedRows = hostReviewMapper.deleteReviewById(review_id);
        if (deletedRows > 0) {
            log.info("Review deleted successfully: review_id={}", review_id);
            return true;
        } else {
            log.warn("Failed to delete question or question not found: review_id={}", review_id);
            return false;
        }
    }

}
