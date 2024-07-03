package com.docmall.basic.admin.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class ProductDTO {

	//체크박스에서 받아내야할 필드들.
	private Integer pro_num;
	private int pro_price;
	private String pro_buy;
	
	
}
