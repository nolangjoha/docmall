package com.docmall.basic.kakaologin;

public interface KakaoMapper {

	// [카카오 정보 존재유무]
	KakaoUserInfo existsKakaoInfo(String sns_email);
	
	// [카카오 계정 DB추가]
	void kakao_insert(KakaoUserInfo kakaoUserInfo);
	
	
}
