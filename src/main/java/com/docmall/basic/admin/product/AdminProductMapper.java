package com.docmall.basic.admin.product;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.docmall.basic.common.dto.Criteria;

public interface AdminProductMapper {

	// [상품등록]
	void pro_insert(ProductVo vo);
	
	//[상품리스트]
	List<ProductVo> pro_list(Criteria cri);
	
	//[전체 데이터 갯수]
	int getTotalCount(Criteria cri);
	
	// [상품수정 페이지]
	ProductVo pro_edit(Integer pro_num);

	// [상품수정 기능]
	void pro_edit_ok(ProductVo vo);
	
	// [상품삭제 기능]
	void pro_delete(Integer pro_num);
	
	// [체크상품 수정작업1] // 라면 3개 끓이는데 한개씩 끓이는 느낌
	void pro_checked_modify1(@Param("pro_num") Integer pro_num, @Param("pro_price") Integer pro_price, @Param("pro_buy") String integer);

	// [체크상품 수정작업2]  // 라면 3개 끓일때 한번에 끓이기
	void pro_checked_modify2(List<ProductDTO> pro_modify_list);
	
	
	
	
}
