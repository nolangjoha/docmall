package com.docmall.basic.user;

import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

//구현클래스
@Service
@RequiredArgsConstructor
public class UserService{

	private final UserMapper userMapper;

	// [아이디 중복체크]
	public String idCheck(String mbsp_id) {
		return userMapper.idCheck(mbsp_id);
	}
	
	// [회원가입저장]
	public void join(UserVO vo) {
		userMapper.join(vo);		
	}
	
	// [로그인작업] 로그인을 위한 아이디는 1개만 가져오니까 UserVO로 작업한다.
	public UserVO login(String mbsp_id) {
		return userMapper.login(mbsp_id);
	}
	
	// [아이디 찾기]
	public String idfind(String mbsp_name, String mbsp_email) {
		return userMapper.idfind(mbsp_name, mbsp_email);
	}
	
	
	// [비밀번호 찾기] 
	public String pwfind(String mbsp_id, String mbsp_name, String mbsp_email) {
		return userMapper.pwfind(mbsp_id, mbsp_name, mbsp_email);
	}
	
	// [비밀번호 업데이트]
	public void tempPwUpdate(String mbsp_id, String temp_enc_pw) {
		userMapper.tempPwUpdate(mbsp_id, temp_enc_pw);
	}
	
	// 임시비밀번호 10자리
	public String getTempPw() {
		//uuid가 16자리의 비번을 랜덤으로 생성해줌 근데 중간에 "-"가 있어서 공백으로 바꿔줌. 10자리까지만.
		return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10);
	}
	
	
	// [마이페이지 수정하기]
	public void modify(UserVO vo) {
		userMapper.modify(vo);
	}
	
	// [비밀전호 변경작업]
	public void changePw(String mbsp_id,String new_mbsp_password) {
		userMapper.changePw(mbsp_id, new_mbsp_password);
	}
	
	// [회원탈퇴]
	public void delete(String mbsp_id) {
		userMapper.delete(mbsp_id);
	}
	
	// [sns계정정보 존재유무]
	public String exsistsUserInfo(String sns_email) {
		return userMapper.exsistsUserInfo(sns_email);
	}
	
	
	
	
	/*	카카오, 네이버로그인	*/
	// sns user데이터 삽입
	//String sns_email은 카카오 컨트롤러에 있음.
	public String sns_user_check(String sns_email) {
		return userMapper.sns_user_check(sns_email);
	}
	
	// [sns테이블 정보삽입]
	public void sns_user_insert(SNSUserDto dto) {
		userMapper.sns_user_insert(dto);
	};
	
	
	
	
	
}
