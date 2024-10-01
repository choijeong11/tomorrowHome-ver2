package com.itwill.tomorrowHome.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itwill.tomorrowHome.domain.Product;
import com.itwill.tomorrowHome.repository.queryDsl.ProductRepositoryCustom;

public interface ProductRepository extends JpaRepository<Product, Integer>, ProductRepositoryCustom {
	/**
	멤버가 가장 선호하는 상품 컨셉 조회
	*/
    @Query(value = "SELECT p_concept FROM ( " +
                   "SELECT p.p_concept FROM orders o " +
                   "JOIN order_item oi ON o.o_no = oi.o_no " +
                   "JOIN product p ON oi.p_no = p.p_no " +
                   "WHERE o.m_id = :m_id " +
                   "UNION ALL " +
                   "SELECT p.p_concept FROM cart c " +
                   "JOIN product p ON c.p_no = p.p_no " +
                   "WHERE c.m_id = :m_id " +
                   "UNION ALL " +
                   "SELECT p.p_concept FROM wishlist w " +
                   "JOIN product p ON w.p_no = p.p_no " +
                   "WHERE w.m_id = :m_id) AS p_union " +
                   "GROUP BY p_concept " +
                   "ORDER BY count(*) DESC", 
           nativeQuery = true)
    List<String> findMemberBestProductConcept(@Param("m_id") String m_id);
    
    /**
    메인 인기상품 혹은 추천상품 리스트 조회
    */
    @Query( "SELECT p FROM Product p " +
            "WHERE (:p_concept IS NULL OR p.p_concept = :p_concept) " +
            "ORDER BY p.p_count DESC, p.p_avg_score DESC")
	List<Product> findMainProductList(@Param("p_concept") String p_concept, Pageable pageable);

    /**
    상품 조회수 증가
    */
    @Modifying
    @Query("UPDATE Product p SET p.p_count = p.p_count + 1 WHERE p.p_no = :p_no")
    void incrementProductCount(@Param("p_no") int p_no);
    
    /**
     * 상품 평균 평점 수정
     */
    @Modifying
    @Query("UPDATE Product p SET p.p_avg_score = :avg_score WHERE p.p_no = :p_no")
    int updateAverageScore(@Param("avg_score") Double avg_score, @Param("p_no") int p_no);
}
