package com.skillspace.sgs.guest.review;

import java.time.LocalDateTime;
import java.util.List;

import com.skillspace.sgs.admin.images.ImagesDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReviewWithImageDTO {

    private Integer         review_id;
    private Integer         reservation_id;
    private Integer         rating;
    private String          review_text;
    private LocalDateTime   created_at;
    private LocalDateTime   updated_at;

    // host_space 정보
    private Integer         host_space_id;
    private String          main_title;

    // products 테이블 정보
    private String          product_name;

    // images 테이블 정보
    private List<ImagesDTO> images;
}
