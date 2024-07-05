package com.docmall.basic.cart;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.docmall.basic.common.util.FileManagerUtils;
import com.docmall.basic.user.UserVO;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/cart/*")
public class CartController {

	private final CartService cartService;

	//상품이미지 업로드 경로
	@Value("${file.product.image.dir}")
	private String uploadPath;	
	
	// [장바구니 추가]
	@GetMapping("/cart_add") // ajax로 요청받음 리턴타입은 엔티티, success를 받을 거니까<String> // 회원제로 운영할거니까 session필요 --> 매개변수 3개 컬럼 사용 됐다(상품코드, 사용자아이디, 수량)
	public ResponseEntity<String> cart_add(CartVo vo, HttpSession session) throws Exception {
											//int pro_num, int cart_amount만 썼는데 mbsp_id도 가져올 거니까, 그냥 카트 클래스 넣음.   //cart_code는 시퀀스 사용 예정. cart_data는 디폴드값으로 사용 예정.
		
		log.info("장바구니 데이터:" + vo);
		
		//userController에 아아디 뽑아오는 작업(mypage) 복붙
		String mbsp_id = ((UserVO) session.getAttribute("login_status")).getMbsp_id();
		vo.setMbsp_id(mbsp_id);
		
		ResponseEntity<String> entity = null;
		
		//db연동작업
		cartService.cart_add(vo);
		
		entity = new ResponseEntity<String>("success", HttpStatus.OK);
		
		return entity;
	}
	
	//[장바구니 목록]
	@GetMapping("/cart_list")
	public void cart_list(HttpSession session, Model model) throws Exception {
		// 사용자의 아이디만 이용하면 목록을 가져올 수 있다.
		
		//userController에 아아디 뽑아오는 작업(mypage) 복붙 // 아이디 확보
		String mbsp_id = ((UserVO) session.getAttribute("login_status")).getMbsp_id();

		//db작업
		List<CartProductVo> cart_list =  cartService.cart_list(mbsp_id);
		//이미지 경로 '/' 수정작업
		cart_list.forEach(vo -> vo.setPro_up_folder(vo.getPro_up_folder().replace("\\", "/")));
		/*
		 람다식 문법
		 cart_list에 여러 값이 들어있음. 그걸 vo에 넣음.
		 날짜 메세지 중에 \\있는걸 /로 바꿔서 vo.setPro_up_folder에 대입해라
		 그리고 원래있던 vo를 vo.setPro_up_folder로 바꿔라.
		 */
		
		model.addAttribute("cart_list", cart_list);
	}
	
	// [상품리스트에서 사용할 이미지 보여주기]  1) <img src="매핑주소">  2) <img src="test.gif">  //우린 1번 방법 사용, 2번방법은 고정적인거, 1번은 스프링의 메서드를 이용해 가져옴.
	@GetMapping("/image_display")
	public ResponseEntity<byte[]> image_display(String dateFolderName, String fileName) throws Exception {
		
		return FileManagerUtils.getFile(uploadPath + dateFolderName, fileName);
	}
	
	
	// [장바구니 삭제]
	@GetMapping("/cart_del")
	public String cart_del(Long cart_code) throws Exception {
		
		cartService.cart_del(cart_code);
		
		return "redirect:/cart/cart_list";
	}
	
	
	
	// [장바구니 수량변경]
	@GetMapping("/cart_change")
	public String cart_change(Long cart_code, int cart_amount) throws Exception {
		
		cartService.cart_change(cart_code, cart_amount);
		
		return "redirect:/cart/cart_list";
	}
	
	
}
