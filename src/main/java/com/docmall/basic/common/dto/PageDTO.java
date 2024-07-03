package com.docmall.basic.common.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PageDTO {
	
	private int startPage;  //각 블럭에서 출력할 시작페이지 번호
	private int endPage;    // 각 블럭에서 출력할 끝 페이지 번호
	
	private boolean prev, next; //다음,이전 아이콘을 표시여부: true값이면 아이콘 활성화, false는 비활성화
	
	private int total; //테이블의 전체 데이터 개수, ceil(무조건 올림)등을 써서 종료페이지 값을 구한다.
	
	//Criteria클래스를 사용
	private Criteria cri; 
	//getter 메서드 사용 가능
	//cri.getPageNum()
	//cri.getAmount()
	//cri.getType()
	//cri.getKeyWord()
	
	//생성자 : 페이지 기능을 구현하기 위한 생성자
	public PageDTO(Criteria cri, int total) {
		//생성자 안에서 위의 5개 필드값을 구하는 작업	
		this.cri = cri;
		this.total = total;
		
		//블럭마다 보여질 페이지번호의 개수. 예) 1		2	3	4	5	6	7	8	9	10
		int pageSize = 10; 
		int endPageInfo = pageSize - 1; //10 - 1 = 9. endPageInfo에 9값 대입
		
		//첫 블록에서 어떤 페이지를 눌러도 화면에 페이지 번호를 다시 출력하기 위하여 endPage변수의 값이 동일하게 공식을 처리하는 목적
		//cri.getPageNum(): 얻어온 페이지 값(사용자가 선택한 페이지)
		//cri.getPageNum() = 1		   (int)(Math.ceil(1/10.0)) * 10 => 10
		//cri.getPageNum() = 9		   (int)(Math.ceil(9/10.0)) * 10 => 10
		//cri.getPageNum() = 10		  (int)(Math.ceil(10/10.0)) * 10 => 10
		this.endPage = (int) (Math.ceil(cri.getPageNum()/(double)pageSize))*pageSize; // 엔드페이지 구현 후 시작페이지를 구한다.
		
		this.startPage = this.endPage - endPageInfo;
		//문제 : 여기까지는 현재는 총데이터 수에 맞는 endPage 값을 구하지 못한 상태
		
		//실제 총 데이터 수에 해당하는 블럭의 마지막 페이지
		int realEnd = (int) (Math.ceil((total*1.0) / cri.getAmount()));  //괄호 잘보기 
		//cri.getAmount() : 한페이지마다 출력할 건페이지 번호 수
		/*
		 예를 들어 total에 33 값이 들어왔을 때  
		(int) (Math.ceil((33*1.0) / 10)) 
		(int) (Math.ceil((33.0) / 10))
		(int) (Math.ceil(3.3))
		(int) (4.0)
		int 4
		int realEnd = int 4
		※int형, int형끼리 나누기를 하면 소수점 이하는 버려진다. 때문에 두값중 하나는 실수형으로 계산을 해야 반올림하는 식을 완성 할 수 있다. 
		*/
		
		//아래 조건식이 true면, endPage 변수의 값은 실제 테이블의 총개수를 이용한 마지막페이지 번호의 의미가 된다.
		if(realEnd <= this.endPage) {
			this.endPage = realEnd;
		}
		
		//[이전][다음] 표시여부
		this.prev = this.startPage > 1;
		this.next = this.endPage < realEnd;
	}
	
	
	
	
}
