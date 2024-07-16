package com.docmall.basic.payinfo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Controller
@Slf4j
@RequestMapping("/payinfo/*")
public class PayInfoController {

	private final PayInfoService payInfoService;
	
	
	
}
