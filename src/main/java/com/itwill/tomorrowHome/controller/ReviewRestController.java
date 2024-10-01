package com.itwill.tomorrowHome.controller;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itwill.tomorrowHome.domain.Product;
import com.itwill.tomorrowHome.domain.Review;
import com.itwill.tomorrowHome.dto.ReviewDto;
import com.itwill.tomorrowHome.service.ProductService;
import com.itwill.tomorrowHome.service.ReviewService;

@RestController
public class ReviewRestController {
	@Autowired
    private MessageSource messageSource;
	@Autowired
	private ReviewService reviewService;
	@Autowired
	private ProductService productService;
	
	/**
	 * 리뷰 등록 
	 */
	@PostMapping("/insert_review")
	public Map<String, Object> insert_review(@RequestBody ReviewDto reviewDto) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		/*
		 *  1 : 성공 
		 * -1 : 상품 주문 X 
		 * -2 : 모든 리뷰 이미 작성 
		 */
		Map<String, Object> insertResultMap = reviewService.insertProductReview(reviewDto);
		int result = (Integer) insertResultMap.get("resultCode");
		if (result == 1) {
			resultMap.put("errorCode", 1);
			resultMap.put("errorMsg", messageSource.getMessage("common.insert", null, Locale.KOREA));
			resultMap.put("data", new ReviewDto((Review)insertResultMap.get("insertReview")));
		} else if (result == -1) {
			resultMap.put("errorCode", -1);
			resultMap.put("errorMsg", messageSource.getMessage("review.invalid", null, Locale.KOREA));
		} else {
			resultMap.put("errorCode", -2);
			resultMap.put("errorMsg", messageSource.getMessage("review.duplicate", null, Locale.KOREA));
		}
			
		return resultMap;
	}

	/**
	 * 리뷰 삭제
	 */
	@PostMapping("/delete_review")
	public Map<String, Object> review_delete(@RequestParam Integer r_no) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		if (r_no == null) {
			throw new Exception(messageSource.getMessage("common.error", null, Locale.KOREA));
		}
		
		reviewService.deleteReview(r_no);
		resultMap.put("errorCode", 1);
		resultMap.put("errorMsg", messageSource.getMessage("common.delete", null, Locale.KOREA));
		
		return resultMap;
	}

	/**
	 * 리뷰 수정폼
	 */
	@PostMapping("/update_review_form")
	public Map<String, Object> update_review_form(@RequestParam Integer r_no) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		Review review = reviewService.selectProductReview(r_no);
		resultMap.put("errorCode", 1);
		resultMap.put("errorMsg", messageSource.getMessage("common.success", null, Locale.KOREA));
		resultMap.put("data", new ReviewDto(review));
		
		return resultMap;
	}

	/**
	 * 리뷰 수정
	 */
	@PostMapping("/update_review")
	public Map<String, Object> update_review(@RequestBody ReviewDto reviewDto) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		Review updateReview = reviewService.updateReview(reviewDto);
		resultMap.put("errorCode", 1);
		resultMap.put("errorMsg", messageSource.getMessage("common.update", null, Locale.KOREA));
		resultMap.put("data", new ReviewDto(updateReview));
		
		return resultMap;
	}

	/**
	 * 상품의 리뷰 정보를 반환
	 */
	@PostMapping("/update_review_info")
	public Map<String, Object> update_review_info(@RequestParam Integer p_no) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		Map<String, Object> dataMap = new HashMap<>();
		if (p_no == null) {
			throw new Exception(messageSource.getMessage("common.error", null, Locale.KOREA));
		}
		
		Product product = productService.selectProductDetail(p_no);
		dataMap.put("count", product.getReviewList().size());
		dataMap.put("avg_score", product.getP_avg_score());
		resultMap.put("errorCode", 1);
		resultMap.put("errorMsg", messageSource.getMessage("common.success", null, Locale.KOREA));
		resultMap.put("data", dataMap);
			
		return resultMap;
	}

}
