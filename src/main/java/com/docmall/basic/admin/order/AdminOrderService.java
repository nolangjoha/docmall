package com.docmall.basic.admin.order;

import java.util.List;

import org.springframework.stereotype.Service;

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
	
	// [주문자 주문정보 가져오기]
	public OrderVo order_info(Long ord_code) {
		return adminOrderMapper.order_info(ord_code);
	}
	
	
	
	
}