package com.docmall.basic.admin.order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.docmall.basic.common.dto.Criteria;
import com.docmall.basic.common.dto.PageDTO;
import com.docmall.basic.common.util.FileManagerUtils;
import com.docmall.basic.order.OrderVo;
import com.docmall.basic.payinfo.PayInfoService;
import com.docmall.basic.payinfo.PayInfoVo;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/order/*")
public class AdminOrderController {

	//상품이미지 업로드 경로
	@Value("${file.product.image.dir}")
	private String uploadPath;	
	
	private final AdminOrderService adminOrderService;
	
	private final PayInfoService payInfoService;
	
	
	// [주문목록 페이지]
	@GetMapping("/order_list")
	public void order_list(Criteria cri, Model model) throws Exception {

		cri.setAmount(2);
		
		// 주문목록 불러오기
		List<OrderVo> order_list = adminOrderService.order_list(cri);
		
		// 총주문데이터 가져오기
		int totalCount = adminOrderService.getTotalCount(cri);

		log.info("pagedto :" + new PageDTO(cri, totalCount));
		
		// 뷰에서 사용할 데이터값의 이름 model로 지정
		model.addAttribute("order_list", order_list);
		model.addAttribute("pageMaker", new PageDTO(cri, totalCount));
	}
	
	
	// [주문 상세정보] $("#popup_info").load("/admin/order/order_info?ord_code=" + ord_code); // ajax성격이지만 load메서드를 이용하므로 responseEntity를 사용하는 것이 아니라 일반 성격으로 불러와야 한다.
	@GetMapping("/order_detail_info")
	public ResponseEntity<Map<String, Object>> order_detail_info(Long ord_code, Model model) throws Exception {
		
		ResponseEntity<Map<String, Object>> entity = null;
		
		Map<String, Object> map = new HashMap<>();
		
		// 1)주문자(수령인) 정보
		OrderVo vo = adminOrderService.order_info(ord_code);
		map.put("ord_basic",vo);
		
		// 2)주문상세정보
		List<OrderDetailInfoVo> ord_product_list = adminOrderService.order_detail_info(ord_code);
		
		//클라이언트에 \를 /로 변환하여, model작업 전에 처리함.  2024\07\01   --> 2024/07/02로 바꿔줄거다.
		ord_product_list.forEach(ord_pro -> {
			ord_pro.setPro_up_folder(ord_pro.getPro_up_folder().replace("\\", "/"));
		});
		
		map.put("ord_pro_list",ord_product_list);
		
		// 3)결제정보
		PayInfoVo p_vo = payInfoService.ord_pay_info(ord_code);
		map.put("payinfo", p_vo);
		
		entity = new ResponseEntity<Map<String,Object>>(map, HttpStatus.OK);
		
		return entity;
	}
	
	//AdminProductController.java에서 가져옴.
	// [주문정보에서 사용할 이미지 보여주기]  1) <img src="매핑주소">  2) <img src="test.gif">  //우린 1번 방법 사용, 2번방법은 고정적인거, 1번은 스프링의 메서드를 이용해 가져옴.
	@GetMapping("/image_display")
	public ResponseEntity<byte[]> image_display(String dateFolderName, String fileName) throws Exception {
		
		return FileManagerUtils.getFile(uploadPath + dateFolderName, fileName);
	}
	
	
	//[주문상품 개별삭제]
	@GetMapping("/order_product_delete")
	public ResponseEntity<String> order_product_delete(Long ord_code, int pro_num) throws Exception {
		ResponseEntity<String> entity = null;
		
		//db연동
		adminOrderService.order_product_delete(ord_code, pro_num);
		
		
		//<트랜젝션 작업이 필요함.>-->service에서 작업이 필요함.
		// 주문테이블 주문금액 변동.
		
		// 결제테이블 주문금액 변동.
		
		
		entity = new ResponseEntity<String>("success", HttpStatus.OK);
		return entity;
	}
	
	//[상세주문정보 수정]
	@PostMapping("/order_basic_modify")  	// HttpSession session 관리자쪽은 이걸 사용하면 안된다. 이건 사용자 아이디. 관리자 아이디가 아님.
	public ResponseEntity<String> order_basic_modify(OrderVo vo) throws Exception {
		
		log.info("주문기본정보:" + vo);
		
		ResponseEntity<String> entity = null;
		
		//db연동
		adminOrderService.order_basic_modify(vo);
		
		entity = new ResponseEntity<String>("success", HttpStatus.OK);
		return entity;
	}
	
	
}
