package com.itwill.tomorrowHome.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itwill.tomorrowHome.domain.Cart;
import com.itwill.tomorrowHome.service.CartService;

import jakarta.servlet.http.HttpSession;

@RestController
public class CartRestController {
	@Autowired
    private MessageSource messageSource;
	@Autowired
	private CartService cartService;
	
	/**
	 * 카트 아이템 삭제
	 */
	@RequestMapping(value = "/cart_remove_rest", produces = "application/json;charset=UTF-8")
	public Map<String, Object> cart_remove_rest(@RequestParam List<String> param, HttpSession session) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		if(param == null) {
			throw new Exception(messageSource.getMessage("common.error", null, Locale.KOREA));
		}
		
		for(String c_no : param) {
			cartService.removeCart(Integer.parseInt(c_no));
		}
		String sM_id = (String)session.getAttribute("sM_id");
		resultMap.put("errorCode", 1);
		resultMap.put("errorMsg", messageSource.getMessage("common.delete", null, Locale.KOREA));
		resultMap.put("data", cartService.cartListAll(sM_id).get("cartList"));
		
		return resultMap;
	}
	
	/**
	 * 카트 수량 수정
	 */
	@RequestMapping(value = "cart_update_action_rest",produces = "application/json;charset=UTF-8")
	public Map<String, Object> cart_update_action_rest(@RequestParam Integer c_no,@RequestParam Integer c_qty,HttpSession session) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		Map<String, Object> dataMap = new HashMap<>();
		if(c_no == null || c_qty == null) {
			throw new Exception(messageSource.getMessage("common.error", null, Locale.KOREA));
		}
		
		Cart updateCart = cartService.updateQty(c_no, c_qty);
		String sM_id = (String)session.getAttribute("sM_id");
		resultMap.put("errorCode", 1);
		resultMap.put("errorMsg", messageSource.getMessage("cart.qty.update", null, Locale.KOREA));
		dataMap.put("cartList", cartService.cartListAll(sM_id).get("cartList"));
		dataMap.put("cart", updateCart);
		resultMap.put("data", dataMap);
			
		return resultMap;
	}
	
	/**
	 * 카트 추가 
	 */
	@RequestMapping("/add_cart_rest")
	public Map<String,Object> add_cart_rest(HttpSession session, @RequestParam("p_no") String[] p_no_arr, @RequestParam Integer qty) throws Exception {
		Map<String,Object> resultMap = new HashMap<>();
		if(p_no_arr == null || qty == null) {
			throw new Exception(messageSource.getMessage("common.error", null, Locale.KOREA));
		}
		
		String sM_id = (String)session.getAttribute("sM_id");
		for(String p_no : p_no_arr) {
			cartService.addInsert(qty, Integer.parseInt(p_no), sM_id);
		}
		resultMap.put("errorCode", 1);
		resultMap.put("errorMsg", messageSource.getMessage("cart.insert", null, Locale.KOREA));
		resultMap.put("data", cartService.cartListAll(sM_id).get("cartList"));
			
		return resultMap;
	}
	
}
