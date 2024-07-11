package com.docmall.basic.review;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.docmall.basic.common.dto.Criteria;
import com.docmall.basic.common.dto.PageDTO;
import com.docmall.basic.user.UserVO;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//pro_detail.html 에서 상품 후기 처리.

@RestController   //jsp, 타임리프 페이지가 필요없음.  pro_detail.html파일 내에서 모두 처리가 될 예정이므로.
@Slf4j
@RequestMapping("/review/*")
@RequiredArgsConstructor 
public class ReviewController {

	private final ReviewService reviewService;
	
	// [리뷰목록]+[페이징] 1)방법으로 한다.		
	// 1)rest api개발방법론 /revlist/상품코드/페이지번호		  //revlist/10/1   
	// 2)전통적인 주소라면  : /revlist?pro_num=10&page=1   / @PathVariable: 매핑주소의 파트부분을 매개변수로 사용하고자 할 경우
	@GetMapping("/revlist/{pro_num}/{page}")    // 매개변수 : 경로에 들어있던 값을 받게 된다.
	public ResponseEntity<Map<String, Object>> revlist(@PathVariable("pro_num") int pro_num, @PathVariable("page") int page) throws Exception{
		ResponseEntity<Map<String, Object>> entity = null;
		
		Map<String, Object> map = new HashMap<>();
		
		// [1.후기목록]
		Criteria cri = new Criteria();
		cri.setAmount(2);
		cri.setPageNum(page);  //페이지 정보 삽입
		
		//db에서 가져온 후기목록 데이터
		List<ReviewVo> revlist = reviewService.rev_list(pro_num, cri);
		
		// [2.페이징정보]
		int revcount =  reviewService.getCountReviewByPro_num(pro_num);
		PageDTO pageMaker = new PageDTO(cri, revcount);
		
//		map.put("realist", "후기목록데이터");
		map.put("revlist", revlist);
//		map.put("pageMaker", "페이징정보");
		map.put("pageMaker", pageMaker);
		
													//map이 상품 상세페이지로 나가는 정보
		entity = new ResponseEntity<Map<String, Object>>(map,HttpStatus.OK);
		
		return entity;
	}
	
	
	//JacksonDatabind 라이브러리가 있어야 아래가 동작된다. 해당 라이브러리는 Spring web에 포함되있다.
	//json작업을 할때는 잭슨 라이브러리가 동작되고 있다는 것을 기억하고 있어야 한다.
	//상품후기 저장
//	@PostMapping(value = 매핑이름(매핑주소) , consumes = 클라이언트에게서 넘어오는 값(후기)를 포맷(MIME): json데이터만 받겠다. 다른값 들어오면 에러처리,  produces = {}   )
	@PostMapping(value = "/review_save", consumes = {"application/json"}, produces = {MediaType.TEXT_PLAIN_VALUE})
	public ResponseEntity<String> review_save(@RequestBody ReviewVo vo, HttpSession session) throws Exception {
		
		//세션작업 : 아이디 가져옴.
		String mbsp_id = ((UserVO) session.getAttribute("login_status")).getMbsp_id();
		vo.setMbsp_id(mbsp_id);
		
		log.info("상품후기데이터:" + vo);
		
		reviewService.review_save(vo);
		
		ResponseEntity<String> entity = null;

		entity = new ResponseEntity<String>("success", HttpStatus.OK);
		
		return entity;
	} 
	
	
	// [상품후기 삭제]
	@DeleteMapping("/review_delete/{re_code}")
	public ResponseEntity<String> review_delete(@PathVariable Long re_code) throws Exception {
		
		ResponseEntity<String> entity = null;
		
		log.info("장바구니코드" + re_code);
		reviewService.review_delete(re_code);
		
		entity = new ResponseEntity<String>("success", HttpStatus.OK);
		
		return entity;
	} 
	
	
	
}
