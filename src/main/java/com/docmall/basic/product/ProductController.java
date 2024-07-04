package com.docmall.basic.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.docmall.basic.admin.product.ProductVo;
import com.docmall.basic.common.dto.Criteria;
import com.docmall.basic.common.dto.PageDTO;
import com.docmall.basic.common.util.FileManagerUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/product/*")
public class ProductController {

	//CKeditor파일 업로드 경로
	@Value("${file.ckdir}") // 환경설정에 있던 임의로 지정한 파일업로드 경로 이름, 이 이름을 통해 C:\\Dev\\upload\\ckeditor\\경로가 들어오게 되는 것.
	private String uploadCKpath;
	
	//상품이미지 업로드 경로
	@Value("${file.product.image.dir}")
	private String uploadPath;	
	
	private final ProductService productService;
	
	
	@GetMapping("/pro_list")			//@ModelAttribute String cat_name : 타임리프 페이지에서 이 이름을 사용하겠다.
	public void pro_list(@ModelAttribute("cat_code") int cat_code, @ModelAttribute("cat_name") String cat_name, Criteria cri, Model model) throws Exception {
		
		cri.setAmount(9); // 출력 목록 갯수
		
		log.info("2차 카테고리코드: " + cat_code);
		log.info("2차 카테고리이름: " + cat_name);
		
		List<ProductVo> pro_list = productService.pro_list(cat_code, cri);
		
		//클라이언트에 \를 /로 변환하여, model작업 전에 처리함.  2024\07\01   --> 2024/07/02로 바꿔줄거다.
		pro_list.forEach(vo -> {
			vo.setPro_up_folder(vo.getPro_up_folder().replace("\\", "/"));
		});
		
		int totalCount = productService.getCountProductByCategory(cat_code);

		model.addAttribute("pro_list", pro_list);
		model.addAttribute("pageMaker", new PageDTO(cri, totalCount));
	}
	
	
	// [상품리스트에서 사용할 이미지 보여주기]  1) <img src="매핑주소">  2) <img src="test.gif">  //우린 1번 방법 사용, 2번방법은 고정적인거, 1번은 스프링의 메서드를 이용해 가져옴.
	@GetMapping("/image_display")
	public ResponseEntity<byte[]> image_display(String dateFolderName, String fileName) throws Exception {
		
		return FileManagerUtils.getFile(uploadPath + dateFolderName, fileName);
	}
	
	
	// [상품정보]
	@GetMapping("pro_info") 
	public ResponseEntity<ProductVo> pro_info(int pro_num) throws Exception {
		ResponseEntity<ProductVo> entity = null;
		
		log.info("상품코드" + pro_num);
		
		//DB연동 작업  //ajax로 넘어가는건 model작업을 하지 않는다.  // jsp나 타임리프에서 보여주는건 model작업을 한다.
		ProductVo vo = productService.pro_info(pro_num);
		vo.setPro_up_folder(vo.getPro_up_folder().replace("\\", "/"));
		
		entity = new ResponseEntity<ProductVo>(vo, HttpStatus.OK);
		//entity = new ResponseEntity<ProductVo>(productService.pro_info(pro_num), HttpStatus.OK);
			//ajax로 그냥 보낼때는 productService.pro_info(pro_num)가 엔티티 첫번째 자리에 들어가야 한다.
		
		
		
		
		return entity;
	}
	
	
	
}
