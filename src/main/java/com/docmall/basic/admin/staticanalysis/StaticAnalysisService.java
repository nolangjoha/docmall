package com.docmall.basic.admin.staticanalysis;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class StaticAnalysisService {

	private final StaticAnalysisMapper staticAnalysisMapper;
	
	
	public List<Map<String,Object>> monthlySalesStatusByTopCategory(String ord_date) {
		return staticAnalysisMapper.monthlySalesStatusByTopCategory(ord_date);
	}
	
	
	
	
}
