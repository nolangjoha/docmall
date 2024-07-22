package com.docmall.basic.admin.order;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.docmall.basic.common.dto.Criteria;
import com.docmall.basic.order.OrderVo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminOrderService {

	private final AdminOrderMapper adminOrderMapper;
	
	// [주문목록 불러오기]
	public List<OrderVo> order_list(Criteria cri) {
		return adminOrderMapper.order_list(cri);
	}
	
	//[전체 주문데이터 갯수]
	public int getTotalCount(Criteria cri) {
		return adminOrderMapper.getTotalCount(cri);
	}
	
	// [주문자(수령인) 정보]
	public OrderVo order_info(Long ord_code) {
		return adminOrderMapper.order_info(ord_code);
	}
	
	// [주문상품 정보]
	public List<OrderDetailInfoVo> order_detail_info(Long ord_code) {
		return adminOrderMapper.order_detail_info(ord_code);
	}
	
	
	//[주문상품 개별삭제]
	@Transactional //이 어노테이션은 service계층에서만 사용해야 한다.
	public void order_product_delete(Long ord_code, int pro_num) {
		adminOrderMapper.order_product_delete(ord_code, pro_num);
	}
	
	//[주문기본정보 수정]
	public void order_basic_modify(OrderVo vo) {
		adminOrderMapper.order_basic_modify(vo);
	};
	
}
