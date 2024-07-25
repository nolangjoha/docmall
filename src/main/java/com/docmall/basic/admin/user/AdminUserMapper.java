package com.docmall.basic.admin.user;

import java.util.List;

public interface AdminUserMapper {

	//[메일내용 DB저장]
	void mailing_save(MailmngVo vo);
	
	// [회원테이블에서 전체 회원 메일정보를 읽어오는 작업]
	String[] getAllMailAddress();
	
	
	
}
