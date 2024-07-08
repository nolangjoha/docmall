package com.docmall.basic.review;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.docmall.basic.common.dto.Criteria;

public interface ReviewMapper {

	// [상품후기 페이징] 상품에 해당하는 상품후기므로 상품코드가 들어가야 함.
	List<ReviewVo> rev_list(@Param("pro_num") Integer pro_num, @Param("cri") Criteria cri);
	
	// [리뷰 전체 데이터 갯수]  //만약 검색까지 들어간다면 Criteria까지 들어가야 함.
	int getCountReviewByPro_num(Integer pro_num);
	
	
	
	
}