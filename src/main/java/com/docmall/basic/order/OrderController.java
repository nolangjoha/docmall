package com.docmall.basic.order;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.docmall.basic.cart.CartProductVo;
import com.docmall.basic.cart.CartService;
import com.docmall.basic.cart.CartVo;
import com.docmall.basic.user.UserService;
import com.docmall.basic.user.UserVO;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/order/*")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;
	
	private final CartService cartService;
	
	private final UserService userService;
	
	
	/*
	1) pro_list.html 바로구매(Modal) 2)pro_detail.html 바로구매  3) 장바구니에서 주문하기 // 에서 동작 시 아래 메서드 동작
	1번, 2번은 vo파라미터 사용 하는 것이 같다. 3번은 cartVo vo 파라미터를 사용하지 않기 때문에 필요가 없다.
	3번으로 아래 코드를 진행할때  
	
			//1) 장바구니 저장
		cartService.cart_add(vo);
	위 코드가 작동되면 에러가 발생하게 된다.
	
	*/
	// [주문내역 페이지]
	@GetMapping("/orderinfo")
	public String orderinfo(@RequestParam(value= "type", defaultValue= "direct") String type, CartVo vo, Model model, HttpSession session) throws Exception {
		
		//아이디 확보
		String mbsp_id = ((UserVO) session.getAttribute("login_status")).getMbsp_id();
		vo.setMbsp_id(mbsp_id);
		
		// 만약 장바구니에서 바로구매를 눌렀을 때 
		if(!type.equals("cartorder")) {
			//1) 장바구니 저장
			cartService.cart_add(vo);	
		}
		
		//2) 주문하기(장바구니 내역 불러와서)
		List<CartProductVo> cart_list = cartService.cart_list(mbsp_id);
		
		int total_price = 0;
		cart_list.forEach(d_vo -> d_vo.setPro_up_folder(d_vo.getPro_up_folder().replace("\\", "/")));

		for(int i=0; i < cart_list.size(); i++) {
			total_price += (cart_list.get(i).getPro_price() * cart_list.get(i).getCart_amount());
		}
		
		
		model.addAttribute("cart_list", cart_list);
		model.addAttribute("total_price", total_price);
		
		return "/order/orderinfo";
	}
	
	
	//[주문자와 동일]
	@GetMapping("/ordersame")
	public ResponseEntity<UserVO> ordersame(HttpSession session) throws Exception {
		ResponseEntity<UserVO> entity = null;
		
		//아이디 확보
		String mbsp_id = ((UserVO) session.getAttribute("login_status")).getMbsp_id();
		//회원정보 가져오기
		entity = new ResponseEntity<UserVO> (userService.login(mbsp_id), HttpStatus.OK);
		
		
		return entity;
	}
	
	
}
