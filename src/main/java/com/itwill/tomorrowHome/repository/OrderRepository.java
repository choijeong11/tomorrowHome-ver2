package com.itwill.tomorrowHome.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itwill.tomorrowHome.domain.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {
	/**
	 * 사용자 주문 목록 전체 조회
	 */
	@Query("SELECT o FROM Order o WHERE o.m_id = :m_id ORDER BY o.o_no DESC")
	List<Order> findOrderListByMId(@Param("m_id") String m_id);
	
	/**
	 * 주문 전체 삭제
	 */
	@Modifying
	@Query("DELETE FROM Order o WHERE o.m_id = :m_id")
	void deleteOrdersByMId(@Param("m_id") String m_id);
	
	/**
	 * 회원의 상품 주문여부 
	 */
    @Query("SELECT COUNT(o) " +
            "FROM Order o " +
            "JOIN o.orderItemList oi " +
            "WHERE o.m_id = :m_id AND oi.product.p_no = :p_no")
     int countOrdersByMIdAndPNo(@Param("m_id") String m_id, @Param("p_no") int p_no);
}
