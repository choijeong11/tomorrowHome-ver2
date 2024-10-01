package com.itwill.tomorrowHome.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.tomorrowHome.domain.Cart;
import com.itwill.tomorrowHome.domain.Product;
import com.itwill.tomorrowHome.dto.CartDto;
import com.itwill.tomorrowHome.repository.CartRepository;
import com.itwill.tomorrowHome.repository.queryDsl.CartRepositoryCustom;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CartService {
	@Autowired
	private CartRepository cartRepository;
	@Autowired
	@Qualifier("cartRepositoryCustom")
	private CartRepositoryCustom CartRepositoryCustom;
	
	/**
	 * 카트 상품 등록 or 수량 추가 
	 */
	@Transactional
	public Cart addInsert(int c_qty,int p_no,String m_id) throws Exception {
		Optional<Cart> optionalCart = cartRepository.findCartByMIdAndPNo(m_id, p_no);
		Cart cart = null;
		if(optionalCart.isPresent()) {
			// 수정
			Cart existingCart = optionalCart.get();
			cart = Cart.builder()
				    .c_no(existingCart.getC_no())
				    .c_qty(existingCart.getC_qty() + c_qty) 
				    .m_id(existingCart.getM_id())
				    .product(existingCart.getProduct())
				    .build();
		}else {
			// 등록
			cart = Cart.builder()
				    .c_qty(c_qty) 
				    .m_id(m_id)
				    .product(Product.builder().p_no(p_no).build())
				    .build();
		}
		
		return cartRepository.save(cart);
	}
	
	/**
	 * 카트 수량 변경 
	 */
	@Transactional
	public Cart updateQty(int c_no, int c_qty) throws Exception {
		Optional<Cart> optionalCart = cartRepository.findById(c_no);
		Cart existingCart = optionalCart.orElseThrow(() -> new EntityNotFoundException());

		Cart updatedCart = Cart.builder()
		    .c_no(existingCart.getC_no())
		    .c_qty(c_qty) 
		    .m_id(existingCart.getM_id())
		    .product(existingCart.getProduct())
		    .build();

		return cartRepository.save(updatedCart);
	}
	
	/**
	 * 카트 상품 하나 삭제 
	 */
	@Transactional
	public void removeCart(int c_no) throws Exception {
		cartRepository.deleteById(c_no);
	}
	
	/**
	 * 카트 비우기 
	 */
	@Transactional
	public void removeCartAll(String m_id) throws Exception {
		cartRepository.deleteCartByMId(m_id);
	}
	
	/**
	 * 특정 카트 상품 정보 조회
	 */
	@Transactional(readOnly = true)
	public Cart getCartByNo(int c_no) throws Exception {
		Optional<Cart> optionalCart = cartRepository.findById(c_no);
		Cart cart = optionalCart.orElseThrow(() -> new EntityNotFoundException());
		return cart;
	}
	
	/**
	 * 회원의 카트 리스트 조회
	 */
	@Transactional(readOnly = true)
	public Map<String, Object> cartListAll(String m_id) throws Exception{
		Map<String, Object> cartResultMap = new HashMap<String, Object>();
		List<CartDto> cartDtoList = CartRepositoryCustom.findCartListAll(m_id);
		List<Cart> cartList = new ArrayList<Cart>();
		int totalPrice = 0;
		for(CartDto cartDto : cartDtoList) {
			totalPrice += cartDto.getC_qty() * cartDto.getP_price();
			cartList.add(Cart.fromDto(cartDto));
		}
		cartResultMap.put("cartList", cartList);
		cartResultMap.put("totalPrice", totalPrice);
		return cartResultMap;
	}
}
