package com.docmall.basic.order;

import org.apache.ibatis.annotations.Param;

public interface OrderMapper {

	//1) 주문테이블(insert)
	void order_insert(OrderVo vo);
	
	//2) 주문상세테이블(insert)  //장바구니테이블을 기반으로 주문상세 테이블에 데이터를 저장한다.
	void orderDetail_insert(@Param("ord_code") Long ord_code,@Param("mbsp_id") String mbsp_id);
	
	//3) 결제테이블(insert)
	
	
	//4) 장바구니 테이블(delete)
	
	
	
	
}
