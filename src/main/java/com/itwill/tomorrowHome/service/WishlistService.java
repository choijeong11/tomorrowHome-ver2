package com.itwill.tomorrowHome.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.tomorrowHome.domain.Product;
import com.itwill.tomorrowHome.domain.Wishlist;
import com.itwill.tomorrowHome.dto.WishlistDto;
import com.itwill.tomorrowHome.repository.WishlistRepository;
import com.itwill.tomorrowHome.repository.queryDsl.WishlistRepositoryCustom;

@Service
public class WishlistService {
	@Autowired
	private WishlistRepository wishlistRepository;
	@Autowired
	@Qualifier("wishlistRepositoryCustom")
	private WishlistRepositoryCustom wishlistRepositoryCustom;
	
	/**
	 * 위시리스트 등록 
	 */
	@Transactional
	public int insertWish(String m_id, int p_no) throws Exception {
		int result = 0;
		Optional<Wishlist> optionalWishlist = wishlistRepository.findWishlistByMIdAndPNo(m_id, p_no);
		if(optionalWishlist.isEmpty()) {
			Wishlist wishList = Wishlist.builder()
					.m_id(m_id)
					.product(Product.builder().p_no(p_no).build())
					.build();
			wishlistRepository.save(wishList);
			result = 1;
		}
		
		return result;
	}

	/**
	 * 위시리스트 상품 하나 삭제 
	 */
	@Transactional
	public void removeWishByNo(int w_no) throws Exception {
		wishlistRepository.deleteById(w_no);
	}

	/**
	 * 위시리스트 전체 조회 
	 */
	@Transactional
	public List<Wishlist> wishlistAll(String m_id) throws Exception {
		List<WishlistDto> wishlistDtoList = wishlistRepositoryCustom.findWishlist(m_id);
		List<Wishlist> wishListList = new ArrayList<Wishlist>();
		for(WishlistDto wishlistDto : wishlistDtoList) {
			wishListList.add(Wishlist.fromDto(wishlistDto));
		}
		return wishListList;
	}

}
