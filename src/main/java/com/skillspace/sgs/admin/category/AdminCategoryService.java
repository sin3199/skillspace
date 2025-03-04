package com.skillspace.sgs.admin.category;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class AdminCategoryService {
	
	private final AdminCategoryMapper adminCategoryMapper;
	
	// 카테고리 생성
	public void createCategory(CategoryDTO vo) {
		// 정렬 기준하는 제일 큰 값(level 1 일때 100, 200, 300시 300값)
		int level = vo.getLevel();
		Integer maxSortOrder = adminCategoryMapper.getMaxSortOrderByLevel(vo.getLevel());
		
		log.info("일 레벨 대비 제일 큰 정렬 값 : level : " + level +" 정렬 값 : " + maxSortOrder);
		// level 1: 0부터, level 2: 2000부터 시작
		if(maxSortOrder == null || maxSortOrder == 0) maxSortOrder = (level == 1) ? 0 : 2000;
		log.info("이 레벨 대비 제일 큰 정렬 값 : level : " + level +" 정렬 값 : " + maxSortOrder);
		
		
		vo.setSort_order(maxSortOrder + 100);
		
		adminCategoryMapper.createCategory(vo);
	}

	// 1차 카테고리 조회
	public List<CategoryDTO> getFirstCategory() {
		return adminCategoryMapper.getFirstCategory();
	}
	
	// 서브 카테고리 조회
	public List<CategoryDTO> getSubCategory(int cate_prtcode) {
		return adminCategoryMapper.getSubCategory(cate_prtcode);
	}
	
	// 모든 2차카테고리 조회
	public List<CategoryDTO> getSubCategory_All() {
		return adminCategoryMapper.getSubCategory_All();
	}

	// 카테고리 정렬순서 변경
	@Transactional
    public void swapSortOrder(int current_cate_id, int target_cate_id) {
        // 현재 카테고리와 타겟 카테고리의 sort_order 값 교환
        CategoryDTO currentCategory = adminCategoryMapper.getCategoryById(current_cate_id);
        CategoryDTO targetCategory = adminCategoryMapper.getCategoryById(target_cate_id);

        int tempSortOrder = currentCategory.getSort_order();
        adminCategoryMapper.updateSortOrder(currentCategory.getCate_id(), targetCategory.getSort_order());
        adminCategoryMapper.updateSortOrder(targetCategory.getCate_id(), tempSortOrder);
    }

	// 카테고리 삭제
	@Transactional
	public void deleteCategory(int cate_id) {
		// 하위 카테고리 삭제
		adminCategoryMapper.deleteSubCategory(cate_id);
		// 카테고리 삭제
		adminCategoryMapper.deleteCategory(cate_id);
	}

	// 카테고리 수정
	public void updateCategory(CategoryDTO dto) {
		adminCategoryMapper.updateCategory(dto);
	}
	
	// 계층형으로 모든 카테고리 조회
	public List<CategoryTreeDTO> getAllCategoryTree() {
		return adminCategoryMapper.getAllCategoryTree();
	}
	
	// 특정 카테고리 조회
	public CategoryDTO getCategoryById(int cate_id) {
		return adminCategoryMapper.getCategoryById(cate_id);
	}
	

}
