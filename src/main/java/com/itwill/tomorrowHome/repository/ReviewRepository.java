package com.itwill.tomorrowHome.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itwill.tomorrowHome.domain.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
	/**
	 * 회원의 리뷰 작성 여부 조회
	 */
    @Query("SELECT COUNT(r) " +
            "FROM Review r " +
            "WHERE r.m_id = :m_id AND r.product.p_no = :p_no")
     int countReviewsByMIdAndPNo(@Param("m_id") String m_id, @Param("p_no") int p_no);
    
    /**
     * 상품 리뷰 평점 조회
     */
    @Query("SELECT ROUND(AVG(r.r_score), 1) FROM Review r WHERE r.product.p_no = :p_no")
    Double findAverageScoreByProductId(@Param("p_no") int p_no);
}
