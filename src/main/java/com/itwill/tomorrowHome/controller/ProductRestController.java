package com.itwill.tomorrowHome.controller;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itwill.tomorrowHome.domain.Product;
import com.itwill.tomorrowHome.service.ProductService;
import com.itwill.tomorrowHome.util.PageMakerDto;

@RestController
public class ProductRestController {
	@Autowired
    private MessageSource messageSource;
	@Autowired
	private ProductService productService;

	/**
	 * 상품 리스트 페이지
	 */
	@RequestMapping("/product_list_rest")
	public Map<String, Object> product_list_rest(@RequestParam Map<String, String> params) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();	
		PageMakerDto<Product> productList = null;
		System.out.println(params);
		/*
		("cg_no");
		("lowPrice");
		("highPrice");
		("color");
		("brand");
		("score");
		("sortBy");
		("search");
		(pageno)
		*/
		productList = productService.selectProductList(params); 
		resultMap.put("errorCode", 1);
		resultMap.put("errorMsg", messageSource.getMessage("common.success", null, Locale.KOREA));
		resultMap.put("data", productList);
		
		return resultMap;
	}
	
	/**
	 * 상품 상세
	 */
	@RequestMapping(value="/product_detail_rest", produces = "application/json;charset=utf-8")
	public Map<String, Object> product_detail(@RequestParam Integer p_no) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		if(p_no == null) {
			throw new Exception(messageSource.getMessage("common.error", null, Locale.KOREA));
		}
		
		Product product = productService.selectProductDetail(p_no);
		resultMap.put("errorCode", "1");
		resultMap.put("errorMsg", messageSource.getMessage("common.success", null, Locale.KOREA));
		resultMap.put("data", product);
			
		return resultMap;
	}
	
}
