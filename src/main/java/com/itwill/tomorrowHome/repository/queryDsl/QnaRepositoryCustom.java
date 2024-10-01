package com.itwill.tomorrowHome.repository.queryDsl;

import java.util.List;
import java.util.Map;

import com.itwill.tomorrowHome.dto.QnaDto;

public interface QnaRepositoryCustom {
	/**
	 * 게시물 수 조회
	 */
	int countQnaList(String search_type, String search_value);
	
	/**
	 * 게시물 리스트 조회
	 */
	List<QnaDto> findQnaList(Map<String, Object> params);
}
