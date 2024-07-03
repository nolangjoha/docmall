package com.docmall.basic;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.docmall.basic.admin.category.AdminCategoryService;
import com.docmall.basic.admin.category.AdminCategoryVo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

	private final AdminCategoryService adminCategoryService;
	
//	@ResponseBody 	// 이 어노테이션을 사용하지 않으면 return의 "index"는 데이터가 아닌 타임리프파일명이 된다.
	@GetMapping("/")
	public String index(Model model) {
		log.info("기본주소.");
	
		//카테고리 가져오기
		List<AdminCategoryVo> cate_list = adminCategoryService.getFirstCategoryList();
		model.addAttribute("user_cate_list", cate_list);  //관리자쪽에 영향 받지 않게 이름을 다르게 함.
		
		return "index";  
	}
	
}
