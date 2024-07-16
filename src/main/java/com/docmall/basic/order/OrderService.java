package com.docmall.basic.order;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.docmall.basic.cart.CartMapper;
import com.docmall.basic.payinfo.PayInfoMapper;
import com.docmall.basic.payinfo.PayInfoVo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

	private final OrderMapper orderMapper;
	private final PayInfoMapper payInfoMapper;
	private final CartMapper cartMapper;
	
	@Transactional	//@Transactional: 여기 코드에서 가장 중요한 부분. 트랜젝션은 원래 DB쪽 기능이다. 한 기능에서 DB와 관련된 여러 작업이 수행될때 사용한다. 하나의 기능이라도 이상이 있으면 진행하지 않고 되돌린다.
	public void order_process(OrderVo vo, String mbsp_id) {
		
		//1) 주문테이블(insert)
		vo.setMbsp_id(mbsp_id); //아이디 값을 넣는 작업
		orderMapper.order_insert(vo); // 이 기능이 실행되면서 주문번호가 들어있는 상태가 된다. 이 주문번호를 주문상세 테이블에 넣어 사용 할 수 있다. 이것이 가능한 이유는 참조 관계가 되기 때문이다.
		
		//2) 주문상세테이블(insert)
		orderMapper.orderDetail_insert(vo.getOrd_code(), mbsp_id);
		
		//3) 결제테이블(insert)
		PayInfoVo p_vo = PayInfoVo.builder()
				.ord_code(vo.getOrd_code())
				.p_price(vo.getOrd_price())
				.paymethod("kakaopay")
				.p_status("완납")
				.build();
				
		payInfoMapper.payInfo_insert(p_vo);
		
		//4) 장바구니 테이블(delete)
		cartMapper.cart_empty(mbsp_id);
		
	}
	
	
	
	
}
