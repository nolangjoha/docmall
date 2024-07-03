package com.docmall.basic.user;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserVO {
/*
 성능관점
   - 회원테이블(로그인포함)
   - 회원테이블 + 로그인 테이블 : 이 때 두 테이블은 1:1관계
 */	
	
/* 총14개
mbsp_id, mbsp_name, mbsp_email, mbsp_password, mbsp_zipcode, mbsp_addr, mbsp_deaddr, 
mbsp_phone, mbsp_nick, mbsp_receive, mbsp_point, mbsp_lastlogin, mbsp_datesub, mbsp_updatedate
*/

	private	String mbsp_id;				// 1.아이디
	private String mbsp_name;			// 2.이름
	private String mbsp_email;			// 3.이메일
	private String mbsp_password;		// 4.비밀번소
	private String mbsp_zipcode;		// 5.우편번호
	private String mbsp_addr;			// 6.주소
	private String mbsp_deaddr;			// 7.세부주소
	private String mbsp_phone;			// 8.휴대폰
	private String mbsp_nick;			// 9.닉네임
	private String mbsp_receive;		// 10. 마케팅수신여부
	private int mbsp_point;				// 11.포인트 (사용자입력x)
	private Date mbsp_lastlogin;		// 12.최근 로그인 (사용자입력x)
	private Date mbsp_datesub;			// 13.최초 등록일 (사용자입력x)
	private Date mbsp_updatedate;		// 14.마지막 정보 갱신일 (사용자입력x)
	
}
