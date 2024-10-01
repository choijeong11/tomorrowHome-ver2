package com.itwill.tomorrowHome.controller;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itwill.tomorrowHome.domain.Order;
import com.itwill.tomorrowHome.service.OrderService;

import jakarta.servlet.http.HttpSession;

@RestController
public class OrderRestController {
	@Autowired
    private MessageSource messageSource;
	@Autowired
	private OrderService orderService;

	/**
	 * 주문상세 
	 */
	@RequestMapping(value = "order_detail_rest", produces = "application/json;charset=UTF-8")
	public Map<String, Object> order_detail_rest(@RequestParam Integer o_no) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		if (o_no == null) {
			throw new Exception(messageSource.getMessage("common.error", null, Locale.KOREA));
		}
		
		Order order = orderService.detail(o_no);
		resultMap.put("errorCode", 1);
		resultMap.put("errorMsg", messageSource.getMessage("common.success", null, Locale.KOREA));
		resultMap.put("data", order);
			
		return resultMap;
	}

	/**
	 * 주문 1개 삭제
	 */
	@PostMapping("order_item_delete_action_rest")
	public Map<String, Object> order_item_delete_action_rest(@RequestParam Integer o_no) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		if (o_no == null) {
			throw new Exception(messageSource.getMessage("common.error", null, Locale.KOREA));
		}
		
		orderService.deleteByOrderNo(o_no);
		resultMap.put("errorCode", 1);
		resultMap.put("errorMsg", messageSource.getMessage("common.delete", null, Locale.KOREA));
			
		return resultMap;
	}

	/**
	 * 주문전체삭제
	 */
	@PostMapping("order_all_delete_action_rest")
	public Map<String, Object> order_all_delete_action_rest(HttpSession session) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		String sM_id = (String) session.getAttribute("sM_id");
		orderService.delete(sM_id);
		resultMap.put("errorCode", 1);
		resultMap.put("errorMsg", messageSource.getMessage("common.delete", null, Locale.KOREA));
		
		return resultMap;
	}
}
