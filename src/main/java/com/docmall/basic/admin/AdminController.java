package com.docmall.basic.admin;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin/*")
public class AdminController {

	private final AdminService adminService;
	
	//암호화 관련 클래스 // com.docmall.basic.common.config.SecurityConfig.java 생성되어있는 빈에서 주입됨.
	private final PasswordEncoder passwordEncoder;
	
	
	
	//[관리자 로그인 페이지]
	@GetMapping("")
	public String loginForm() {
		
		return "/admin/adLogin";
	}

	// [관리자 로그인]
	@PostMapping("admin_ok")
	public String admin_ok(AdminVO vo, HttpSession session) throws Exception {
		
		log.info("관라지정보 :" + vo);
		
		//로그인 페이지에서 사용자가 입력한 아이디를 d_vo에 대입한다.
		AdminVO d_vo = adminService.loginOk(vo.getAdmin_id());

		String url = "";
		
		// 만약에 사용자가 입력한 값이 DB에 있다면 내부코드 실행
		if(d_vo != null) {
			
			// 만약 DB의 비밀번호와 사용자가 입력한 비밀번호가 일치한다면 아래 코드 실행
			if(passwordEncoder.matches(vo.getAdmin_pw(), d_vo.getAdmin_pw())) {
				
				log.info("비번일치 : ");
				
				d_vo.setAdmin_pw("");
				session.setAttribute("admin_state", d_vo);
				
				// url에 다음 문자열을 대입한다. //경로 입력할때 '/'주의 할것. "/admin/admin_menu"라고 입력했더니 제대로 출력되지 않았음.
				url = "admin/admin_menu";
			}
			
		}
		
		
		return "redirect:/" + url;
	}

	
	//[관리자 메뉴 페이지]
	@GetMapping("/admin_menu")
	public void admin_menu() {
		
	}
	
	
	// [관리자 페이지 로그아웃]
	@GetMapping("/admin_logout")
	public String admin_logout(HttpSession session) {
		
//		session.invalidate();  일괄 제거 기능

		session.removeAttribute("admon_state");  //관리자 세션 부분만 제거
		
		return "redirect:/admin/";
	}
	
	
	
}
