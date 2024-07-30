package com.docmall.basic.admin.staticanalysis;

import java.util.List;
import java.util.Map;

public interface StaticAnalysisMapper {

	
	List<Map<String,Object>> findDailyOrderStats();
	
	List<Map<String,Object>> monthlySalesStatusByTopCategory(String ord_date);
	
	
}
