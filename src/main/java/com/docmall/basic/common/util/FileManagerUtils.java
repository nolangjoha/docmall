package com.docmall.basic.common.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import net.coobird.thumbnailator.Thumbnails;   //썸네일 관련 라이브러리

//여러 Controller에서 사용될수 있는 기능이므로 클래스로 빼버렸음.(재사용성)

//@Component  // 스프링에서 클래스를 자동관리. bean으로 관리 할 수 있게 해준다.
public class FileManagerUtils {

	// [기능1]현재폴더를 운영체제에 맞게 문자열로 반환.
	public static String getDateFolder() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //날짜 포맷 형식
		Date date = new Date(); // 오늘 날짜 정보
		
		String str = sdf.format(date); // "2024-05-16" 폴더명 문자열 
	
		//File.separator: 이 코드를 실행하는 운영체제별로 파일의 경로 구분자를 리턴.
		/*
		  - 유닉스, 맥, 리눅스 구분자 : /    (예)"2024-05-16"   >  "2024/05/16"
		  - 윈도우 구분자 : \(역슬래시) 		(예)"2024-05-16"   >  "2024\05\16"	
		 */
		
		return str.replace("-", File.separator);
		//str변수안에 들어있던 값이 file.separator로 인해 맞는 운영체제로 변환된다.
	}
	
	
	// [기능2]업로드 기능을하는 메서드
	/*
	 - String uploadFoledr : 업로드 폴더명 ("C:\\Dev\\upload\\pds" 까지 들어옴)
	 - String dateFolder : 업로드 되는 날짜폴더명    ("2024\\05\\16")
	 - MultipartFile uploadFile : 클라이언트에서 전송한 파일이 들어오는 폴더
	 
	 */
	public static String uploadFile(String uploadFoledr, String dateFolder, MultipartFile uploadFile) {
		
		String realUploadFileName = ""; //실제 업로드한 파일명. 
										//우리는 하나의 파일을 올렸지만 서버에는 이름이 중복된 파일이 많을것이다. 이에 이름을 바꿔주는 것. 
		
		//File클래스 : JDK제공. 파일과 폴더관련 기능을 제공.
		/*
		File file = new File(파일 또는 폴더정보구성);  file,명령어(속성과 메서드)
		 - 파일 또는 폴더 존재여부확인
		 - 존재하지 않으면 파일 또는 폴더 생성
		 - 존재하면 파일 또는 폴더 속성확인		 
		*/
		File file = new File(uploadFoledr, dateFolder);  //(예) ":\\Dev\\upload\\pds"(고정)   "2024\\05\\16"(동적)
		
		// "2024/05/16" 폴더가 존재하지 않으면, 폴더 생성
		// 새로운 날짜에 첫 파일업로드가 진행되면, 폴더생성되고, 두번째 파일업로드부터는 폴더가 생성되지 않든다.
		if(file.exists() == false) {
			file.mkdirs();  //폴더가 빠져도 다 생성하겠다. 
			 //mkdirs는 폴더를 여러개 생성
			 // mkdir은 폴덜르 1개만 생성(개발에서 잘 쓰지 않는다.)
		}
		
		//클라이언트에서 보낸 파일명    UUID는 고유식별자를 말하며 고유한 값을 생성해줌.
		String clientFileName = uploadFile.getOriginalFilename(); // abc.png		
		UUID uuid = UUID.randomUUID(); //2f48f241-9d64-4d16-bf56-70b9d4e0e79a(인터넷에서 가져온 아무 고유값)
									   //랜덤으로 값을 생성해준다. 중복이 나올수는 있으나 거의 나오지 않는다.
		
		//2f48f241-9d64-4d16-bf56-70b9d4e0e79a_abc.png
		realUploadFileName = uuid.toString() + "_" + clientFileName;
	
		//예외처리작업
		try {
			File saveFile = new File(file, realUploadFileName);
			uploadFile.transferTo(saveFile); //파일복사(원본)
			
			//Thumnail 작업(사본)// 파일명이 이미지여야 작업이 가능함.
			//원본파일 해상도크기를 줄여 섬네일이미지 생성하기
			if(checkImageType(saveFile)) {
				// Thumnail 파일명 : 2f48f241-9d64-4d16-bf56-70b9d4e0e79a_abc.png 생성
				// thumnailFile 객체 : "C:\\Dev\\upload\\pds" "2f48f241-9d64-4d16-bf56-70b9d4e0e79a_abc.png"
				File thumbnailFile = new File(file, "s_" + realUploadFileName);
			
				//saveFile객체 : 업로드 된 파일정보
				BufferedImage bo_img = ImageIO.read(saveFile);
				
				double ratio = 3;
				int width = (int) (bo_img.getWidth() / ratio);
				int height = (int) (bo_img.getHeight() / ratio);
				
				Thumbnails.of(saveFile) 
						  .size(width, height)
						  .toFile(thumbnailFile);  
				
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return realUploadFileName;	
	}
	

	// [기능3] 업로드파일의 MIME타입 확인. 즉 이미지 파일 또는 일반파일 여부를 체크
	public static boolean checkImageType(File saveFile) {

		boolean isImageType = false;
		try {
			//MIME : text/html, text/plain,image/jpeg,.....
			// 클라이언트에서 전송한 파일의 MIME정보 추출
			//응용프로그램들은 자기네들의 확장자명이 있는데 네트워크 상에서는 mime으로 어떤 속성 파일인지 가르쳐 준다.
			String contentType = Files.probeContentType(saveFile.toPath());  //MIME정보 추출
	
			//contentType의 내용이 "image"문자열 시작여부 boolean값 반환
			isImageType = contentType.startsWith("image");
		}catch(IOException e) {
			e.printStackTrace();
		}
		return isImageType;
	}
	
	
	// [기능4]이미지파일을 웹브라우저 화면에 보이는 작업.
	// <img src="abc.gif"> <img src="매핑주소"> 매핑주소를 통한 서버측에서 받아오는 바이트배열을 이용하여 브라우저가 이미지를 표시한다.
	/*
	  - String uploadPath : 서버 업로드폴더  예)"C:\\Dev\\upload\\pds"
	  - String fileName : 이미지 파일명 (날짜폴더명 포함)
	 */
	
	// 파일업로드되는 폴더가 외부 프로젝틍 존재하여, 보안적인 이슈가 있으므로, 업로드 파일들을 byte배열로 읽어서 클라이언트로 보낸다.
	public static ResponseEntity<byte[]> getFile(String uploadPath, String fileName) throws Exception{
		ResponseEntity<byte[]> entity = null;
		File file = new File(uploadPath, fileName);  // 이 경로는 실제 파일이 존재 해야 함.
		
		if(!file.exists()) { //파일이 존재하지 않으면 entity를 반환한다.
			return entity;
		}
		
		
		HttpHeaders headers = new HttpHeaders();
		//Files.probeContentType(file.toPath()) : MIME TYPE정보 예)image/jpeg
		headers.add("Content-Type", Files.probeContentType(file.toPath()));
		
		entity= new ResponseEntity<byte[]>(FileCopyUtils.copyToByteArray(file), headers, HttpStatus.OK);
		
		return entity;
	}
	
	
	// [기능5] 이미지파일 삭제
	/*
	 - String uploadPath : 서버업로드 폴더
	 - String folderName : 날짜 폴더명
	 - String fileName : 파일명(날자폴더명 포함)
	*/
	
	public static void delete(String uploadPath, String dateFolderName, String fileName, String type) {
		
		// 2) 원본파일 fileName.substring(2)) : s_2f48f241-9d64-4d16-bf56-70b9d4e0e79a_abc.png
		File file2 = new File((uploadPath + "\\" + dateFolderName + "\\" + fileName.substring(2)).replace('\\', File.separatorChar));
		if(file2.exists()) file2.delete();  //파일이 존재하면 삭제한다.

		if(type.equals("image")) {
			// 1)thumnail파일  예)"C:/Dev/upload/pds"  "2024/05/06"     "s_2f48f241-9d64-4d16-bf56-70b9d4e0e79a_abc.png"
			File file1 = new File((uploadPath + "\\" + dateFolderName + "\\" + fileName).replace('\\', File.separatorChar));
			if(file1.exists()) file1.delete();  // 썸네일파일이 존재하면 삭제한다.
		}
		
	}
	
}
