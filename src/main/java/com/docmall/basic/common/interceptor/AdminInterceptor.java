package com.docmall.basic.common.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.docmall.basic.admin.AdminVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component 
public class AdminInterceptor implements HandlerInterceptor {
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		log.info("preHandle메서드 실행");
		
		boolean result = false;
		
		HttpSession session = request.getSession();
		AdminVO vo = (AdminVO)session.getAttribute("admin_state");
		
		if(vo == null) { //만약 로그인을 하지 않았으면, 요청이 인증되지 않은 상태를 의미 
			
			result = false;
			
				if(isAjaxRequest(request)) {  // ajax요청이라는 의미.
					response.sendError(400); //에러발생정보가 클라이언트에게 넘어감  //유효하지 않은 요청을 했다(로그인 안하고 로그인서비스를 이용하려 했다 등)
				}else {
					//원래 요청한 주소를 세션으로 저장하는 기능. 
					getTargetUrl(request); // 아래에서 수동으로 만든 페이지 저장 메서드 : 로그인 전에 있었던 페이지를 저장해놓는다.
					response.sendRedirect("/admin/");  // 로그인 페이지 주소
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
