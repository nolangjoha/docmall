package com.docmall.basic.common.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.docmall.basic.user.UserVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

//용도? 인터셉터 기능을 하기위한 클래스 : Controller로 가는 신호를 가로챔.
// 어떤 매핑주소를 인터셉터 하는가? : WebMvcConfig.java 파일에서 설정.

@Slf4j
@Component 
public class LoginInterceptor implements HandlerInterceptor{
	
	// 3개의 메서드가 이벤트 특징을 갖고있다.	
	//진행순서 : 매핑주소 요청발생 > 1)preHandle 메서드 > 2)컨트롤 url주소의 메서드가 진행됨 > 3) postHandle 메서드 > 4) afterCompletion 메서드
	
	
// (1)preHandle메서드: Controller로 요청이 들어가기 전에 가로채어 다음 메서드가 호출(실행)된다.
	//	  리턴값이 true가 되면, 컨트롤러로 진행이 이뤄진다.
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		log.info("preHandle메서드 실행");
		
		boolean result = false;
		
		HttpSession session = request.getSession();
		UserVO vo = (UserVO)session.getAttribute("login_status");
		
		if(vo == null) { //만약 로그인을 하지 않았으면, 요청이 인증되지 않은 상태를 의미 
			
			result = false;
			
				if(isAjaxRequest(request)) {  // ajax요청이라는 의미.
					response.sendError(400); //에러발생정보가 클라이언트에게 넘어감  //유효하지 않은 요청을 했다(로그인 안하고 로그인서비스를 이용하려 했다 등)
				}else {
					//원래 요청한 주소를 세션으로 저장하는 기능. 
					getTargetUrl(request); // 아래에서 수동으로 만든 페이지 저장 메서드 : 로그인 전에 있었던 페이지를 저장해놓는다.
					response.sendRedirect("/user/login");  // 로그인 페이지 주소
				}
			
			// 로그인 안하고 WebMvcConfig.java에서 addPathPatterns로 지정한마이페이지에 접속하려 하면 로그인 페이지로 자동 이동
		}else {
			result = true;
		}
		return result;  //true값이면 controller에서 진행예정이었던 매핑으로 갔다가 postHandel로 되돌아온다.
	} 

	
	// (1-1) [원래 요청한 주소를 가지고 있는 기능]
		private void getTargetUrl(HttpServletRequest request) {
			
			String uri = request.getRequestURI();  //  /userinfp/mypage
			String query = request.getQueryString();  //   ?뒤의 문자열.
			
			if (query ==  null || query.equals("null")) {
				query = "";
			}else {
				query = "?" + query;
			}
			
			String targetUrl = uri + query;
			
			if(request.getMethod().equals("GET")) {
				request.getSession().setAttribute("targetUrl", targetUrl);   //요청한 주소에 대한 정보를 세션으로 저장해놓겠다.
			}
		}
	
	// (1-2) 사용자의 요청이 ajax요청인지 구분 하는 기능
		private boolean isAjaxRequest(HttpServletRequest request) {
			boolean isAjax = false;
			
			String header = request.getHeader("AJAX");  // AJAX에는  header.jsp에서 진행한 ajax문법으로 인해 true값이 들어있다.
			
			if(header != null && header.equals("true")) {
				isAjax = true;
			} return isAjax;
		}
	
		
		
		
		

	
	
}
