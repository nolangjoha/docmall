package com.docmall.basic.product;

import java.util.List;

import org.springframework.stereotype.Service;

import com.docmall.basic.admin.product.ProductVo;
import com.docmall.basic.common.dto.Criteria;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

	private final ProductMapper productMapper;
	
	
	public List<ProductVo> pro_list(int cat_code, Criteria cri) {
		return productMapper.pro_list(cat_code, cri);
	}
	
	//페이징 할땐 언제나 데이터 갯수가 따라다녀야 한다.
	public int getCountProductByCategory(int cat_code) {
		return productMapper.getCountProductByCategory(cat_code);
	}
	
}
