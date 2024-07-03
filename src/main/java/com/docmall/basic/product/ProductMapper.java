package com.docmall.basic.product;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.docmall.basic.admin.product.ProductVo;
import com.docmall.basic.common.dto.Criteria;

public interface ProductMapper {

	// [상품 리스트]
	List<ProductVo> pro_list(@Param("cat_code") int cat_code, @Param("cri") Criteria cri);
	
	// [카테고리마다 보이는 상품 갯수]
	int getCountProductByCategory(int cat_code);
	//페이징 할땐 언제나 데이터 갯수가 따라다녀야 한다.
}
