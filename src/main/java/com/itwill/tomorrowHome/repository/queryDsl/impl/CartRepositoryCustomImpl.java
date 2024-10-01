package com.itwill.tomorrowHome.repository.queryDsl.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.itwill.tomorrowHome.domain.QCart;
import com.itwill.tomorrowHome.domain.QImage;
import com.itwill.tomorrowHome.domain.QProduct;
import com.itwill.tomorrowHome.dto.CartDto;
import com.itwill.tomorrowHome.repository.queryDsl.CartRepositoryCustom;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

@Repository
@Qualifier("cartRepositoryCustom")
public class CartRepositoryCustomImpl implements CartRepositoryCustom {
	private final JPAQueryFactory queryFactory;
	private final QCart cart = QCart.cart;
	
    public CartRepositoryCustomImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }
    
    /**
     * 사용자 카트 전체 조회
     */
    @Override
    public List<CartDto> findCartListAll(String m_id) {
         QProduct product = QProduct.product;
         QImage image = QImage.image;

         return queryFactory
                 .select(
                         Projections.fields(
                                 CartDto.class,
                                 cart.c_no,
                                 cart.c_qty,
                                 cart.m_id,
                                 product.p_no,
                                 product.p_name,
                                 product.p_price,
                                 image.im_name.min().as("im_name")
                         )
                 )
                 .from(cart)
                 .join(product).on(cart.product.p_no.eq(product.p_no))
                 .leftJoin(image).on(product.p_no.eq(image.product.p_no))
                 .where(cart.m_id.eq(m_id)
                         .and(image.im_div.eq("IMG")))
                 .groupBy(cart.c_no, cart.c_qty, cart.m_id, product.p_no, product.p_name, product.p_price)
                 .fetch();
    }
}
