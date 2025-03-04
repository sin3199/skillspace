package com.skillspace.sgs.admin.category;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CategoryTreeDTO {
	
	private int 	cate_id;
	private Integer cate_prtcode;
	private String 	cate_name;
	private int		level;
	private int		sort_order;
	
	private List<CategoryDTO> subCategories;
	
}
