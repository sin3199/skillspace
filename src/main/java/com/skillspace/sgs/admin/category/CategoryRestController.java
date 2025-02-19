package com.skillspace.sgs.admin.category;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@RestController
public class CategoryRestController {
	
	private final AdminCategoryService adminCategoryService;
	
	// 카테고리 생성
	@PostMapping
    public ResponseEntity<String> createCategory(@RequestBody CategoryDTO dto) throws Exception {
		
        adminCategoryService.createCategory(dto);
        return new ResponseEntity<>("success", HttpStatus.CREATED);
    }
	
	// 1차 카테고리 조회
	@GetMapping("/first")
	public ResponseEntity<List<CategoryDTO>> getFirstCategory() throws Exception {
		
		return new ResponseEntity<List<CategoryDTO>>(adminCategoryService.getFirstCategory(), HttpStatus.OK);
	}
	
	// 서브 카테고리 조회
	@GetMapping("/sub/{cate_prtcode}")
	public ResponseEntity<List<CategoryDTO>> getSubCategory(@PathVariable("cate_prtcode") int cate_prtcode) throws Exception {
		
		return new ResponseEntity<List<CategoryDTO>>(adminCategoryService.getSubCategory(cate_prtcode), HttpStatus.OK);
	}
	
	// 모든 2차 카테고리 조회
	@GetMapping("/sub")
	public ResponseEntity<List<CategoryDTO>> getSubCategory_All(){
		
		return new ResponseEntity<List<CategoryDTO>>(adminCategoryService.getSubCategory_All(), HttpStatus.OK);
	}
	
	// 카테고리 삭제
	@DeleteMapping("/{cate_id}")
	public ResponseEntity<String> deleteCategory(@PathVariable("cate_id") int cate_id) throws Exception {
		
		adminCategoryService.deleteCategory(cate_id);
		return new ResponseEntity<String>("success", HttpStatus.OK);
	}
	
	// 카테고리 순서 변경
	@PostMapping("/swap")
	public ResponseEntity<String> swapCategory(@RequestBody Map<String, Integer> request) {
        int current_cate_id = request.get("current_cate_id");
        int target_cate_id  = request.get("target_cate_id");

    	adminCategoryService.swapSortOrder(current_cate_id, target_cate_id);
        return new ResponseEntity<String>("success", HttpStatus.OK);
    }
	
	// 카테고리 수정
	@PutMapping
	public ResponseEntity<String> updateCategory(@RequestBody CategoryDTO dto) throws Exception {
		
		adminCategoryService.updateCategory(dto);
		return new ResponseEntity<String>("success", HttpStatus.OK);
	}
	

}
