package com.itwill.tomorrowHome.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class CartController {
	/**
	 * 카트 리스트
	 */
	@RequestMapping("/cart_view")
	public String cart_view(HttpServletRequest request, HttpSession session, Model model) {
		return "cart";
	}
	
}
