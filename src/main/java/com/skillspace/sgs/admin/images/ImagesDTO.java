package com.skillspace.sgs.admin.images;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
public class ImagesDTO {
	private int				image_id;
	private String			entity_type;
	private int				entity_id;
	private String			image_up_folder;
	private String			image_name;
	private LocalDateTime	created_at;
}
