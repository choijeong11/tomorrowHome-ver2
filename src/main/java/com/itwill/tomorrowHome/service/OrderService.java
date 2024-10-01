package com.itwill.tomorrowHome.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.tomorrowHome.domain.Cart;
import com.itwill.tomorrowHome.domain.Order;
import com.itwill.tomorrowHome.domain.OrderItem;
import com.itwill.tomorrowHome.domain.Product;
import com.itwill.tomorrowHome.repository.OrderItemRepository;
import com.itwill.tomorrowHome.repository.OrderRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class OrderService {
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private OrderItemRepository orderItemRepository;
	@Autowired
	private CartService cartService;
	@Autowired
	private ProductService productService;

	/**
	 * 주문1개 삭제
	 */
	@Transactional
	public void deleteByOrderNo(int o_no) throws Exception {
		orderRepository.deleteById(o_no);
	}

	/**
	 * 주문 전체 삭제
	 */
	@Transactional
	public void delete(String m_id) throws Exception {
		orderRepository.deleteOrdersByMId(m_id);
	}

	/**
	 * 주문목록
	 */
	@Transactional(readOnly = true)
	public List<Order> list(String m_id) throws Exception {
		return orderRepository.findOrderListByMId(m_id); 
	}

	/**
	 * 주문 상세보기
	 */
	@Transactional(readOnly = true)
	public Order detail(int o_no) throws Exception {
		Optional<Order> optionalOrder = orderRepository.findById(o_no);
		Order order = optionalOrder.orElseThrow(() -> new EntityNotFoundException());
		return order;
	}

	/**
	 * 상품에서 직접주문
	 */
	@Transactional
	public void create(int p_no, int oi_qty, Order order) throws Exception {
		Product product = productService.selectProductDetail(p_no);
		ArrayList<OrderItem> orderItemList = new ArrayList<OrderItem>();
		orderItemList.add(OrderItem.builder()
						.oi_qty(oi_qty)
						.product(product)
						.build());
		// 주문이름 생성
		String order_desc = orderItemList.get(0).getProduct().getP_name();
		Order createOrder = Order.builder()
				.o_no(order.getO_no())
				.o_date(new Date())
				.o_desc(order_desc)
				.o_price(order.getO_price())
				.o_pay_method(order.getO_pay_method())
				.o_status(order.getO_status())
				.o_rv_name(order.getO_rv_name())
				.o_rv_phone(order.getO_rv_phone())
				.o_rv_address(order.getO_rv_address())
				.o_rv_post(order.getO_rv_post())
				.o_message(order.getO_message())
				.m_id(order.getM_id())
				.orderItemList(orderItemList)
				.build();
		// Order 저장
        order = orderRepository.save(createOrder);
        // OrderItem 저장
        for(OrderItem orderItem : orderItemList) { 
			orderItem.setOrder(order);
		}
        orderItemRepository.saveAll(orderItemList);
	}

	/**
	 * 카트 전체 주문
	 */
	@Transactional
	public void create(Order order) throws Exception {
		Map<String, Object> cartResultMap = cartService.cartListAll(order.getM_id());
		@SuppressWarnings("unchecked")
		List<Cart> cartList = (List<Cart>) cartResultMap.get("cartList");
		ArrayList<OrderItem> orderItemList = new ArrayList<OrderItem>();
		for (Cart cart : cartList) {
			orderItemList.add(OrderItem.builder()
							.oi_qty(cart.getC_qty())
							.product(cart.getProduct())
							.build());
		}
		// 주문이름 생성
		String order_desc = orderItemList.get(0).getProduct().getP_name();
		int size = orderItemList.size();
		if (size > 1) {
			order_desc += " 외 " + (size - 1) + "건";
		}
		Order createOrder = Order.builder()
						.o_no(order.getO_no())
						.o_date(new Date())
						.o_desc(order_desc)
						.o_price(order.getO_price())
						.o_pay_method(order.getO_pay_method())
						.o_status(order.getO_status())
						.o_rv_name(order.getO_rv_name())
						.o_rv_phone(order.getO_rv_phone())
						.o_rv_address(order.getO_rv_address())
						.o_rv_post(order.getO_rv_post())
						.o_message(order.getO_message())
						.m_id(order.getM_id())
						.orderItemList(orderItemList)
						.build();
		// Order 저장
        order = orderRepository.save(createOrder);
        // OrderItem 저장
        for(OrderItem orderItem : orderItemList) { 
			orderItem.setOrder(order);
		}
        orderItemRepository.saveAll(orderItemList);
        // 카트 비우기
		cartService.removeCartAll(order.getM_id());
	}

	/**
	 * cart에서 선택주문
	 */
	@Transactional
	public void create(String[] cart_item_no_array, Order order) throws Exception {
		ArrayList<OrderItem> orderItemList = new ArrayList<OrderItem>();
		for (int i = 0; i < cart_item_no_array.length; i++) {
			int c_no = Integer.parseInt(cart_item_no_array[i]);
			Cart cart = cartService.getCartByNo(c_no);
			orderItemList.add(OrderItem.builder()
							.oi_qty(cart.getC_qty())
							.product(cart.getProduct())
							.build());
			cartService.removeCart(c_no);
		}
		// 주문이름 생성
		String order_desc = orderItemList.get(0).getProduct().getP_name();
		int size = orderItemList.size();
		if (size > 1) {
			order_desc += " 외 " + (size - 1) + "건";
		}
		Order createOrder = Order.builder()
						.o_no(order.getO_no())
						.o_date(new Date())
						.o_desc(order_desc)
						.o_price(order.getO_price())
						.o_pay_method(order.getO_pay_method())
						.o_status(order.getO_status())
						.o_rv_name(order.getO_rv_name())
						.o_rv_phone(order.getO_rv_phone())
						.o_rv_address(order.getO_rv_address())
						.o_rv_post(order.getO_rv_post())
						.o_message(order.getO_message())
						.m_id(order.getM_id())
						.orderItemList(orderItemList)
						.build();
		// Order 저장
        order = orderRepository.save(createOrder);
        // OrderItem 저장
        for(OrderItem orderItem : orderItemList) { 
			orderItem.setOrder(order);
		}
        orderItemRepository.saveAll(orderItemList);
	}

}
