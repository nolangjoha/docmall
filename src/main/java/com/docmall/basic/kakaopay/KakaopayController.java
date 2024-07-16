package com.docmall.basic.kakaopay;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.docmall.basic.cart.CartProductVo;
import com.docmall.basic.cart.CartService;
import com.docmall.basic.order.OrderService;
import com.docmall.basic.order.OrderVo;
import com.docmall.basic.user.UserVO;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by kakaopay
 */
@Controller
@RequestMapping("/kakao/*")
@RequiredArgsConstructor
@Slf4j
public class KakaopayController {
    
    private final KakaopayService kakaopayService;
    private final CartService cartService;  // 장바구니 정보 가져오겠음.
    private final OrderService orderService;
    
    private OrderVo vo; // 전역으로 빼둔 주문정보
    private String mbsp_id;
    
    
    @GetMapping("/kakaoPayRequest")
    public void kakaoPayRequest() {
    	
    }
    
    @ResponseBody
    @GetMapping(value="/kakaoPay")
    public ReadyResponse kakaoPay(OrderVo vo, HttpSession session) {
    	
    	log.info("주문자정보: " + vo);
    	
    	//1) 결제준비요청(ready)
    	/*
    	ready(String partnerOrderId, String partnerUserId,  String itemName, int quantity, int totalAmount , int taxFreeAmount, int vatAmount)
    	*/
    	//로그인 아이디 확보
    	String mbsp_id = ((UserVO) session.getAttribute("login_status")).getMbsp_id(); // 이걸로 장바구니 정보를 가져오겠다.
    	this.mbsp_id = mbsp_id;
    	
    	
    	List<CartProductVo> cart_list = cartService.cart_list(mbsp_id); //카트 목록 가져옴.
    	
    	//for문 돌기 전에 값을 가져옴.
    	String itemName = "";  //장바구니에서 참조
    	int quantity = 0;
    	int totalAmount = 0;
    	int taxFreeAmount = 0;
    	int vatAmount = 0;
    	
    	for(int i = 0; i < cart_list.size(); i++) {
    		itemName += cart_list.get(i).getPro_name() + "/";
    		quantity += cart_list.get(i).getCart_amount();
    		totalAmount += cart_list.get(i).getPro_price() * cart_list.get(i).getCart_amount();
    	
    	
    	}
    	
    	String partnerOrderId = mbsp_id;
    	String partnerUserId = mbsp_id;
//    	String itemName = "장바구니에서 참조";
//    	int quantity = 0;
//    	int totalAmount = 0;
//    	int taxFreeAmount = 0;
//    	int vatAmount = 0;
    	
    	ReadyResponse readyResponse = kakaopayService.ready(partnerOrderId, partnerUserId, itemName, quantity, totalAmount, taxFreeAmount, vatAmount);
    	
    	log.info("응답데이터:" + readyResponse);
    	
    	//주문정보
    	this.vo = vo;
    	
    	return readyResponse;
    }
    
    
    //성공
    @GetMapping("/approval")
    public void approval(String pg_token) {
    	
    	log.info("pg_token값:" + pg_token);
    	
    	//2) 결제 승인 요청
    	String approveResponse = kakaopayService.approve(pg_token);	
    	
    	log.info("최종결과 : " + approveResponse);
    	
    	// 주문정보 저장.

    	// aid값이 존재하면
    	
    	/*
    	트랜잭션으로 처리 : 하나의 기능에서 여러 DB작업이 일어나는 경우, 하나라도 오류가 나면 낫띵처리해서 다 진행되지 않게 해버린다.
    				   금융권에서 주로 사용, 계좌이체시 출금하여 입금처리 할때 오류가 발생하면 롤백하여 다시 출금한 금액을 되돌리고 아무일도 안 일어난 것처럼 처리
    				   주문테이블(insert), 주문상세테이블(insert), 결제테이블(insert), 장바구니 비우기(delete) //(4가지 작업)
    	*/
    	if(approveResponse.contains("aid")) {
    		log.info("주문자정보2: " + vo);
    		orderService.order_process(vo, mbsp_id);
    		
    	}
    	
    	
    	
    	
    }
    
    //취소
    @GetMapping("/cancel")
    public void cancel() {
    	
    }
    
    // 실패
    @GetMapping("/fail")
    public void fail() {
    	
    }
    
    
    
    
     
//
//    @GetMapping("/")
//    public String index() {
//        return "index";
//    }
//
//    @GetMapping("/ready/{agent}/{openType}")
//    public String ready(@PathVariable("agent") String agent, @PathVariable("openType") String openType, Model model) {
//        ReadyResponse readyResponse = kakaopayService.ready();
//
//        // pc
//        model.addAttribute("response", readyResponse);
//        return agent + "/" + openType + "/ready";
//    }
//
//    @GetMapping("/approve/{agent}/{openType}")
//    public String approve(@PathVariable("agent") String agent, @PathVariable("openType") String openType, @RequestParam("pg_token") String pgToken, Model model) {
//        String approveResponse = kakaopayService.approve(pgToken);
//        model.addAttribute("response", approveResponse);
//        return agent + "/" + openType + "/approve";
//    }
//
//    @GetMapping("/cancel/{agent}/{openType}")
//    public String cancel(@PathVariable("agent") String agent, @PathVariable("openType") String openType) {
//        // 주문건이 진짜 취소되었는지 확인 후 취소 처리
//        // 결제내역조회(/v1/payment/status) api에서 status를 확인한다.
//        // To prevent the unwanted request cancellation caused by attack,
//        // the “show payment status” API is called and then check if the status is QUIT_PAYMENT before suspending the payment
//        return agent + "/" + openType + "/cancel";
//    }
//
//    @GetMapping("/fail/{agent}/{openType}")
//    public String fail(@PathVariable("agent") String agent, @PathVariable("openType") String openType) {
//        // 주문건이 진짜 실패되었는지 확인 후 실패 처리
//        // 결제내역조회(/v1/payment/status) api에서 status를 확인한다.
//        // To prevent the unwanted request cancellation caused by attack,
//        // the “show payment status” API is called and then check if the status is FAIL_PAYMENT before suspending the payment
//        return agent + "/" + openType + "/fail";
//    }
}
