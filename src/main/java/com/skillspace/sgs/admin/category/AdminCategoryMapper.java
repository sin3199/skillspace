package com.skillspace.sgs.admin.category;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface AdminCategoryMapper {

	// 같은 level중 sort_order중 가장 큰값
	Integer getMaxSortOrderByLevel(int level);
	
	// 카테고리 저장
	void createCategory(CategoryDTO vo);

	// 1차 카테고리 조회
	List<CategoryDTO> getFirstCategory();

	// 서브 카테고리 조회
	List<CategoryDTO> getSubCategory(int cate_prtcode);
	
	// 모든 2차카테고리 조회
	List<CategoryDTO> getSubCategory_All();
	
	// 특정 카테고리 조회
	CategoryDTO getCategoryById(int cate_id);

	// 카테고리 정렬 순서 변경
	void updateSortOrder(@Param("cate_id") int cate_id, @Param("sort_order") int sort_order);

	// 하위 카테고리 삭제
	void deleteSubCategory(int cate_prtcode);
	
	// 카테고리 삭제
	void deleteCategory(int cate_id);

	// 카테고리 수정
	void updateCategory(CategoryDTO dto);


}
