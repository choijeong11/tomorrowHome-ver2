package com.itwill.tomorrowHome.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.itwill.tomorrowHome.domain.Wishlist;
import com.itwill.tomorrowHome.service.WishlistService;

import jakarta.servlet.http.HttpSession;

@Controller
public class WishlistController {
	@Autowired
	private WishlistService wishlistService;

	/**
	 위시리스트 출력 
	 */
	@RequestMapping("/wishlist_view")
	public String wishlist_view(HttpSession session, Model model) throws Exception {
		String sM_id = (String) session.getAttribute("sM_id");
		List<Wishlist> wishListList = wishlistService.wishlistAll(sM_id);
		model.addAttribute("wishListList", wishListList);
			
		return "wishlist";
	}

}
