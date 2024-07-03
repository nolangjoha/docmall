package com.docmall.basic.admin.product;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.docmall.basic.common.dto.Criteria;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminProductService {

	private final AdminProductMapper adminProductMapper;
	
	// [상품등록]
	public void pro_insert(ProductVo vo) {
		adminProductMapper.pro_insert(vo);
	}
	
	//[상품리스트]
	public List<ProductVo> pro_list(Criteria cri) {
		return adminProductMapper.pro_list(cri);
	}
	
	//[전체 데이터 갯수]
	public int getTotalCount(Criteria cri) {
		return adminProductMapper.getTotalCount(cri);
	}
	
	// [상품수정 페이지]
	public ProductVo pro_edit(Integer pro_num) {
		return adminProductMapper.pro_edit(pro_num);
	}

	// [상품수정기능] 
	public void pro_edit_ok(ProductVo vo) {
		adminProductMapper.pro_edit_ok(vo);
	}
	
	// [상품삭제 기능]
	public void pro_delete(Integer pro_num) {
		adminProductMapper.pro_delete(pro_num);
	}
	
	// [체크상품 수정작업1] 체크된 개수만큼 반복문이 동작하여, 커넥션 객체수가 진행 되기 때문에 성능적으로 권장할 사항은 아님. 
	// 서버는 DB연동에 가장 부하를 많이 받는다. 아래와 같이 진행한 이유는 관리자 쪽에서 수정이 빈번하게 일어나지 않기 때문이다. 
	// 하지만 사용자에서 요청되는 작업인 경우에는 많은 사용자로 인해 성능에 좋지 않다.
	public void pro_checked_modify1(List<Integer> pro_num_arr, List<Integer> pro_price_arr, List<String> pro_buy_arr) {
		
		// 체크된 갯수만큼 for문이 반복
		for(int i=0; i<pro_num_arr.size(); i++) {
			adminProductMapper.pro_checked_modify1(pro_num_arr.get(i), pro_price_arr.get(i), pro_buy_arr.get(i));
		}
	}
	
	// [체크상품 수정작업2]  // 라면 3개 끓일때 한번에 끓이기
	public void pro_checked_modify2(List<Integer> pro_num_arr, List<Integer> pro_price_arr, List<String> pro_buy_arr) {
	
		List<ProductDTO> pro_modify_list = new ArrayList<>();
		
		for(int i=0; i<pro_num_arr.size(); i++) {
			ProductDTO productDTO = new ProductDTO(pro_num_arr.get(i), pro_price_arr.get(i), pro_buy_arr.get(i));
					
			pro_modify_list.add(productDTO);
		}
		adminProductMapper.pro_checked_modify2(pro_modify_list);
	}
	
}
