package com.skillspace.sgs.host.review;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.skillspace.sgs.common.utils.SearchCriteria;

public interface HostReviewMapper {

    // 유저 아이디로 조회하는 이용후기 목록
    List<ReviewHostDTO> getReviewListByUserId(
            @Param("user_id") String user_id,
            @Param("cri") SearchCriteria cri);

    // 유저 아이디로 조회하는 이용후기 개수
    int getCountReviewListByUserId(
            @Param("user_id") String user_id,
            @Param("cri") SearchCriteria cri);

    // 질문 ID로 해당 질문이 속한 공간의 소유자 user_id 조회
    String getReviewOwnerUserId(Integer review_id);

    // 질문 ID로 질문 삭제
    int deleteReviewById(Integer review_id);
            
}
