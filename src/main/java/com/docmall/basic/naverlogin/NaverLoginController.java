package com.docmall.basic.naverlogin;

import java.io.UnsupportedEncodingException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.docmall.basic.user.SNSUserDto;
import com.docmall.basic.user.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/oauth2")
public class NaverLoginController {

	private final NaverLoginService naverLoginService;
	
	//카카오 로그인 사용을 위해 추가.
	private final UserService userService;
	
	//step1.
	//[네이버 로그인 폼]
	@GetMapping("/naverlogin")
	public String connect() throws UnsupportedEncodingException {
		
		String url = naverLoginService.getNaverAuthorizeUrl();
		
		return "redirect:" + url;
	}
	
	
	//step2.
	// [네이버 Callback주소 생성작업] http://localhost:9090/oauth2/callback/naver
	//네이버 로그인 api에서 우리를 호출하기 위해 만들어둔 주소.
	//API 요청 성공시 : http://콜백URL/redirect?code={code값}&state={state값}
	//API 요청 실패시 : http://콜백URL/redirect?state={state값}&error={에러코드값}&error_description={에러메시지}
	@GetMapping("/callback/naver")
	public String callBack(NaverCallback callback, HttpSession session) throws UnsupportedEncodingException, Exception {
		
		if(callback.getError() != null) {
			log.info(callback.getError_description()); // 에러나면 로그를 출력해 확인해보겠다.			
		}
		
		// Json응답 데이터
		String responseToken = naverLoginService.getNaverTokenUrl(callback);
		
		ObjectMapper objectMapper = new ObjectMapper();
		NaverToken naverToken = objectMapper.readValue(responseToken, NaverToken.class);
		
		log.info("토킅정보 : "+ naverToken.toString());
		
		//액세스 토큰을 이용한 사용자 정보 받아오기.
		//responseUser 안에 접근토큰을 이용하여 프로필 api호출하기 출력결과의 변수들이 들어간다.
		String responseUser = naverLoginService.getNaverUserByToken(naverToken);
		NaverResponse naverResponse = objectMapper.readValue(responseUser, NaverResponse.class);
		
		log.info("사용자정보:" + naverResponse.toString());
		
		String sns_email = naverResponse.getResponse().getEmail();
		
		if(naverResponse != null) {
			session.setAttribute("naver_status", naverResponse);
			session.setAttribute("accessToken", naverToken.getAccess_token());
			
			if(userService.exsistsUserInfo(sns_email) == null && userService.sns_user_check(sns_email) == null) {
				
				SNSUserDto dto = new SNSUserDto();
				dto.setId(naverResponse.getResponse().getId());
				dto.setEmail(naverResponse.getResponse().getEmail());
				dto.setName(naverResponse.getResponse().getName());
				dto.setSns_type("naver");
				
				userService.sns_user_insert(dto);
			}
			
			
			
		}
		
		return "redirect:/";
		
	}
	
	
	
	// [네이버 로그아웃]
	@GetMapping("/naverlogout")
	public String naverlogout(HttpSession session) {
		
		
		// 위의 session.setAttribute("kakao_status", kakaoUserInfo);를 제거해줘야 함.
		//String accessToken = (String)session.getAttribute("kakao_status");
		String accessToken = (String)session.getAttribute("accessToken");  //kakao_status로 잘못 넣어서 에러 났었음. 토큰 사용 중이니까 accessToken을 넣어줘야 함!
		
		naverLoginService.getNaverTokenDelete(accessToken);
		
		//토큰이 낫널이거나 공백이 아닐경우
		if(accessToken != null && !"".equals(accessToken)) {
			/*
			try {
				naverLoginService.naverlogout(accessToken);
			}catch(JsonProcessingException ex) {
				throw new RuntimeException(ex);
			}
			*/
			//세션을 사용하지 않을 때는 제거해줘야 한다.
			session.removeAttribute("naver_status");
			session.removeAttribute("accessToken");
		}
		return "redirect:/";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
