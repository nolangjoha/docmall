package com.docmall.basic.common.dto;

import org.springframework.web.util.UriComponentsBuilder;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Criteria {

	//페이지 필드
	private int pageNum; // 선택된 페이지 번호를 저장하기 위한 변수, 처음엔 1
	private int amount;  // 페이지마다 출력할 게시물 개수, 10을 넣으면 페이지당 10개씩 출력 
	
	//검색필드
	private String type;  	// 선택한 검색종류(옵션6가지) : 1.제목만(T), 2.글작성자(W), 3.내용(C) 4. 제목 or 작성자(TW) 5. 제목 or 내용(TC) 6. 제목 or 작성자 or 내용(TWC)
	private String keyword; // 검색어 저장되는 곳.
	
	//생성자 : 1페이지부터 보여주기 위해 해당 생성자를 생성했다.
	// 기본페이지에서 1~10으로 생성하겠다.
	public Criteria() {
		this(1, 10);
	}
	
	//매개변수가 있는 생성자
	public Criteria(int pageNum, int amount) {
		this.pageNum = pageNum;
		this.amount = amount;
		
		//확인용
		System.out.println("pageNum:"+ pageNum +", amount:" + amount);
	}
	
	// 여기 위에까지는 먼저 만든다.
	// 여기 아래는 필요할때마다 추가한다.
	
	
	//아래 메서드명은 getter의 메서드 이름 규칙대로 작성해야 한다. get(접두사) + typeArr(필드) = getTypeArr 메서드명
	//클라이언트로부터 검색종류가 {정보 또는}로 선택되어 지면 type필드에 값이 TWC로 넘어간다. 
	// type.split("")  -> "TWC".split("") -> "T" "W" "C"  -> mapper.xml이 가져다 쓴다.   -> xml의 foreach에 값이 3번 들어가므로 3번 작동된다.
	// getTypeArr()는 boardMapper.xml파일에서 사용. typeArr(필드)이름으로 참조하지만, 실제는 아래 getter메서드가 내부적으로 호출됨.
	public String[] getTypeArr() { // 필드에 typeArr는 없지만 return으로 값을 제어해줌...xml에서 사용하기 위해 get스타일의 메서드를 만든것이다.
		return type == null ? new String[] {} : type.split("");  // 클라이언트로부터 값을 받지 못했을때(null) > 공백상태가 되는것. 
	}
	
	//UriComponentsBuilder : 여러개의 파라미터를 연결하여 URL 형태로 만들어주는 기능
	// ?pageNum=2&amount=10&type=T&keyword=사과
	public String getListLink() {
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("")
				.queryParam("pageNum", this.pageNum)
				.queryParam("amount", this.amount)
				.queryParam("type", this.type)
				.queryParam("keyword", this.keyword);
				
		return builder.toUriString();
	}
	
	
	
}
