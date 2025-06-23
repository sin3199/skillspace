package com.skillspace.sgs.host.reply;

import java.time.LocalDateTime;

import groovy.transform.ToString;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ToString
public class ReplyDTO {

    private Integer         reply_id;
    private Integer         review_id;
    private String          reply_text;
    private LocalDateTime   created_at;
    private LocalDateTime   updated_at;
}
