package com.docmall.basic.admin.user;

import java.util.List;

import org.springframework.stereotype.Service;

import com.docmall.basic.common.dto.Criteria;

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
	
	// [메일발송 횟수 업데이트]
	public void mailSendCountUpdate(int idx) {
		adminUserMapper.mailSendCountUpdate(idx);
	}
	
	// [메일링 목록 불러오기]
	public List<MailmngVo> getMailInfoList(Criteria cri, String title) {
		return adminUserMapper.getMailInfoList(cri, title);
	}
	
	// [메일링 데이터 총갯수]
	public int getMailListCount(String title) {
		return adminUserMapper.getMailListCount(title);
	}
	
	// [발송메일링 정보]
	public MailmngVo getMailSendInfo(int idx) {
		return adminUserMapper.getMailSendInfo(idx);
	}
	
	// [메일링 수정]
	public void mailingedit(MailmngVo vo) {
		adminUserMapper.mailingedit(vo);
	}
	
	
	
}
