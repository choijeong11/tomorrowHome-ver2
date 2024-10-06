package com.itwill.tomorrowHome.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.itwill.tomorrowHome.domain.Cart;
import com.itwill.tomorrowHome.domain.Order;
import com.itwill.tomorrowHome.domain.Product;
import com.itwill.tomorrowHome.service.CartService;
import com.itwill.tomorrowHome.service.OrderService;
import com.itwill.tomorrowHome.service.ProductService;

import jakarta.servlet.http.HttpSession;

@Controller
public class OrderController {
	@Autowired
	private OrderService orderService; 
	@Autowired
	private ProductService productService; 
	@Autowired
	private CartService cartService;   
	
	/*
	 * <<< 주문 정보 session 저장 >>> 
	 * - 주문시 카트 번호를 전달하기 위한 array
	 * session.setAttribute("cart_item_noStr_array", cart_item_noStr_array); 
	 * - 주문form에 상품정보 출력을 위한 list 
	 * session.setAttribute("cartItemList", cartItemList);
	 * - 주문 정보를 저장하는 Order 객체 
	 * session.setAttribute("order", order);
	 */
	/**
	 * 주문 상품 정보 저장 - 주문자 정보로 이동
	 */
	@PostMapping("/checkout_2")
	public String order_orderer_form(HttpSession session, String buyType, String p_no, String qty,
			@RequestParam(value = "cart_no", required = false) String[] cart_item_noStr_array, Model model) throws Exception {

		String sM_id = (String) session.getAttribute("sM_id");
		// 주문할 상품리스트를 담을 cartItemList. 주문 form에 상품정보 출력을 위한 list다.
		List<Cart> cartItemList = new ArrayList<>();
		Map<String, Object> cartResultMap = cartService.cartListAll(sM_id);
		@SuppressWarnings("unchecked")
		List<Cart> cartList = (List<Cart>) cartResultMap.get("cartList");
		
		if (buyType.equals("cart_select")) {
			// 장바구니 선택 주문의 경우, 전달된 cart번호로 cart객체를 가져와 리스트에 세팅
			for (String cart_item_noStr : cart_item_noStr_array) {
				cartItemList.add(cartService.getCartByNo(Integer.parseInt(cart_item_noStr)));
			}
			// 주문시 카트 번호를 전달하기 위한 array
			session.setAttribute("cart_item_noStr_array", cart_item_noStr_array);
			
		} else if (buyType.equals("direct")) {
			// 바로 주문의 경우 상품번호로 상품객체를 가져와 상품객체와 수량을 카트객체에 세팅 후, 카트객체를 리스트에 세팅
			Product product = productService.selectProductDetail(Integer.parseInt(p_no));
			cartItemList.add(Cart.builder()
								.c_qty(Integer.parseInt(qty))
								.product(product)
								.m_id(sM_id)
								.build());
			session.setAttribute("p_no", p_no);
			session.setAttribute("qty", qty);
			
		} else if (buyType.equals("cart")) {
			cartItemList = cartList;
		}
		
		session.setAttribute("buyType", buyType);
		session.setAttribute("cartItemList", cartItemList);
			
		return "checkout-2";
	}

	/**
	 * 주문자 정보 저장 - 배송 정보 이동
	 */
	@PostMapping("/checkout_3")
	public String order_reciever_form(HttpSession session, @RequestParam Map<String, String> params, Model model) throws Exception {
		String sM_id = (String) session.getAttribute("sM_id");
		session.setAttribute("order", Order.builder().m_id(sM_id).build());
		
		return "checkout-3";
	}

	/**
	 * 배송 정보 저장 - 결제 정보로 이동
	 */
	@PostMapping("/checkout_4")
	public String order_payment_form(HttpSession session, @RequestParam Map<String, String> params, Model model) throws Exception {
		Order sessionOrder = (Order) session.getAttribute("order");
		Order order = Order.builder()
					.m_id(sessionOrder.getM_id())
					.o_rv_name(params.get("o_rv_name"))
					.o_rv_phone(params.get("o_rv_phone"))
					.o_rv_address(params.get("o_rv_address"))
					.o_rv_post(params.get("o_rv_post"))
					.o_message(params.get("o_message"))
					.build();
		session.setAttribute("order", order);
			
		return "checkout-4";
	}

	/**
	 * 결제 방법 정보 저장 - 주문 정보 확인창으로 이동
	 */
	@SuppressWarnings("unchecked")
	@PostMapping("/checkout_5")
	public String order_confirm_form(HttpSession session,
			@RequestParam String o_pay_method, Model model) throws Exception {
		Order sessionOrder = (Order) session.getAttribute("order");
		String status = "";
		switch (o_pay_method) {
		case "CARD":
		case "PayPal":
			status = "결제완료"; break;
		case "BankTransfer":
		case "CoD":
			status = "결제대기"; break;
		}
		Order order = Order.builder()
					.m_id(sessionOrder.getM_id())
					.o_rv_name(sessionOrder.getO_rv_name())
					.o_rv_phone(sessionOrder.getO_rv_phone())
					.o_rv_address(sessionOrder.getO_rv_address())
					.o_rv_post(sessionOrder.getO_rv_post())
					.o_message(sessionOrder.getO_message())
					.o_pay_method(o_pay_method)
					.o_status(status)
					.build();
		session.setAttribute("order", order);
		
		// 주문 상품
		List<Cart> cartItemList = (List<Cart>) session.getAttribute("cartItemList");
		model.addAttribute("cartItemList", cartItemList);
		// 주문 총합
		int orderTotalPrice = 0;
		for(Cart cart : cartItemList) {
			orderTotalPrice += cart.getC_qty() * cart.getProduct().getP_price();
		}
		model.addAttribute("orderTotalPrice", orderTotalPrice);
		model.addAttribute("shipping_price", orderTotalPrice < 50000 ? 2500 : 0);
		
		return "checkout-5";
	}

	/**
	 * 주문 생성
	 */
	@PostMapping("/create_order")
	public String create_order(HttpSession session, Model model, String total_price) throws Exception {
		Order sessionOrder = (Order) session.getAttribute("order");
		Order order = Order.builder()
				.m_id(sessionOrder.getM_id())
				.o_rv_name(sessionOrder.getO_rv_name())
				.o_rv_phone(sessionOrder.getO_rv_phone())
				.o_rv_address(sessionOrder.getO_rv_address())
				.o_rv_post(sessionOrder.getO_rv_post())
				.o_message(sessionOrder.getO_message())
				.o_pay_method(sessionOrder.getO_pay_method())
				.o_status(sessionOrder.getO_status())
				.o_price(Integer.parseInt(total_price))
				.build();
		String buyType = (String)session.getAttribute("buyType");
		if(buyType.equals("cart")) {
			orderService.create(order); 
		}else if(buyType.equals("cart_select")) {
			orderService.create((String[])session.getAttribute("cart_item_noStr_array"), order);
		}else if(buyType.equals("direct")) {
			int p_no = Integer.parseInt((String) session.getAttribute("p_no"));
			int oi_qty = Integer.parseInt((String) session.getAttribute("qty"));
			orderService.create(p_no, oi_qty, order); 
		}
		model.addAttribute("status", order.getO_status());
		
		session.removeAttribute("order");
		session.removeAttribute("p_no");
		session.removeAttribute("oi_qty");
		session.removeAttribute("buyType");
		session.removeAttribute("cartItemList");
		session.removeAttribute("cart_item_noStr_array");
			
		return "checkout-complate";
	}

	/**
	 * 주문리스트
	 */
	@RequestMapping("/order_list")
	public String order_list(HttpSession session, Model model) throws Exception {
		String sM_id = (String) session.getAttribute("sM_id");
		List<Order> orderList = orderService.list(sM_id);
		model.addAttribute("orderList", orderList); 
			
		return "order-list";
	}
	
	/*
	 * Get Mapping
	 */
	@GetMapping({ "/checkout_2", "/checkout_3", "/checkout_4", "/checkout_5", "/create_order" })
	public String get_mapping() throws Exception {
		return "redirect:index";
	}
}
