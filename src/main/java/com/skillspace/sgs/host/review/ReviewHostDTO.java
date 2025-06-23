package com.skillspace.sgs.host.review;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.skillspace.sgs.admin.images.ImagesDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReviewHostDTO {

    private Integer         review_id;
    private Integer         reservation_id;
    private Integer         rating;
    private String          review_text;
    private LocalDateTime   created_at;
    private LocalDateTime   updated_at;

    private String          user_nick;      // 이용후기 작성자 닉네임
    
    private List<ImagesDTO> images;         // images 테이블 정보
    private Map<String, Object> reply;      // 답변 테이블 정보
    private Map<String, Object> hostSpace;  // 공간 테이블 정보

}
