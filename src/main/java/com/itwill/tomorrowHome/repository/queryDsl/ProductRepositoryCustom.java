package com.itwill.tomorrowHome.repository.queryDsl;

import java.util.List;
import java.util.Map;

import com.itwill.tomorrowHome.domain.Product;

public interface ProductRepositoryCustom {
	/**
	 * 조건별 상품 전체 개수 조회
	 */
	int countProductList(Map<String, String> params);
	
	/**
	 * 상품 리스트 조회
	 */
	List<Product> findProductList(Map<String, String> params);

}
