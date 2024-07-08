package com.docmall.basic.review;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//pro_detail.html 에서 상품 후기 처리.

@RestController   //jsp, 타임리프 페이지가 필요없음.  pro_detail.html파일 내에서 모두 처리가 될 예정이므로.
@Slf4j
@RequestMapping("/review/*")
@RequiredArgsConstructor 
public class ReviewController {

	private final ReviewService reviewService;
	
}
