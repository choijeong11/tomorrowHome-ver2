package com.itwill.tomorrowHome.repository.queryDsl;

import java.util.List;

import com.itwill.tomorrowHome.dto.WishlistDto;

public interface WishlistRepositoryCustom {
	/**
	 * 사용자 위시리스트 조회
	 */
	List<WishlistDto> findWishlist(String m_id);
}
