package com.itwill.tomorrowHome.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itwill.tomorrowHome.domain.Wishlist;
import com.itwill.tomorrowHome.repository.queryDsl.WishlistRepositoryCustom;

public interface WishlistRepository extends JpaRepository<Wishlist, Integer>, WishlistRepositoryCustom {
	/**
	 * 위시리스트 내역 조회
	 */
	@Query("SELECT w FROM Wishlist w WHERE w.m_id = :m_id AND w.product.p_no = :p_no")
    Optional<Wishlist> findWishlistByMIdAndPNo(@Param("m_id") String m_id, @Param("p_no") int p_no);
}
