package com.docmall.basic.review;

import java.util.List;

import org.springframework.stereotype.Service;

import com.docmall.basic.common.dto.Criteria;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewMapper reviewMapper;
	
	// [상품후기 페이징] 상품에 해당하는 상품후기므로 상품코드가 들어가야 함.
	public List<ReviewVo> rev_list(Integer pro_num,Criteria cri) {
		return reviewMapper.rev_list(pro_num, cri);
	}
	
	// [리뷰 전체 데이터 갯수]  //만약 검색까지 들어간다면 Criteria까지 들어가야 함.
	public int getCountReviewByPro_num(Integer pro_num) {
		return reviewMapper.getCountReviewByPro_num(pro_num);
	}
	
	
	// [리뷰 저장]
	public void review_save(ReviewVo vo) {
		reviewMapper.review_save(vo);
	};
	
	
	//[리뷰삭제]
	public void review_delete(Long re_code) {
		reviewMapper.review_delete(re_code);
	}
	
	
}
