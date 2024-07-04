package com.docmall.basic.cart;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CartVo {

//	-- cart_tbl
//	-- cart_code, pro_num, mbsp_id, cart_amount, cart_date

	private Integer cart_code;  //장바구니는 데이터가 많이 발생하니까 / long으로 해도 큰 문제는 없다.
	private int pro_num;
	private String mbsp_id;
	private int cart_amount;
	private Date cart_date;
	//Data클래스 3개 기억해두기 : Carlendar, LocalDate, LocalDateTime
	
}
