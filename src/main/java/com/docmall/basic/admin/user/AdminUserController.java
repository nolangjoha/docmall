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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.docmall.basic.common.constants.Constants;
import com.docmall.basic.common.dto.Criteria;
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
	
	//메일발송목록
	@GetMapping("/mailinglist")
	public void mailinglist(Criteria cri, Model model) throws Exception {
		
	}
	
	//메일발송폼(CKEditor 사용) - 구분 1.광고/이벤트 2.일반
	@GetMapping("/mailingform")
	public void mailingform() {
		
	}
	
	//메일 프로세스
	@PostMapping("/mailingprocess")
	public String mailingprocess(MailmngVo vo, RedirectAttributes rttr) {
		
		// 1)메일내용 DB저장
		adminUserService.mailing_save(vo);
		
		// 2)메일발송
		// 2.1)회원테이블에서 전체 회원 메일정보를 읽어오는 작업
		String[] emailArr = adminUserService.getAllMailAddress();
				
		//EmailDTO dto = new EmailDTO("DocMall", "DocMall", "수신자메일주소", "제목", "내용");
		EmailDTO dto = new EmailDTO("DocMall", "DocMall", "", vo.getTitle(), vo.getContent());

		emailService.sendMail(dto, emailArr); 
		
		return "redirect:/admin/user/mailinglist";
	}
	
	// 메일발송 액션
	
	
	
	
	
	
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
