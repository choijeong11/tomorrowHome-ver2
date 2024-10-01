package com.itwill.tomorrowHome.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itwill.tomorrowHome.domain.Qna;
import com.itwill.tomorrowHome.dto.QnaDto;
import com.itwill.tomorrowHome.service.QnaService;
import com.itwill.tomorrowHome.util.PageMakerDto;

@RestController
public class QnaRestController {
	@Autowired
	private QnaService qnaService;

	/**
	 * 게시글 리스트 반환 (REST)
	 */
	@RequestMapping("/qna_list_rest")
	public Map<String, Object> qna_list_rest(@RequestParam(required = false, defaultValue = "1") Integer pageno,
			String search_type, String search_value) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		PageMakerDto<QnaDto> qnaList = null;
		
		qnaList = qnaService.selectQnaList(pageno, search_type, search_value);
		resultMap.put("errorCode", 1);
		resultMap.put("errorMsg", "성공");
		resultMap.put("data", qnaList);
		
		return resultMap;
	}

	/**
	 * 게시글 삭제
	 */
	@RequestMapping("/qna_delete_rest")
	public Map<String, Object> qna_delete_rest(Integer pageno, Integer q_no) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		if (pageno == null || q_no == null) {
			resultMap.put("errorCode", -1);
			resultMap.put("errorMsg", "잘못된 접근입니다");
			return resultMap;
		}
		
		qnaService.deleteQna(q_no);
		resultMap.put("errorCode", 1);
		resultMap.put("errorMsg", "게시글을 삭제하였습니다");
		
		return resultMap;
	}
	
	/**
	 * 게시글 등록
	 */
	@PostMapping("/qna_new_write")
	public Map<String, Object> qna_new_write(@RequestBody Qna qna) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		qnaService.insertNewQna(qna);
		resultMap.put("errorCode", 1);
		resultMap.put("errorMsg", "게시글을 등록하였습니다");
		
		return resultMap;
	}
	
	/**
	 * 답글 등록
	 */
	@PostMapping("/qna_reply_write")
	public Map<String, Object> qna_reply_write(@RequestBody Map<String, String> params) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		String pageno = params.get("pageno");
		String q_no = params.get("q_no");
		//String e_search_value = "";
		if (pageno == null || q_no == null) {
			resultMap.put("errorCode", -1);
			resultMap.put("errorMsg", "잘못된 접근입니다");
			return resultMap;
		}
		
		qnaService.insertReply(Qna.builder()
						.q_no(Integer.parseInt(q_no))
						.q_title(params.get("q_title"))
						.q_content(params.get("q_content"))
						.m_id(params.get("m_id"))
						.build());
		//e_search_value = URLEncoder.encode(params.get("search_value"), "utf-8");
		resultMap.put("errorCode", 1);
		resultMap.put("errorMsg", "답글을 등록하였습니다");
		
		return resultMap;
	}
	
	/**
	 * 게시글 수정
	 */
	@PostMapping("/qna_update")
	public Map<String, Object> qna_update(@RequestBody Map<String, String> params) throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		String pageno = params.get("pageno");
		String q_no = params.get("q_no");
		//String e_search_value = "";
		if (pageno == null || q_no == null) {
			resultMap.put("errorCode", -1);
			resultMap.put("errorMsg", "잘못된 접근입니다");
			return resultMap;
		}
		
		qnaService.updateQna(Qna.builder()
						.q_no(Integer.parseInt(q_no))
						.q_title(params.get("q_title"))
						.q_content(params.get("q_content"))
						.build());
		//e_search_value = URLEncoder.encode(params.get("search_value"), "utf-8");
		resultMap.put("errorCode", 1);
		resultMap.put("errorMsg", "게시글을 수정하였습니다");
		
		return resultMap;
	}

}
