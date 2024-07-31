package com.docmall.basic.common.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.docmall.basic.common.interceptor.LoginInterceptor;

import lombok.RequiredArgsConstructor;

// 용도? : LoginInterceptor.java 인터셉터 클래스를 위한 매핑주소 설정

@RequiredArgsConstructor   // 롬복에 있는 것으로 final표시가 있는걸 생성자로 만들어줌.
@Component  // loginInterceptor bean생성, bean을 관리하는 곳 : 스프링 콘테이터
public class WebMvcConfig implements WebMvcConfigurer{

	public final LoginInterceptor loginInterceptor;   //loginInterceptor 빈이 먼저 생성되고 WebMvcConfig이름의 빈이 생성되는 것이다.

	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		registry.addInterceptor(loginInterceptor)
				.addPathPatterns("/user/mypage", 
								 "/user/changepw", 
								 "/user/delete", 
								 "/user/ajax",
								 "/cart/cart_list",
								 "/cart/cart_add",
								 "/cart/cart_empty"
						); 
				//addPathPatterns("매핑주소설정", "매핑주소설정") //갯수 상관없음
		
		/*
		 특정주소가 인증된 경우에만 사용 할 때 : /userinfo/*  - userinfo의 바로 밑의 레벨의 모든 주소를 인터셉트
		 											 - 현 시점에서 이렇게 설정해버리면 회원가입조(/usrerinfo/join)차 못함.
		 						      : /userinfo/** - userinfo의 하위 폴더가 여러개일 경우 그 하위 레벨의 모든 주소들 인터셉트
		 */
	}
	
	
	
	
	
	
	
}
