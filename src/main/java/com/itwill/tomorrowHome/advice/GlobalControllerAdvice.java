package com.itwill.tomorrowHome.advice;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.itwill.tomorrowHome.domain.Member;
import com.itwill.tomorrowHome.service.CartService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class GlobalControllerAdvice {
	@Autowired
	private CartService cartService;

    @ModelAttribute
    public void addAttributes(HttpServletRequest request, HttpSession session, Model model) throws Exception {
		String requestURI = request.getRequestURI();
        if ("/get_now_time".equals(requestURI)) { 
        	// 해당 URI 제외
            return; 
        }
        
        // 세션 로그인 상태 값
        String sM_id = (String) session.getAttribute("sM_id");
        Member sMember = (Member) session.getAttribute("sMember");
        model.addAttribute("sM_id", sM_id);
        model.addAttribute("sMember", sMember);
        
        // 공통 헤더 카트리스트
        if(sM_id != null && !sM_id.equals("")) {
        	Map<String, Object> cartResultMap = cartService.cartListAll(sM_id);
        	model.addAttribute("cartList", cartResultMap.get("cartList"));
        	model.addAttribute("totalPrice", cartResultMap.get("totalPrice"));
        }
    }
    
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        model.addAttribute("errorMsg", e.getMessage());
        return "error";
    }
}