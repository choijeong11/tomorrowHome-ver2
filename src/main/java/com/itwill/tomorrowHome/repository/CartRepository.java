package com.itwill.tomorrowHome.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itwill.tomorrowHome.domain.Cart;
import com.itwill.tomorrowHome.repository.queryDsl.CartRepositoryCustom;

public interface CartRepository extends JpaRepository<Cart, Integer>, CartRepositoryCustom {
	/**
	 * 카트 내역 조회
	 */
	@Query("SELECT c FROM Cart c WHERE c.m_id = :m_id AND c.product.p_no = :p_no")
    Optional<Cart> findCartByMIdAndPNo(@Param("m_id") String m_id, @Param("p_no") int p_no);
	
	/**
	 * 카트 비우기
	 */
	@Modifying
	@Query("DELETE FROM Cart c WHERE c.m_id = :m_id")
	void deleteCartByMId(@Param("m_id") String m_id);
}
