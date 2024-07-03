package com.docmall.basic.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

//시큐리티 라이브러리를 사용하기 위한 설정작업을 위한 클래스
@Configuration  // 설정목적으로 사용하는 클래스에는 이 어노테이션을 사용한다.
//@EnableWebSecurity  //시큐리티 기능 주석
public class SecurityConfig {
/* 시큐리티 기능 주석
	// [스프링 시큐리티 설정] // v2.7과 v3.x 버전 차이가 있음.
	@Bean
	SecurityFilterChain securityFileterChain(HttpSecurity http) throws Exception{
		http
				.csrf((csrf) -> csrf.disable());
//		.cors((c) -> c.disable())
//		.headers((headers) -> headers.disable());
		return http.build();	
	}
*/	
	
	// [암호화기능 bean 생성]
	@Bean  //자바의 다형서 때문에 PasswordEncoder.java부모인터페이스를 사용했다. BCryptPasswordEncoder의 부모가 PasswordEncoderek.
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
