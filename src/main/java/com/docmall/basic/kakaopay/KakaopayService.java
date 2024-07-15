package com.docmall.basic.kakaopay;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by kakaopay
 */
@Service
@Slf4j
public class KakaopayService {
//    @Value("${kakaopay.api.secret.key}")
//    private String kakaopaySecretKey;
//
//    @Value("${cid}")
//    private String cid;
//
//    @Value("${sample.host}")
//    private String sampleHost;

    private String tid;

    //1) 결제 준비요청(ready)
    public ReadyResponse ready() {
        // Request header
        HttpHeaders headers = new HttpHeaders();  // 헤더에 값을 담을 때 사용할 수 있다.
        //headers.add("Authorization", "DEV_SECRET_KEY " + kakaopaySecretKey);
        //headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization","SECRET_KEY DEV2D81342C94F24F1B0838CD4B516A9B5664D0A");
        headers.set("Content-Type", "application/json;charset=utf-8");
        
        // 2)Request param
        ReadyRequest readyRequest = ReadyRequest.builder()  //.builder()를 사용하면 매개변수를 사용한 생성자들을 만든것과 동일한 결과가 나온다. 빌터패턴이라 함.
                .cid("TC0ONETIME")
                .partnerOrderId("1")
                .partnerUserId("1")
                .itemName("상품명")
                .quantity(1)
                .totalAmount(1100)
                .taxFreeAmount(0)
                .vatAmount(100)
                .approvalUrl("http://localhost:9090/kakao/approval")	//성공. 카카오페이 서버에서 이 주소를 찾아옴.
                .cancelUrl("http://localhost:9090/kakao/cancel") 	//취소. 
                .failUrl("http://localhost:9090/kakao/fail")		//실패 . 
                .build();

        // Send reqeust
        HttpEntity<ReadyRequest> entityMap = new HttpEntity<>(readyRequest, headers);
        
        ResponseEntity<ReadyResponse> response = new RestTemplate().postForEntity(
                "https://open-api.kakaopay.com/online/v1/payment/ready",
                entityMap,
                ReadyResponse.class
        );
        
        //응답데이터
        ReadyResponse readyResponse = response.getBody();
        
        log.info("응답데이터" + readyResponse);

        // 주문번호와 TID를 매핑해서 저장해놓는다.
        // Mapping TID with partner_order_id then save it to use for approval request.
        this.tid = readyResponse.getTid(); // 전역변수 작업
        return readyResponse;
    }

    //2) 결제승인요청(approve)
    public String approve(String pgToken) {
        // ready할 때 저장해놓은 TID로 승인 요청
        // Call “Execute approved payment” API by pg_token, TID mapping to the current payment transaction and other parameters.
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization","SECRET_KEY DEV2D81342C94F24F1B0838CD4B516A9B5664D0A");
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Request param
        //2차 요청정보
        ApproveRequest approveRequest = ApproveRequest.builder()
                .cid("TC0ONETIME")
                .tid(tid)  //전역변수 tid를 여기서 사용한다.
                .partnerOrderId("1")
                .partnerUserId("1")
                .pgToken(pgToken)
                .build();

        // Send Request
        HttpEntity<ApproveRequest> entityMap = new HttpEntity<>(approveRequest, headers);
        try {
            ResponseEntity<String> response = new RestTemplate().postForEntity(
                    "https://open-api.kakaopay.com/online/v1/payment/approve",
                    entityMap,
                    String.class
            );

            // 승인 결과를 저장한다.
            // save the result of approval
            String approveResponse = response.getBody();
            return approveResponse;
        } catch (HttpStatusCodeException ex) {
            return ex.getResponseBodyAsString();
        }
    }

}
