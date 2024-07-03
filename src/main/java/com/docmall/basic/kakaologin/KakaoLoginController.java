package com.docmall.basic.kakaologin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.docmall.basic.user.SNSUserDto;
import com.docmall.basic.user.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/oauth2")
@RequiredArgsConstructor
public class KakaoLoginController {

	private final KakaoLoginService kakaoLoginService;
	
	//카카오 로그인 사용을 위해 추가.
	private final UserService userService;
	
	//아무이름이나 해도 되지만, 연관성 있는 이름으로 
	@Value("${kakao.client.id}") // 이 어노테이션으로 환경설정에 작업해놓은 우리가 임의로 만든 카카오 설정값을 가져 올 수 있다!
	private String clientId;

	@Value("${kakao.redirect.uri}")
	private String redirectUri;
	
	@Value("${kakao.client.secret}")
	private String clientSecret;
	
	
	// step1.카카오 api server에게 인가코드를 요청하는 작업. 
	// 헤더는 없고, 요청 파라미터가 있는 경우 //헤더가 있으면 아래와 같이 작업 불가.
	//카카오로그인 주소작업
	@GetMapping("/kakaologin")
	public String kakaoConnect() {
		
		//StringBuffer : string buffer는 기존에 생성된 메모리를 유지하면서 데이터를 계속 추가할 수 있다. String클래스보다 성능이 좋다.// String 단순한것들은 이걸 쓴다. 추가할때마다 힙모리가 계속 생성된다.
		// StringBuffer는 문자열 합치는 방법이 append를 사용하고, String은 +를사용한다.
		StringBuffer url = new StringBuffer();
		url.append("https://kauth.kakao.com/oauth/authorize?");
		url.append("response_type=code");
		url.append("&client_id=" + clientId);
		url.append("&redirect_uri=" + redirectUri);  
		
		//추가옵션
		url.append("&prompt=login"); //카카오 로그인 클릭시 카카오 로그인 화면이 다시 나오게 함.
		
		log.info("인가코드:" + url.toString());
		
		//redirect는 뒤의 주소를 요청하는 기능
		return "redirect:" + url.toString();  //요 주소가 요청되서 웹 화면에 출력됨.
	}
	
	
	// step2. 카카오 로그인 api server에서 개발자사인트Callback 주소호출. 카카오 개발자 사이트에서 애킆리케이션 등록과정에서 아래 주소 설정을 이미 한 상태.   //redirect uri 호출 // 
	// redirect uri 를 이전에 설정해놨는데 카카오가 우리가 설정한 redirect uri 이 맞는지 다시 한번 확인하는 작업.  // 환경설정에 kakao.redirect.uri에 있는 주소 // 동의하고 계속하기 누르면 실행 된다.
	@GetMapping("/callback/kakao")	//String code는 카카오에서 정한 코드, session은 나중에 해야하니까 미리 추가
	public String callback(String code, HttpSession session) {
	
		log.info("code(코드):"+ code);
		
		String accessToken = "";
		KakaoUserInfo kakaoUserInfo = null;
		
		try {
			// 위의 매개변수인 code를 이용해서 인증 코튼을 요청하는 작업 진행
			// 카카오 로그인 api 서버에게 로그인 인증을 성공. 인증토큰을 이용하여 카카오 사용자에대한 정보를 제공.
			accessToken = kakaoLoginService.getAccessToken(code);  // 인가코드를 통한 인증토큰을 요청.
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		
		try {
			kakaoUserInfo = kakaoLoginService.getKakaoUserInfo(accessToken);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		
		if(kakaoUserInfo != null) {
			log.info("사용자 정보" + kakaoUserInfo);
			
			//db작업
			/*
			 	- 카카오 로그인 사용자가 회원테이블 존재유무 확인.
			 		1. 존재 : 회원수정작업을 회원테이블에서 가져오면 된다.
			 		2. 존재X: 회원수정작업은 회원테이블을 참조하지 않고, 해당클래스를 에러없도록 작업한다.
			 	- 혹은 카카오 회원테이블을 만들어 해당 테이블의 데이터 존재유무 확인. 
			 */
			
			//인증을 세션방식으로 처리.
			session.setAttribute("kakao_status", kakaoUserInfo); //인증 여부 구분목적
			session.setAttribute("accessToken", accessToken);  //카카오 로그아웃에 사용
			
			String sns_email = kakaoUserInfo.getEmail();
			
			String sns_login_type = userService.exsistsUserInfo(sns_email);
//			session.setAttribute("sns_type", sns_login_type);
			
			
			// 둘다 데이터가 존재하지 않으면 : 회원테이블에도 존재안하고, 카카오테이블에도 존재하지 않으면)
			//&& : 같은 데이터 테스트시 첫번째는 좌측은 true가 되고, 우측도 true가 되어, 전체조건 ture가 되어 데이터 삽입
			//	   두번째는 좌측은 true가 되고, 우측은 false가 되어, 전체 조건이 false가 되어  데이터 삽입이 진행되지 않는다.
			//if(!(userService.exsistsUserInfo(sns_email) != null) && !(kakaoLoginService.existsKakaoInfo(sns_email) != null)) 
			if(userService.exsistsUserInfo(sns_email) == null && userService.sns_user_check(sns_email) == null) {			
				//sns 테이블에서 데이터 삽입 작업. //if조건이 && 연산자이므로 2가지 조건이 모두 맞아야 아래 행위가 실행된다.
				SNSUserDto dto = new SNSUserDto();
				dto.setId(kakaoUserInfo.getId().toString());
				dto.setEmail(kakaoUserInfo.getEmail());
				dto.setName(kakaoUserInfo.getNickname());
				dto.setSns_type("kakao");
				
				userService.sns_user_insert(dto);
			
			}
			
		}
		return "redirect:/";
	}
	
	
	
	// [카카오 로그아웃]
	@GetMapping("/kakaologout")
	public String kakaologout(HttpSession session) {
		
		// 위의 session.setAttribute("kakao_status", kakaoUserInfo);를 제거해줘야 함.
		//String accessToken = (String)session.getAttribute("kakao_status");
		String accessToken = (String)session.getAttribute("accessToken");  //kakao_status로 잘못 넣어서 에러 났었음. 토큰 사용 중이니까 accessToken을 넣어줘야 함!
		
		//토큰이 낫널이거나 공백이 아닐경우
		if(accessToken != null && !"".equals(accessToken)) {
			
			try {
				kakaoLoginService.kakaologout(accessToken);
			}catch(JsonProcessingException ex) {
				throw new RuntimeException(ex);
			}
			//세션을 사용하지 않을 때는 제거해줘야 한다.
			session.removeAttribute("kakao_status");
			session.removeAttribute("accessToken");
		}
		return "redirect:/";
	}
	
	
	
	
	
}
