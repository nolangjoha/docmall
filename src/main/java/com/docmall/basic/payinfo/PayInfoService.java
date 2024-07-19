package com.docmall.basic.payinfo;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PayInfoService {

	private final PayInfoMapper payInfoMapper;
	
	
	// [결제정보 가져오기]
	public PayInfoVo ord_pay_info(Long ord_code) {
		return payInfoMapper.ord_pay_info(ord_code);
	}
	
}
