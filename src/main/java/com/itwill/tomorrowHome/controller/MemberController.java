package com.itwill.tomorrowHome.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class MemberController {
	/**
	 * 로그인폼 
	 */
	@RequestMapping("/login_form")
	public String login_form(HttpServletRequest request, Model model) throws Exception {
		model.addAttribute("loginMsg", request.getAttribute("loginMsg"));
		return "login";
	}

	/**
	 * 마이페이지
	 */
	@RequestMapping("/my_account")
	public String my_account(HttpSession session, Model model) throws Exception {
		return "my-account";
	}

	/**
	 * 회원 정보 보기
	 */
	@RequestMapping("/account_details")
	public String account_details(HttpSession session, Model model) throws Exception {
		return "account-details";
	}
	
	/*
	 * 로그아웃
	 */
	@RequestMapping("/member_logout")
	public String member_login_rest(HttpSession session) throws Exception {
		session.invalidate();
		return "redirect:index";
	}

}
