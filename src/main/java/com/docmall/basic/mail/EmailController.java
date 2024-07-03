package com.docmall.basic.mail;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/email/*")
public class EmailController {

	private final EmailService emailService;
	
	//스프링이 아래 작업을 자동으로 처리해준다.
	//EmailDTO dto = new EmailDTO dto();
	//dto.setReceiverMail("입력한 메일주소");
	
	//[1. 인증코드 발송]
	@GetMapping("/authcode")
	public ResponseEntity<String> authcode(String type, EmailDTO dto, HttpSession session){

		log.info("dto : " + dto);
		
		ResponseEntity<String> entity = null;
		
		//인증코드 생성
		String authcode="";
		for(int i=0; i<6; i++) {
			authcode += String.valueOf((int)(Math.random() * 10));
		}
		
		log.info("인증코드" + authcode);

		// 사용자가 자신의 메일에서 발급받은 인증코드를 읽고, 회원가입시 인증 확인란에 입력을 하게되면, 서버에서 비교목적으로 세션방식으로 인증코드를 메모리에 저장해두어야 한다.
		session.setAttribute("authcode", authcode); //톰캣서버가 내장되어 있는데 톰캣은 세션이 30분. 인증번호를 30분 이후에 기입하고 확인하게 되면, 세션만료로 인한 데이터 소멸로 인증이 실패함.
		
		//사용자에게 메일발송(예외발생할 수 있으니까 try catch)
		try {
			
			//메일발송
			//메일제목작업
			if(type.equals("emailJoin")) {
				dto.setSubject("DocMall 회원가입 인증코드 입니다.");
			}else if(type.equals("emailID")) {
				dto.setSubject("DocMall 아이디 찾기 인증코드 입니다.");
			}else if(type.equals("emailPw")) {
				dto.setSubject("DocMall 비밀번호 찾기 인증코드 입니다.");
			}
			
			
			emailService.sendMail(type, dto, authcode);
			entity = new ResponseEntity<String>("success", HttpStatus.OK); //200코드
		}catch(Exception e) {
			e.printStackTrace();
			entity = new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR); // 500코드
		}
		return entity;
	}
	
	
	// [2. 인증코드 확인]
	@GetMapping("/email/confirm_authcode")
	public ResponseEntity<String> confirm_authcode(String authcode, HttpSession session) {

		ResponseEntity<String> entity = null; 
		
			//session.setAttribute("authcode", authcode);에 세션을 저장해놨기 때문에 아래에서도 authcode를 사용해야 한다. 
			//세션이 유지되고 있는 동안
			if(session.getAttribute("authcode") != null) {	
				
				if(authcode.equals(session.getAttribute("authcode"))) {
					entity = new ResponseEntity<String> ("success", HttpStatus.OK);
					session.removeAttribute("authcode"); // 성공했으니까 서버측의 메모리를 제거해준다.
					//removeAttribute("");는 session.setAttribute의 반대개념이다.
				}else {
					entity = new ResponseEntity<String> ("fail", HttpStatus.OK);
				}
			}else { //세션이 소멸되었을 경우
				entity = new ResponseEntity<String> ("request", HttpStatus.OK);
			}
		
			return entity;
		}


}
