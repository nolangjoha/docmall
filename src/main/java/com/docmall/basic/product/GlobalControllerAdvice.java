package com.docmall.basic.product;

import java.util.List;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.docmall.basic.admin.category.AdminCategoryService;
import com.docmall.basic.admin.category.AdminCategoryVo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
//"com.docmall.basic" 기본패키지를 설정하면, 전체 영향을 받는다.
@ControllerAdvice(basePackages = {"com.docmall.basic.product"})  
//카테고리가 사용되는 컨트롤러의 패키지를 지정. 지정한 패키지에는 카테고리 작업을 하지 않아도 된다.
public class GlobalControllerAdvice {

	private final AdminCategoryService adminCategoryService;
	
	@ModelAttribute
	public void comm_test(Model model) {
		log.info("공통코드 실행");
		
		//카테고리 가져오기
		List<AdminCategoryVo> cate_list = adminCategoryService.getFirstCategoryList();
		model.addAttribute("user_cate_list", cate_list);  //관리자쪽에 영향 받지 않게 이름을 다르게 함.
		
	}
}
