package com.skillspace.sgs.host.space;

import java.time.LocalDateTime;
import java.util.List;

import com.skillspace.sgs.admin.category.CategoryDTO;
import com.skillspace.sgs.admin.images.ImagesDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class HostSpaceDTO {

	private Integer			host_space_id;
	private String 			user_id;
	private int 			cate_id;
	private String 			main_title;
	private String 			sub_title;
	private String 			space_intro;
	private String 			space_guide;
	private String 			space_zipcode;
	private String 			space_addr;
	private String 			space_addrdetail;
	private LocalDateTime 	created_at;
	private LocalDateTime 	updated_at;
	private String 			is_visible;
	
	private List<ImagesDTO> images;
	private CategoryDTO category;
	
}
