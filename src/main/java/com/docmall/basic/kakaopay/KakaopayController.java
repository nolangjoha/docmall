package com.docmall.basic.kakaopay;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
    
    
    @GetMapping("/kakaoPayRequest")
    public void kakaoPayRequest() {
    	
    }
    
    
    @ResponseBody
    @GetMapping(value="/kakaoPay")
    public ReadyResponse kakaoPay() {
    	ReadyResponse readyResponse = kakaopayService.ready();
    	
    	log.info("응답데이터:" + readyResponse);
    	
    	return readyResponse;
    }
    
    //성공
    @GetMapping("/approval")
    public void approval(String pg_token) {
    	
    	log.info("pg_token값:" + pg_token);
    	String approveResponse = kakaopayService.approve(pg_token);	//결제 승인 요청
    	log.info("최종결과 : " + approveResponse);
    	
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
