package com.docmall.basic.admin.user;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.docmall.basic.common.constants.Constants;
import com.docmall.basic.common.dto.Criteria;
import com.docmall.basic.common.dto.PageDTO;
import com.docmall.basic.common.util.FileManagerUtils;
import com.docmall.basic.mail.EmailDTO;
import com.docmall.basic.mail.EmailService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/user/*")
public class AdminUserController {

	private final AdminUserService adminUserService;

	//이메일 주입작업
	private final EmailService emailService;
	
	
	//CKeditor파일 업로드 경로
	@Value("${file.ckdir}") // 환경설정에 있던 임의로 지정한 파일업로드 경로 이름, 이 이름을 통해 C:\\Dev\\upload\\ckeditor\\경로가 들어오게 되는 것.
	private String uploadCKpath;
	
	
	//https://www.onedaynet.co.kr/ 참고
	
	// 회원목록(검색, 페이징 포함)
	
	//회원조회 및 정보수정
	
	
	
	
	// [메일발송목록]
	@GetMapping("/mailinglist")
	public void mailinglist(Criteria cri, String title, Model model) throws Exception {
		
		cri.setAmount(Constants.ADMIN_MAILING_LIST_AMOUNT);
		
		List<MailmngVo> maillist = adminUserService.getMailInfoList(cri, title);
		
		int totalCount = adminUserService.getMailListCount(title);
		PageDTO pageDTO = new PageDTO(cri, totalCount);
		
		model.addAttribute("maillist", maillist);
		model.addAttribute("pageMaker", pageDTO);
	}
	
	
	// <스프링의 기본, 기초>
	// 일반 메서드(매핑주소 없음)를 호출하는 경우에는 파라미터(매개변수)값을 제공해줘야 함.
	// 주소에 의하여 호출되는 메서드(매핑주소 있음)는 파라미터를 스프링이 관여하여, 객체를 먼저 생성한다. 그리고 사용자가 입력한 값이 setter메서드에 의하여 객체에 저장된다.
	// MailmngVo vo : 처음엔 데이터가 없음. 데이터 없이 메모리만 생성되어 있는 상태
	// [메일발송폼](CKEditor 사용) - 구분 1.광고/이벤트 2.일반
	@GetMapping("/mailingform")
	public void mailingform(@ModelAttribute("vo") MailmngVo vo) {
		// 매핑주소없이 호출되는 일반메서드 들은 메서드 호출하면 매개변수에 값을 제공해줘야함.
		// 매핑주소(url주소)로 호출되는 메서드 들은 스프링이 관여해서 매개변수가 이미 객체를 갖고있다. 매개변수가 힙 영역에 디폴트 생성자로 먼저 생성되어 있음.
		
		
	}
	
	// [메일저장]
	@PostMapping("/mailingsave")
	public String mailingsave(@ModelAttribute("vo") MailmngVo vo, RedirectAttributes rttr, Model model) throws Exception {

		log.info("메일내용:" + vo); 
		
		adminUserService.mailing_save(vo);  //vo에는 데이터가 아니라 참조(주소)값이 들어가 있는것. 클래스는 참조타입임. 최초 데이터가 생성된 곳은 MailmngVo.
		
		log.info("시퀀스:" + vo.getIdx());

		model.addAttribute("idx", vo.getIdx()); // 메일보내기 횟수 작업에 사용. 이 값을 타임리프 페이지에 hidden으로 숨겨놔야 함.
		
//		rttr.addFlashAttribute("msg", "save"); // return에 redirect 사용시에만 적용됨.
		
		model.addAttribute("msg", "save");
		
		return "/admin/user/mailingform"; //redirect뺌.
		// redirect로사용: 새로운 주소로 요청하여 해당 주소로 이동함. 해당 주소가 맵핑주소가 되는것
		// redirect로사용x: 쌩 주소만 있으면 이건 타임리프나 jsp페이지로 인식함. 
		// mailingform에 매개변수(@ModelAttribute("vo") MailmngVo vo)가 없다면 에러가 발생한다.
		//
	}
	
	
	// [메일발송]
	@PostMapping("/mailingsend")
	public String mailingsend(MailmngVo vo, RedirectAttributes rttr) throws Exception {

		log.info("메일내용:" + vo);
		
		// 1)메일발송
		// 1.1)회원테이블에서 전체 회원 메일정보를 읽어오는 작업
		String[] emailArr = adminUserService.getAllMailAddress();
				
		//EmailDTO dto = new EmailDTO("DocMall", "DocMall", "수신자메일주소", "제목", "내용");
		EmailDTO dto = new EmailDTO("DocMall", "DocMall", "", vo.getTitle(), vo.getContent());

		emailService.sendMail(dto, emailArr); 
		
		// 2)메일발송 횟수 업데이트
		adminUserService.mailSendCountUpdate(vo.getIdx());
		
		rttr.addFlashAttribute("msg", "send");
		
		return "redirect:/admin/user/mailinglist";
	}
	
	
	@GetMapping("/mailingsendform")
	public void mailsendform(int idx, Model model) throws Exception {
	
		MailmngVo vo = adminUserService.getMailSendInfo(idx);
		
		model.addAttribute("vo", vo);
	}
	
	
	@PostMapping("/mailingedit")
	public String mailingedit(@ModelAttribute("vo") MailmngVo vo, Model model) throws Exception {
		
		//db수정작업
		adminUserService.mailingedit(vo);
	
		model.addAttribute("msg", "modify");
		
		return "/admin/user/mailingsendform";
	}
	
	
	
	
	
	// [CKEditor 상품설명 이미지 업로드]
	// [CKeditor 업로드]
	// MultipartFile upload : CKEditor의 업로드 탭에서 나온 파일첨부 <input type="file" name="upload">을 참조함.
	//'upload'가 매우 중요함. 회사마다 다름. 위 방법으로 확인하고 일치시켜줘야함. 
	//HttpServletRequest request : 클라이언트의 요청정보를 가지고 있는 객체
	//HttpServletResponse response : 서버에서 클라이언트에게 보낼 정보를 응답하는 객체
	// < 1. 이미지를 서버에 전송>
	@PostMapping("/imageupload")
	public void imageupload(HttpServletRequest request, HttpServletResponse response, MultipartFile upload) {
		
		OutputStream out = null;
		PrintWriter printWriter = null; // 서버에서 클라이언트에게 응답정보를 보낼때 사용.(업로드한 이미지정보를 브라우저에게 보내는 작업용도)
		
		try {
			//1)CKeditor를 이용한 파일업로드 처리.
			String fileName = upload.getOriginalFilename(); // 업로드 할 클라이언트 파일이름
			byte[] bytes = upload.getBytes(); // 업로드 할 파일의 바이트배열
			
			String ckUploadPath = uploadCKpath + fileName; // "C:\\Dev\\upload\\ckeditor\\" + "abc.gif"
			
			out = new FileOutputStream(ckUploadPath); // "C:\\Dev\\upload\\ckeditor\\abc.gif" 파일생성. 0 byte
			
			out.write(bytes); // 빨대(스트림)의 공간에 업로드할 파일의 바이트배열을 채운상태.
			out.flush(); // "C:\\Dev\\upload\\ckeditor\\abc.gif" 0 byte -> 크기가 채워진 정상적인 파일로 인식.
			
			//2)업로드한 이미지파일에 대한 정보를 클라이언트에게 보내는 작업
			
			printWriter = response.getWriter();
			
			String fileUrl = Constants.ROOT_URL + "/admin/product/display/" + fileName; // 매핑주소/이미지파일명
//			String fileUrl = fileName;
			
			
			// Ckeditor 4.12에서는 파일정보를 다음과 같이 구성하여 보내야 한다.
			// {"filename" : "abc.gif", "uploaded":1, "url":"/ckupload/abc.gif"}
			// {"filename" : 변수, "uploaded":1, "url":변수}
			printWriter.println("{\"filename\" :\"" + fileName + "\", \"uploaded\":1,\"url\":\"" + fileUrl + "\"}"); // 스트림에 채움.
			printWriter.flush();
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}finally {
			if(out != null) {
				try {
					out.close();
				}catch(Exception ex) {
					ex.printStackTrace();
				}
			}
			if(printWriter != null) printWriter.close();
		}
	}
	
	
	// < 2. 전송된 이미지를 다시 에디터에 출력될 수 있도록 하는 작업>
	//<img src="매핑주소">
	@GetMapping("/display/{fileName}")
	public ResponseEntity<byte[]> getFile(@PathVariable("fileName") String fileName) {
		
		log.info("파일이미지: " + fileName);
		
		
		ResponseEntity<byte[]> entity = null;
		
		try {
			entity = FileManagerUtils.getFile(uploadCKpath, fileName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return entity;
		
	}
	
	
	
	
	
	
}
