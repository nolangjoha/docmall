package com.docmall.basic.admin.user;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.docmall.basic.common.dto.Criteria;

public interface AdminUserMapper {

	// [메일내용 DB저장]
	void mailing_save(MailmngVo vo);  //시퀀스가 들어있음. 
	
	// [회원테이블에서 전체 회원 메일정보를 읽어오는 작업]
	String[] getAllMailAddress();
	
	// [메일발송 횟수 업데이트]
	void mailSendCountUpdate(int idx);
	
	// [메일링 목록 불러오기]
	List<MailmngVo> getMailInfoList(@Param("cri")Criteria cri, @Param("title")String title);
	
	// [메일링 데이터 총갯수]
	int getMailListCount(String title);
	
	// [발송메일링 정보]
	MailmngVo getMailSendInfo(int idx);
	
	// [메일링 수정]
	void mailingedit(MailmngVo vo);
	
}
