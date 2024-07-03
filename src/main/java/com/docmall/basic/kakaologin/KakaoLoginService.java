package com.docmall.basic.kakaologin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Service
@RequestMapping("/oauht2")
public class KakaoLoginService {

	private final KakaoMapper kakaoMapper;
	
	
	//아무이름이나 해도 되지만, 연관성 있는 이름으로 
	@Value("${kakao.client.id}") // 이 어노테이션으로 환경설정에 작업해놓은 우리가 임의로 만든 카카오 설정값을 가져 올 수 있다!
	private String clientId;

	@Value("${kakao.redirect.uri}")
	private String redirectUri;
	
	@Value("${kakao.client.secret}")
	private String clientSecret;
	
	@Value("${kakao.oauth.tokenuri}")
	private String tokenUri;
	
	@Value("${kakao.oauth.userinfouri}")
	private String userinfoUri;
	
	//그냥 로그아웃
	@Value("${kakao.user.logout}")
	private String kakaologout;
	
	
	// [엑세스 토큰을 받기위한 정보]
	// 주소 호출 작업(https://kauth.kakao.com/oauth/token)
	// 요청방식 : post
	// 헤더(header) : Content-type: application/x-www-form-urlencoded;charset=utf-8
	/* 본문(Body) : 
		grant_type: authorization_code
		client_id: 앱 REST API 키
		redirect_uri : 인가 코드가 리다이렉트된 URI
		code : 인가 코드 받기 요청으로 얻은 인가 코드
		client_secret  토큰 발급 시, 보안을 강화하기 위해 추가 확인하는 코드
	*/
	public String getAccessToken(String code) throws JsonProcessingException{
		
		// 1)Http Header 생성
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		
		// 2)Http body생성
		 MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		 body.add("grant_type", "authorization_code");	
		 body.add("client_id", clientId);	
		 body.add("redirect_uri", redirectUri);	
		 body.add("code", code);	
		 body.add("client_secret", clientSecret);	
		 
		 // 3) Http Header + Http body 정보를 Entity로 구성.
		 HttpEntity<MultiValueMap<String, String>> tokenKakaoRequest = new HttpEntity<MultiValueMap<String,String>>(body, headers);

		 //4) Http 요청보내기. (API Server와 통신을 담당하는 기능을 제공하는 클래스)/	(RestTemplate작업) //https://adjh54.tistory.com/234
		 RestTemplate restTemplate = new RestTemplate();
		 
		 		//exchange를 가지고 4개의 정보를 통신한다.
		 ResponseEntity<String> response = restTemplate.exchange(tokenUri, HttpMethod.POST, tokenKakaoRequest, String.class);
				 
		 //5)  Http 응답(JSon) -> Access Token 추출(Parsing)작업
		 String responseBody = response.getBody();
		 log.info("응답데이터 "+ responseBody);
		 
		 ObjectMapper objectMapper = new ObjectMapper();
		 JsonNode jsonNode = objectMapper.readTree(responseBody); 
		 
		return jsonNode.get("access_token").asText(); //인증 토큰 받아옴(제일 중요) //이걸로 이미 로그인은 된것이나 다름없지만  우린 이걸 활용해서 세션으로 저장해서 로그인 작업을 해줘야 한다.
	}
	
	// access토큰을 이용한 사용자 정보 받아오기
	public KakaoUserInfo getKakaoUserInfo(String accessToken) throws JsonProcessingException {
	
		//1) Header생성
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);
		headers.add("Content-type", "Content-type: application/x-www-form-urlencoded;charset=utf-8");
		
		//2) body는 생성안함. api 매뉴얼에서 필수가 아님.
		
		//3) header + body 정보를 Entity로 구성.      // body가 없어서 new HttpEntity<>(headers)로 작성
		HttpEntity<MultiValueMap<String, String>> userInfoKakaoRequest = new HttpEntity<>(headers);
		
		//4) http 요청
		RestTemplate restTemplate = new RestTemplate();
		
		//5) http 응답
		ResponseEntity<String> responseEntity = restTemplate.exchange(userinfoUri, HttpMethod.POST, userInfoKakaoRequest, String.class);
		
		String responseBody = responseEntity.getBody();
		log.info("응답 사용자 정보: " + responseBody);
		
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(responseBody);
		
		
		//인증토큰을 이용한 카카오 사용자에 대한 정보를 받아옴. json구조로 들어온다(w3school에서 js Json개념 볼 수 있음.)
		Long id = jsonNode.get("id").asLong();	
		String email = jsonNode.get("kakao_account").get("email").asText();
		String nickname = jsonNode.get("properties").get("nickname").asText();
		
		return new KakaoUserInfo(id, nickname, email);
		
	}
	
	
	// [카카오 로그아웃] https://kapi.kakao.com/v1/user/logout    헤더는 있고, 파라미터는 없는 경우.
	// 헤더 Authorization: Bearer ${ACCESS_TOKEN}	
	public void kakaologout(String accessToken) throws JsonProcessingException{
		
		// Http Header 생성
		HttpHeaders headers = new HttpHeaders();
		//"Bearer "  : 띄어쓰기 주의.
		headers.add("Authorization", "Bearer " + accessToken);
		headers.add("Content-type", "application/x-www-form-urlencoded");  //상단의 62번줄 복사해옴.
		
		//Http 요청 작업
		HttpEntity<MultiValueMap<String, String>> kakaoLogoutReqeust = new HttpEntity<>(headers);
		
		//Http 요청하기
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.exchange(kakaologout, HttpMethod.POST, kakaoLogoutReqeust, String.class);
		
		//리턴된 정보 : json포맷의 문자열   // 이렇게 넘어오는 정보들은 log를 찍어봐야 한다.
		String responseBody = response.getBody();
		log.info("responseBody: " + responseBody);
		
		//Json문자열을 Java객체로 역직렬화 하거나 Java객체를 JSon으로 직렬화 할때 사용하는 Jackson라이브러리
		//ObjectMapper생성 비용이 비싸기 때문에 bena/static으로 처리하는 것이 성능에 좋
		ObjectMapper objectMapper = new ObjectMapper(); 
		JsonNode jsonNode = objectMapper.readTree(responseBody);
		
		Long id = jsonNode.get("id").asLong();
		
		log.info("id:"+id);
		 
	}
	

	
}
