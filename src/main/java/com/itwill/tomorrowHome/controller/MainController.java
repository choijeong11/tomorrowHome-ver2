package com.itwill.tomorrowHome.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.itwill.tomorrowHome.domain.Product;
import com.itwill.tomorrowHome.service.ProductService;

import jakarta.servlet.http.HttpSession;

@Controller
public class MainController {
	@Autowired
	private ProductService productService;

	@RequestMapping("/")
    public String home() {
        return "redirect:index"; 
    }
	
	/**
	 * 메인페이지
	 */
	@RequestMapping("/index")
	public String index(HttpSession session, Model model) throws Exception {
		String sM_id = (String) session.getAttribute("sM_id");
		Map<String, List<Product>> mainProductList = productService.selectMainProductList(sM_id);
		model.addAttribute("popularList", mainProductList.get("popularList"));
		model.addAttribute("suggestionList", mainProductList.get("suggestionList"));
			
		return "index";
	}

	/*
	 * FAQ 페이지
	 */
	@RequestMapping("/faq")
	public String faq_page(HttpSession session, Model model) throws Exception {
		return "faq";
	}
	
	/**
	 * session여부 체크
	 */
	@ResponseBody
	@RequestMapping("/check_session")
	public String check_session(HttpSession session) throws Exception {
		String result = "N"; 
		if(session.getAttribute("sM_id") != null) {
			result = "Y";
		}
		return result;
	}
	
	/**
	 * 현재 날짜,시간정보 반환 
	 */
	@ResponseBody
	@RequestMapping("/get_now_time")
	public String get_now_time() throws Exception {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
		simpleDateFormat.setTimeZone(timeZone);
		return simpleDateFormat.format(new Date());
	}
	
}
