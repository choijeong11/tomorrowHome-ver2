package com.itwill.tomorrowHome.repository.queryDsl;

import java.util.List;

import com.itwill.tomorrowHome.dto.CartDto;

public interface CartRepositoryCustom {
	/**
	 * 사용자 카트 전체 조회
	 */
	List<CartDto> findCartListAll(String m_id);

}
