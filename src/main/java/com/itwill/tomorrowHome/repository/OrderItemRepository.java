package com.itwill.tomorrowHome.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.tomorrowHome.domain.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
	
}
