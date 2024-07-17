package com.docmall.basic.payinfo;

import java.util.Date;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class PayInfoVo {
/*
 --payinfo
--p_id, ord_code, paymethod, p_price, p_status, p_date
--pk_payinfo_idx
 */
	
	private Integer p_id;
	private Long ord_code;
	private String mbsp_id;
	private String paymethod;
	private String payinfo;
	private int p_price;
	private String p_status;
	private Date p_date;
	
	
}
