package com.skillspace.sgs.guest.review;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReviewDTO {

    private Integer         review_id;
    private Integer         reservation_id;
    private Integer         rating;
    private String          review_text;
    private LocalDateTime   created_at;
    private LocalDateTime   updated_at;

}
