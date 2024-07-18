package com.docmall.basic.admin.order;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.docmall.basic.common.dto.Criteria;
import com.docmall.basic.common.dto.PageDTO;
import com.docmall.basic.order.OrderVo;

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
	public void order_detail_info(Long ord_code, Model model) throws Exception {
		
		// 주문자(수령인) 정보
		OrderVo vo = adminOrderService.order_info(ord_code);
		
		// 주문상세정보
		
		
		// 결제정보
		
	}
	
	
}
