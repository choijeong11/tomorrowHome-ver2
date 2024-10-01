package com.itwill.tomorrowHome.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.tomorrowHome.domain.Product;
import com.itwill.tomorrowHome.domain.Review;
import com.itwill.tomorrowHome.dto.ReviewDto;
import com.itwill.tomorrowHome.repository.OrderRepository;
import com.itwill.tomorrowHome.repository.ProductRepository;
import com.itwill.tomorrowHome.repository.ReviewRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ReviewService {
	@Autowired
	private ReviewRepository reviewRepository;
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private ProductRepository productRepository;

	/**
	 * 상품 리뷰 등록
	 */
	@Transactional
	public Map<String, Object> insertProductReview(ReviewDto reviewDto) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		reviewDto.setR_date(new Date());
		Review review = Review.fromDto(reviewDto);
		/*
		 * 1 : 성공 
		 * -1 : 상품 주문 X 
		 * -2 : 모든 리뷰 이미 작성
		 */
		// 회원의 상품 주문여부 확인
		int orderCount = orderRepository.countOrdersByMIdAndPNo(review.getM_id(), review.getProduct().getP_no());
		// 상품을 주문했다면
		if (orderCount > 0) {
			// 회원의 특정 상품 리뷰 작성 여부 조회
			int reviewCount = reviewRepository.countReviewsByMIdAndPNo(review.getM_id(), review.getProduct().getP_no());
			// 리뷰 작성이 가능하다면
			if (orderCount > reviewCount) {
				// 리뷰 등록
				Review insertReview = reviewRepository.save(review);
				updateProductReviewAvg(review.getProduct().getP_no());
				resultMap.put("insertReview", insertReview);
				resultMap.put("resultCode", 1);
			} else {
				resultMap.put("resultCode", -2);
			}
		} else {
			resultMap.put("resultCode", -1);
		}
		return resultMap;
	}

	/**
	 * 특정 리뷰 정보 조회
	 */
	@Transactional
	public Review selectProductReview(int r_no) throws Exception {
		Review review = reviewRepository.findById(r_no).orElseThrow(() -> new EntityNotFoundException());
		return review;
	}

	/**
	 * 리뷰 수정
	 */
	@Transactional
	public Review updateReview(ReviewDto reviewDto) throws Exception {
		Review beforeReview = selectProductReview(reviewDto.getR_no());
		reviewDto.setR_date(beforeReview.getR_date());
		Review updateReview = reviewRepository.save(Review.fromDto(reviewDto));
		updateProductReviewAvg(updateReview.getProduct().getP_no());
		return updateReview;
	}

	/**
	 * 리뷰 삭제
	 */
	@Transactional
	public void deleteReview(int r_no) throws Exception {
		Review review = selectProductReview(r_no);
		reviewRepository.deleteById(r_no);
		updateProductReviewAvg(review.getProduct().getP_no());
	}
	
	/**
	 * 상품의 리뷰평균 수정 
	 */
	public void updateProductReviewAvg(int p_no) throws Exception { 
		Double avg_score = 0.0;
		Product product = productRepository.findById(p_no).get();
		if(product.getReviewList().size() != 0) {
			avg_score = reviewRepository.findAverageScoreByProductId(p_no);
		}
		productRepository.updateAverageScore(avg_score, p_no);
	}
	
}
