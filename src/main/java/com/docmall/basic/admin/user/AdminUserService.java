package com.docmall.basic.admin.user;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class AdminUserService {

	private final AdminUserMapper adminUserMapper;
	
	//[메일내용 DB저장]
	public void mailing_save(MailmngVo vo) {
		adminUserMapper.mailing_save(vo);
	}
	
	// [회원테이블에서 전체 회원 메일정보를 읽어오는 작업]
	public String[] getAllMailAddress() {
		return adminUserMapper.getAllMailAddress();
	}
	
	
	
	
	
	
	
	
}
