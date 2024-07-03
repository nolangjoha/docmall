package com.docmall.basic.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.docmall.basic.kakaologin.KakaoUserInfo;
import com.docmall.basic.mail.EmailDTO;
import com.docmall.basic.mail.EmailService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/user/*")
@Slf4j
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	//이 코드는 service단계에서 작업해줄것이므로 serviceImpl에 작성해줄것이다.
	//아래 코드는 학업용으로 남겨놓음. 
	private final PasswordEncoder passwordEncoder;
	
	//이메일 주입작업
	private final EmailService emailService;
	
	
	// [회원가입 폼]
	@GetMapping("join")
	public void joinForm() {
		log.info("join폼");
	}
	
	// [회원가입 정보 전송]
	@PostMapping("join")
	public String joinOk(UserVO vo) throws Exception{
		log.info("회원정보" + vo);
		
		//비밀번호 암호화로 변경.
		vo.setMbsp_password(passwordEncoder.encode(vo.getMbsp_password()));
		
		userService.join(vo);
		return "redirect:/user/login";
	}
	
	// [아이디 중복체크]
	//ajax요청작업은 리턴타입이 ResponseEntity 사용해야한다. 그리고 @responseBody어노테이션을 사용할 필요가 없다.
	@GetMapping("/idCheck")
	public ResponseEntity<String> idCheck(String mbsp_id) throws Exception {
		
		log.info("아이디: " + mbsp_id);
		
		ResponseEntity<String> entity = null;
		
		String idUse = "";
		
		if(userService.idCheck(mbsp_id) != null) {
			idUse="no";  //사용불가 (null이 아니면 DB에 값이 있는 것으로 사용 불가함.)
		}else {
			idUse = "yes"; //사용 가능(null이어야 DB에 값이 없는 것이므로 사용 가능인것)
		}
		
		entity = new ResponseEntity<String>(idUse, HttpStatus.OK);
													//HttpStatus.OK로 200번이 출력된다.
		return entity;
	}
	
	
	// [로그인폼]
	@GetMapping("/login")
	public void loginForm() {
		log.info("login폼");
	}
	
	//[로그인 작업]
	@PostMapping("/login") // 1)LoginDTO dto 	2) String mbsp_id, String mbsp_password
	public String loginOk(LoginDTO dto, HttpSession session, RedirectAttributes rttr) throws Exception {   //LoginDTO를 만들어 아래와 같이 작성해도 된다. 

		UserVO vo = userService.login(dto.getMbsp_id());
		
		String msg = "";   // 로그인 폼 주소
		String url = "/";  // 메인주소		
		
		if(vo != null) {  // 아이디가 존재한다면 (비밀번호도 존재한다 --> 비밀번호 비교작업)
			//비밀번호 비교 - u_id : 사용자가 입력한 비밀번호, vo.getU_pwd() : DB에 암호화된 비번
			if(passwordEncoder.matches(dto.getMbsp_password(), vo.getMbsp_password())) {  // 사용자가 입력한 비번이 암호화된 형태에 해당하는 것이라면?
				vo.setMbsp_password(""); //  암호화된 비밀번호지만 어쨌든 비밀번호. 해당코드로 공백으로 만들어준다. // 안한다고 해서 문제 될건 없지만 비밀번호니까.
				session.setAttribute("login_status", vo);  //vo를 "lonin_status"란 이름으로 타임리프에서 사용 // 저 값의 null여부에 따라 로그인 작업을 진행하거나 막을 수 있다.
				}else {
					msg = "failPW";
					url = "/user/login";
				}
			}else { //아이디가 존재하지 않으면
				msg = "failID";
				url = "/user/login";
			}
		rttr.addFlashAttribute("msg", msg);  //타임리프 에서 msg변수를 사용목적
		
		//log.info("url확인 "+ url);
		//log.info("msg확인 : " + msg);
		
		return "redirect:" + url;   //메인으로 이동 : 정상적으로 진행되면 url값은 /을 갖게 된다. 
		
		}	
	
	// [로그아웃]
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();	// 세션형태로 관리되는 모든 메모리는 소멸.
		
		return "redirect:/";
	}

	
	// [아이디 찾기 화면]
	@GetMapping("/idfind")
	public void idfindForm() {
		
	}
	
	// [아이디 찾기]
	@PostMapping("/idfind")
	public String idfindOk(String mbsp_name, String mbsp_email, String authcode, HttpSession session, RedirectAttributes rttr) throws Exception {
		
		String url = "";
		String msg = "";
						
		// EmailController.java와 비교하며 값을 넣어야 한다.
		// 인증코드 확인
		if(authcode.equals(session.getAttribute("authcode"))) {

			// 이름과 메일주소 확인.
			String mbsp_id = userService.idfind(mbsp_name, mbsp_email);
			if(mbsp_id != null) {		

				//아이디를 내용으로 메일발송작업 (emailServiceImpl에서 기능을 가져와야한다. 주입작업을 해야한다★★★★★)
				String subject = "Docmall 아이디를 보내드립니다.";
				EmailDTO dto = new EmailDTO("DocMall", "DocMall", mbsp_email, subject, mbsp_id);
				
				emailService.sendMail("emailIDResult", dto, mbsp_id);   //emailIDResult(타임리프 파일명)

				session.removeAttribute("authcode");  //세션 만료시키기
				
				msg = "success";
				url = "/user/login";
				rttr.addFlashAttribute("msg", msg);
				
			}else {
				msg = "failID";
				url = "/user/idfind";
			}
			
		}else {
			msg = "failAuthCode";
			url = "/user/idfind";
			
		}	
		rttr.addFlashAttribute("msg", msg);
			
		return "redirect:" + url;
	}
	
	
		// [비밀번호 찾기]
		// [비밀번호 찾기 페이지]
		@GetMapping("/pwfind")
		public void pwfindForm() {
		}
		
		
		// [비밀번호 찾기 기능]
		@PostMapping("/pwfind")
		public String pwfindOk(String mbsp_id, String mbsp_name, String mbsp_email, String authcode, HttpSession session, RedirectAttributes rttr) throws Exception {
			
			String url = "";
			String msg = "";
			
			if(authcode.equals(session.getAttribute("authcode"))) {
				
				//사용자가 입력한 3개정보(아아디, 이름, 이메일)를 조건으로 사용하여, 이메일을 디비에서 가져온다.
				//여기가 email이여서 mapper의 pwfind의 select다음이 email컬럼이 온다.
				String d_email = userService.pwfind(mbsp_id, mbsp_name, mbsp_email);
				if(d_email != null) {
					
					//임시비밀번호생성(UUID이용)   --> 기능적으로 본다면 service쪽으로 뺄수도 있다. Controller쪽은 심플한 느낌으로 가는 것이 좋음.
					String tempPw = userService.getTempPw();
				
					//암호화 된 비밀번호
					String temp_enc_pw = passwordEncoder.encode(tempPw); // 부작위로 뽑은 비밀번호 인코딩
					
					////암호화 된 임시비밀번호를 해당아이디에 업데이트 한다. 
					userService.tempPwUpdate(mbsp_id, temp_enc_pw); //인코딩된 암호 db에 저장
					
					//메일발송
					EmailDTO dto = new EmailDTO("DocMall", "DocMall", d_email, "DocMall에서 입시 비밀번호를 보내드립니다.", tempPw); //변경안내 이메일에 들어갈 내용들
					
					emailService.sendMail("emailPwResult", dto, tempPw); // 비밀번호 변경 메일 발송
					
					session.removeAttribute("authcode");  //세션 제거
					msg = "success";
					url = "/user/pwfind";
					}else {
						url = "/user/pwfind";
						msg = "failInput";	
					}
							
					//비밀번호 임시코드를 생성하여, 암호화해서, 사용자아이디에 저장한다.
					//그리고, 비밀번호 임시코드를 메일로 발급한다.
					}else {
						url = "/user/pwfind";
						msg = "failAuth";	
					}
				
			rttr.addFlashAttribute("msg", msg);
			
			return "redirect:" + url;  // 띄어쓰기 주의!! "redirect: " + url   ""사이에 공백 있었더니 페이지를 찾지 못함.
			

		}
		
			
		// [마이페이지]
		//일반 로그인 또는 카카오포그인 인지를 체크하는 작업
		@GetMapping("/mypage")
		public void mypage(HttpSession session, Model model) throws Exception {
			
			if(session.getAttribute("login_status") != null) {
				String mbsp_id = ((UserVO) session.getAttribute("login_status")).getMbsp_id();
				UserVO vo = userService.login(mbsp_id);  //로그인 정보가 나의 정보이므로 이걸로 사용함.
				log.info("수정할 아이디 정보" + vo);
				model.addAttribute("user", vo);  //user라는 이름으로 타임리프 mypage에서 사용				
			}else if (session.getAttribute("kakao_status") != null) {
				
				KakaoUserInfo kakaoUserInfo = (KakaoUserInfo) session.getAttribute("kakao_status");
				
				//mypage에서 보여둘 정보를 선택적으로 작업.
				UserVO vo = new UserVO();
				vo.setMbsp_name(kakaoUserInfo.getNickname());
				vo.setMbsp_email(kakaoUserInfo.getEmail());
				
				model.addAttribute("user", vo);
				model.addAttribute("msg", "kakao_login");
			} 
		}
	
		
		// [마이페이지 수정하기]
		@PostMapping("/modify")   //RedirectAttributes rttr는 수정하기 메세지 출력해주려구   //HttpSession session : 인증정보가 필요하므로 작성, 인증정보가 필요한 작업에는 반드시 매개변수로 들어가야 한다.
		public String modifyOk(UserVO vo, HttpSession session, RedirectAttributes rttr) throws Exception {
			log.info("수정된 아이디 정보 " + vo);
			
			//인증된 사용자만 할 수 있는 작업에는 해당 코드가 모두 들어가야 함. 근데 코드가 길어지면..?
			// 이러한 스프링인터셉트 작업은 홈페이지 구성이 마무리 된 후 만든다.// 인터셉트보다 더 좋은 기능은 시큐리티다.
			//인터셉터 기능으로 나중에는 삭제할 코드.
			if(session.getAttribute("login_status") == null) return "redirect:/user/login";
			
			log.info("회원정보 수정" + vo);
			
			//get으로 가져온 아이디를 mbsp_id에 넣는다. 물론 이 아이디는 상단 매개변수로 인해 들어오긴 하지만 그래도 확실하게 로그인한 사용자 확인을 위해 해당 코드를 넣어준다.
			//인증된 사용자만 사용하게 하기 위해 해당 정보를 가져온다.
			String mbsp_id = ((UserVO) session.getAttribute("login_status")).getMbsp_id();
			
			userService.modify(vo);
			
			rttr.addFlashAttribute("msg","success"); //리턴에 있는 경로의 jsp에서 사용할 목적: 성공메세지 줄거임
			
			return "redirect:/user/mypage";
				
		}
		
		
		// [비밀번호 변경 페이지]
		@GetMapping("/changepw")
		public void changepwForm() {
			
		}
		
		
		// [비밀번호 변경하기]
		@PostMapping("/changepw")
		public String changepwOk(String cur_mbsp_pwd, String new_mbsp_pwd, HttpSession session, RedirectAttributes rttr) {
			log.info("현재비번:" + cur_mbsp_pwd);
			//세션작업 : 아이디 가져옴.
			String mbsp_id = ((UserVO) session.getAttribute("login_status")).getMbsp_id();
			
			UserVO vo = userService.login(mbsp_id);
			
			String msg = "";   
			
			if(vo != null) {  // 아이디가 존재한다면 (비밀번호도 존재한다 --> 비밀번호 비교작업)
				//비밀번호 비교 - cur_mbsp_pwd : 사용자가 입력한 비밀번호, vo.getU_pwd() : DB에 암호화된 비번
				if(passwordEncoder.matches(cur_mbsp_pwd, vo.getMbsp_password())) {  // 사용자가 입력한 비번이 암호화된 형태에 해당하는 것이라면?
							
						// [신규비밀번호 변경작업]
						String enc_new_mbsp_password = passwordEncoder.encode(new_mbsp_pwd); // 암호화 작업
						userService.changePw(mbsp_id, enc_new_mbsp_password); // 암호화된 비번이 들어감.
						msg= "success";
						
					}else { //사용자가 입력한 비번이 암호화된 형태에 해당하지 않는 것이라면
						msg = "failPW";
					}
			}	
			rttr.addFlashAttribute("msg", msg);  //jsp에서 msg변수를 사용목적
			
			return "redirect:/user/changepw";
			

		}
		
		
		// [회원탈퇴 페이지]
		@GetMapping("/delete")
		public void deleteForm() {
			
		}
		
		
		@PostMapping("/delete")   //String mbsp_password은 html에서 name값으로 가져온 것, // 로그인된 사용자만 이 기능을 사용할 수 있으니 session작업.
		public String deleteOk (String mbsp_password, HttpSession session, RedirectAttributes rttr) throws Exception {
			
			//사용자가 비번을 잘못입력할 수 있으니 확인작업이 필요함.  -->로그인 로직을 가져오겠다.
			String mbsp_id = ((UserVO) session.getAttribute("login_status")).getMbsp_id();
			
			//비밀번호 컬럼 1개만 필요하지만, 로그인정보 사용해도 기능에 큰 문제는 없다.
			UserVO vo = userService.login(mbsp_id);
			
			String msg = "";   // 로그인 폼 주소
			String url = "/";  // 메인주소		
			
			if(vo != null) {  // 아이디가 존재한다면 (비밀번호도 존재한다 --> 비밀번호 비교작업)
				//비밀번호 비교 - u_id : 사용자가 입력한 비밀번호, vo.getU_pwd() : DB에 암호화된 비번
				if(passwordEncoder.matches(mbsp_password, vo.getMbsp_password())) {  // 사용자가 입력한 비번이 암호화된 형태에 해당하는 것이라면?

					//회원탈퇴(삭제)작업
					userService.delete(mbsp_id);
					session.invalidate(); //세션삭제작업 
					
					}else {
						msg = "failPW";
						url = "/user/delete";  //회원탈퇴폼 주소
		
						rttr.addFlashAttribute("msg", msg);
					}
			
				}
			
				
			return "redirect:"+ url;
		}
		
}
