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

import com.itwill.tomorrowHome.service.WishlistService;

import jakarta.servlet.http.HttpSession;

@RestController
public class WishlistRestController {
	@Autowired
    private MessageSource messageSource;
	@Autowired
	private WishlistService wishlistService;

	/**
	 * 위시리스트 추가
	 */
	@RequestMapping("/add_wishList_rest")
	public Map<String, Object> add_wishList_rest(HttpSession session, @RequestParam Integer p_no) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		if (p_no == null) {
			throw new Exception(messageSource.getMessage("common.error", null, Locale.KOREA));
		}
		
		String sM_id = (String) session.getAttribute("sM_id");
		int result = wishlistService.insertWish(sM_id, p_no);
		if (result != 0) {
			resultMap.put("errorCode", 1);
			resultMap.put("errorMsg", messageSource.getMessage("wishlist.insert", null, Locale.KOREA));
			resultMap.put("data", wishlistService.wishlistAll(sM_id));
		} else {
			resultMap.put("errorCode", -2);
			resultMap.put("errorMsg", messageSource.getMessage("wishlist.duplicate", null, Locale.KOREA));
		}
		
		return resultMap;
	}
	
	/**
	 * 위시리스트 삭제
	 */
	@RequestMapping("/delete_wishList_rest")
	public Map<String, Object> delete_wishList_rest(@RequestParam List<String> param) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		if(param == null) {
			throw new Exception(messageSource.getMessage("common.error", null, Locale.KOREA));
		}
		
		for(String w_no : param) {
			wishlistService.removeWishByNo(Integer.parseInt(w_no));
		}
		resultMap.put("errorCode", 1);
		resultMap.put("errorMsg", messageSource.getMessage("common.delete", null, Locale.KOREA));
		
		return resultMap;
	}

}
