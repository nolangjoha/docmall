package com.docmall.basic.admin;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {

	private final AdminMapper adminmapper;
	
	
	// [관리자 로그인 기능]
	public AdminVO loginOk(String admin_id) {
		return adminmapper.loginOk(admin_id);
	};
	
	
	
	
}
