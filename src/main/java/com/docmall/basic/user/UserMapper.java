package com.docmall.basic.user;

import org.apache.ibatis.annotations.Param;

public interface UserMapper {
/*
mbsp_id, mbsp_name, mbsp_email, mbsp_password, mbsp_zipcode, mbsp_addr, mbsp_deaddr, mbsp_phone, 
mbsp_nick, mbsp_receive, mbsp_point, mbsp_lastlogin, mbsp_datesub, mbsp_updatedate

 * */
	
	
	// [아이디 중복체크]
	String idCheck(String mbsp_id);
	
	// [회원가입 저장]
	public void join(UserVO vo);
	
	// [로그인작업] 로그인을 위한 아이디는 1개만 가져오니까 UserInfVO로 작업한다.
	UserVO login(String mbsp_id);
	
	// [아이디 찾기] 파라미터 티입이 2개이면 파람 어노테이션을 붙여 작업해줘야 한다.
	String idfind(@Param("mbsp_name") String mbsp_name, @Param("mbsp_email") String mbsp_email);
	
	// [비밀번호 찾기]
	String pwfind(@Param("mbsp_id") String mbsp_id, @Param("mbsp_name") String mbsp_name, @Param("mbsp_email") String mbsp_email);
	
	// [비밀번호 업데이트]
	void tempPwUpdate(@Param("mbsp_id") String mbsp_id, @Param("temp_enc_pw") String temp_enc_pw);

	// [마이페이지 수정하기]
	void modify(UserVO vo);
	
	// [비밀전호 변경작업]
	// 파라미터타입이 2개면 파람작업을 해줘야 한다.
	void changePw(@Param("mbsp_id") String mbsp_id, @Param("new_mbsp_password") String new_mbsp_password);
	
	//[회원탈퇴] > 비번은 암호화 되어 있어서 파라미터가 2개 들어갈 수 없다, 사용자가 입력한 비번을 비교할 수 없다. DB에는 암호화 되어 들어가 있기 때문에 일치하지 않을 것. > 비교불가하니 Controller에서 matches를 사용하는 것.
	void delete(String mbsp_id); //시큐리티 라이브러리를 사용해서 암호화된 비밀번호가 DB에 저장되어 있기 때문에 사용자가 입력한 값과 일치 할 수 없다.
	
	// [sns계정정보 존재유무]
	String exsistsUserInfo(String sns_email);
	

	/*	카카오, 네이버로그인	*/
	// sns user데이터 삽입
	//String sns_email은 카카오 컨트롤러에 있음.
	String sns_user_check(String sns_email);
	
	// [sns테이블 정보삽입]
	void sns_user_insert(SNSUserDto dto);
	
}
