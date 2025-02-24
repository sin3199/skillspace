package com.skillspace.sgs.host.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.skillspace.sgs.admin.images.ImagesDTO;
import com.skillspace.sgs.host.space.HostSpaceDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class HostProductDTO {

	private Integer         product_id;
    private Integer         host_space_id;
    private String 		    user_id;
    private String          name;
    private String          product_intro;
    private BigDecimal      price;
    private LocalDateTime   created_at;
    private LocalDateTime   updated_at;
    private String 			is_visible;

    private List<ImagesDTO> images;
    private HostSpaceDTO    hostSpace;

}
