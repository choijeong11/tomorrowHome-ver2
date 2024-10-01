package com.itwill.tomorrowHome.controller;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.itwill.tomorrowHome.domain.Qna;
import com.itwill.tomorrowHome.dto.QnaDto;
import com.itwill.tomorrowHome.service.QnaService;
import com.itwill.tomorrowHome.util.PageMakerDto;

import jakarta.servlet.http.HttpSession;

@Controller
public class QnaController {
	@Autowired
	private QnaService qnaService;

	/**
	 * 게시글 리스트 반환
	 */
	@RequestMapping("/qna_list")
	public String qna_list(@RequestParam(required = false, defaultValue = "1") Integer pageno, String search_type,
			String search_value, Model model, HttpSession session) throws Exception {
		PageMakerDto<QnaDto> qnaList = qnaService.selectQnaList(pageno, search_type, search_value);
		model.addAttribute("qnaList", qnaList);
		model.addAttribute("pageno", pageno);
		model.addAttribute("search_type", search_type);
		model.addAttribute("search_value", search_value);
		model.addAttribute("toDay", new Date());
			
		return "tables";
	}

	/**
	 * 게시글 상세
	 */
	@RequestMapping("/qna_view")
	public String qna_detail(@RequestParam Map<String, String> params, Model model, HttpSession session) throws Exception {
		String pageno = params.get("pageno");
		String q_no = params.get("q_no");
		if (pageno == null || q_no == null) {
			return "redirect:index";
		}
		
		Qna qna = qnaService.selectQna(Integer.parseInt(q_no));
		qnaService.updateReadCount(Integer.parseInt(q_no));
		model.addAttribute("qna", qna);
		model.addAttribute("pageno", pageno);
		model.addAttribute("search_type", params.get("search_type"));
		model.addAttribute("search_value", params.get("search_value"));
			
		return "tables-detail";
	}

	/**
	 * 게시글 수정폼
	 */
	@RequestMapping("/qna_update_form")
	public String qna_update_form(@RequestParam Map<String, String> params, Model model, HttpSession session) throws Exception {
		String pageno = params.get("pageno");
		String q_no = params.get("q_no");
		if (pageno == null || q_no == null) {
			return "redirect:index";
		}
		
		Qna qna = qnaService.selectQna(Integer.parseInt(q_no));
		model.addAttribute("qna", qna);
		model.addAttribute("pageno", pageno);
		model.addAttribute("search_type", params.get("search_type"));
		model.addAttribute("search_value", params.get("search_value"));
			
		return "tables-update";
	}

	/**
	 * 게시글 입력폼
	 */
	@RequestMapping("/qna_write_form")
	public String qna_write_form(Integer pageno, String search_type, String search_value, Model model,
			HttpSession session) throws Exception {
		if (pageno == null) {
			return "redirect:index";
		}
		
		model.addAttribute("pageno", pageno);
		model.addAttribute("search_type", search_type);
		model.addAttribute("search_value", search_value);
		model.addAttribute("toDay", new Date());
		
		return "tables-write";
	}

	/**
	 * 답글 입력폼
	 */
	@RequestMapping("/qna_reply_form")
	public String qna_reply_form(@RequestParam Map<String, String> params, Model model, HttpSession session) throws Exception {
		String pageno = params.get("pageno");
		String q_no = params.get("q_no");
		if (pageno == null || q_no == null) {
			return "redirect:index";
		}
		
		Qna qna = qnaService.selectQna(Integer.parseInt(q_no));
		model.addAttribute("qna", qna);
		model.addAttribute("q_no", q_no);
		model.addAttribute("pageno", pageno);
		model.addAttribute("search_type", params.get("search_type"));
		model.addAttribute("search_value", params.get("search_value"));
		model.addAttribute("toDay", new Date());
			
		return "tables-reply-write";
	}

}
