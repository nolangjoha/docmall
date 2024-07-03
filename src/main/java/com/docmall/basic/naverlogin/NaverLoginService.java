package com.docmall.basic.naverlogin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NaverLoginService {

	@Value("${naver.client.id}")
	private String clientId;
	
	@Value("${naver.redirect.uri}")
	private String redirectUri;
	
	@Value("${naver.client.secret}")
	private String clientSecret;
	
	
	//1. 요청문 샘플을 리턴하기 위한 메서드(https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=CLIENT_ID&state=STATE_STRING&redirect_uri=CALLBACK_URL)
	// 요청문을 만들어서 Controller 에 있는 connect메서드로 넘겨줄 예정.
	// 카카오에서는 StringBuffer로 만든것과 동일한 작업 이다.
	// 헤더가 있는 경우에는 StringBuffer로 만들 수 없다. 헤더가 있으면 클래스를 이용해 작업을 들어가야한다. 헤더가 없고 단순 요청 주소가 있다면 문자열로 만들어 버릴 수 있다.
	public String getNaverAuthorizeUrl() throws UnsupportedEncodingException {
		
		//UriComponents : 게시판 페이징 할때 사용했던 클래스 // 쿼리 스트링 만들때도 사용 됨.
		UriComponents  uriComponents = UriComponentsBuilder
				.fromUriString("https://nid.naver.com/oauth2.0/authorize")
				.queryParam("response_type", "code")
				.queryParam("client_id", clientId)
				.queryParam("state", URLEncoder.encode("1234", "UTF-8"))  //1234는 아무값이나 넣은 것.
				.queryParam("redirect_uri", URLEncoder.encode(redirectUri, "UTF-8"))  //패키지가 같은게 있으면 1순위로 골라야할것은 SpringFameWork로 시작하는것 2순위는 Java로 시작하는 것.
				.build();
		
		return uriComponents.toString(); //리턴값으로 요청문이 출력된다.
	}
	
	
	//2. callback주소 만들기 메서드 (https://nid.naver.com/oauth2.0/token?grant_type=authorization_code&client_id=jyvqXeaVOVmV&client_secret=527300A0_COq1_XV33cf&code=EIc5bFrl4RibFls1&state=9kgsGTfH4j7IyAkg)
	public String getNaverTokenUrl(NaverCallback callback)  {
		
		// access token을 받기위한 요청보내기 작업을 여기서 직접 하겠다. 작업후 결과값이 JSON으로 응답
		// 이 작업은 RestTemplate클래스 혹은 HttpURLConnection으로 할 수 있다. 카카오 구현할땐 전자로 했음. 네이버도 그걸로 해도 되지만 연습을 위해 두번째 방법으로 해보겠다.
		
		//URL url = new URL(uriComponents.toString());
		try {
			//UriComponents : 게시판 페이징 할때 사용했던 클래스 // 쿼리 스트링 만들때도 사용 됨.
			UriComponents  uriComponents = UriComponentsBuilder
					.fromUriString("https://nid.naver.com/oauth2.0/token")
					.queryParam("grant_type", "authorization_code")
					.queryParam("client_id", clientId)
					.queryParam("client_secret", clientSecret)  
					.queryParam("code", callback.getCode())
					.queryParam("state", URLEncoder.encode(callback.getState(), "UTF-8"))
					.build();
			
			URL url = new URL(uriComponents.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();  // HttpURLConnection 사용.
			conn.setRequestMethod("GET");
			
			int responseCode= conn.getResponseCode();
			BufferedReader br;
			
			//아래는 입력스트림 작업: 내가 요청했을때 보내오는 정보를 읽는다.   / 반대로 출력은 내가 주소에 어떤 정보를 보낼때 사용한다.
			// conn.getInputStream() : 바이트 스트림.
			//InputStreamReader: 바이트 기반의 스트림을 문자기반스트림으로 변환하는 기능
			//스트림작업은 아래와 같이 케이크처럼 작업을 쌓아간다.
			//BufferReader : 버퍼기능 보조스트림.
			if(responseCode == 200) {
				//br : 입력스트림 객체가 된것.
				br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			}else {
				br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			}
			
			String inputLine;
			StringBuffer response = new StringBuffer();
			while((inputLine = br.readLine()) != null) {
				response.append(inputLine); 
			}
			
			br.close();
			
			log.info("응답데이터11:" + response.toString());
			
			return response.toString(); //response안에 응답 정보가 들어있다.
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	
		return null;
		
	}
	
	
	//접근토큰을 이용하여 프로필 api호출하기(헤더작업 필요, restremplate(springframework에서 제공)과 HttpURLConnection(java에서 제공)로 할수 있음.)
	//https://openapi.naver.com/v1/nid/me
	/*
	접근 토큰(access token)을 전달하는 헤더
	다음과 같은 형식으로 헤더 값에 접근 토큰(access token)을 포함합니다. 토큰 타입은 "Bearer"로 값이 고정되어 있습니다.
	Authorization: {토큰 타입] {접근 토큰] 
	 */
	//[사용자 정보 받아오는 토큰]
	public String getNaverUserByToken(NaverToken naverToken)  {
		
		String accessToken = naverToken.getAccess_token();
		String tokenType = naverToken.getToken_type();
		
		try {
			URL url = new URL("https://openapi.naver.com/v1/nid/me");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Authorization", tokenType + " " + accessToken);
			
			
			int responseCode= conn.getResponseCode();
			BufferedReader br;
			
			//아래는 입력스트림 작업: 내가 요청했을때 보내오는 정보를 읽는다.   / 반대로 출력은 내가 주소에 어떤 정보를 보낼때 사용한다.
			// conn.getInputStream() : 바이트 스트림.
			//InputStreamReader: 바이트 기반의 스트림을 문자기반스트림으로 변환하는 기능
			//스트림작업은 아래와 같이 케이크처럼 작업을 쌓아간다.
			//BufferReader : 버퍼기능 보조스트림.
			if(responseCode == 200) {
				//br : 입력스트림 객체가 된것.
				br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			}else {
				br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			}
			
			String inputLine;
			StringBuffer response = new StringBuffer();
			while((inputLine = br.readLine())!= null) {
				response.append(inputLine); 
			}
			
			br.close();
			
			log.info("사용자정보 응답데이터:" + response.toString());
			
			return response.toString(); //response안에 응답 정보가 들어있다.
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return null;
	}	
	
	
	
	
	// getNaverTokenUrl 뼈대 복사해옴
	// [네이버 토큰 제거 해서 로그아웃]
	public void getNaverTokenDelete(String access_token)  {
		
		// access token을 받기위한 요청보내기 작업을 여기서 직접 하겠다. 작업후 결과값이 JSON으로 응답
		// 이 작업은 RestTemplate클래스 혹은 HttpURLConnection으로 할 수 있다. 카카오 구현할땐 전자로 했음. 네이버도 그걸로 해도 되지만 연습을 위해 두번째 방법으로 해보겠다.
		//URL url = new URL(uriComponents.toString());
		try {
			//UriComponents : 게시판 페이징 할때 사용했던 클래스 // 쿼리 스트링 만들때도 사용 됨.
			UriComponents  uriComponents = UriComponentsBuilder
					.fromUriString("https://nid.naver.com/oauth2.0/token")
					.queryParam("grant_type", "delete")  //매뉴얼 보면 토큰 삭제시 delete값을 넣으라 함.
					.queryParam("client_id", clientId)
					.queryParam("client_secret", clientSecret)  
					//.queryParam("code", callback.getCode())		//발급할때만 필요함
					//.queryParam("state", URLEncoder.encode(callback.getState(), "UTF-8"))		//발급할때만 필요함
					.queryParam("access_token", URLEncoder.encode(access_token,"UTF-8"))  	//기발급받은 토큰,  매개변수로 받아온다.    
					.build();
			
			URL url = new URL(uriComponents.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();  // HttpURLConnection 사용.
			conn.setRequestMethod("GET");
			
			int responseCode= conn.getResponseCode();
//			BufferedReader br;
//			
//			//아래는 입력스트림 작업: 내가 요청했을때 보내오는 정보를 읽는다.   / 반대로 출력은 내가 주소에 어떤 정보를 보낼때 사용한다.
//			// conn.getInputStream() : 바이트 스트림.
//			//InputStreamReader: 바이트 기반의 스트림을 문자기반스트림으로 변환하는 기능
//			//스트림작업은 아래와 같이 케이크처럼 작업을 쌓아간다.
//			//BufferReader : 버퍼기능 보조스트림.
//			if(responseCode == 200) {
//				//br : 입력스트림 객체가 된것.
//				br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//			}else {
//				br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
//			}
			
//			String inputLine;
//			StringBuffer response = new StringBuffer();
//			while((inputLine = br.readLine()) != null) {
//				response.append(inputLine); 
//			}
//			
//			br.close();
//			
//			log.info("응답데이터11:" + response.toString());
//			
//			return response.toString(); //response안에 응답 정보가 들어있다.
		} catch (Exception e) {
			                   
			e.printStackTrace();
		}

		
	}
	
	
	
}
