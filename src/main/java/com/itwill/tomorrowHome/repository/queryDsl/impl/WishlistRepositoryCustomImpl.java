package com.itwill.tomorrowHome.repository.queryDsl.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.itwill.tomorrowHome.domain.QImage;
import com.itwill.tomorrowHome.domain.QProduct;
import com.itwill.tomorrowHome.domain.QWishlist;
import com.itwill.tomorrowHome.dto.WishlistDto;
import com.itwill.tomorrowHome.repository.queryDsl.WishlistRepositoryCustom;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

@Repository
@Qualifier("wishlistRepositoryCustom")
public class WishlistRepositoryCustomImpl implements WishlistRepositoryCustom {
	private final JPAQueryFactory queryFactory;
	private final QWishlist wishlist = QWishlist.wishlist;
	
    public WishlistRepositoryCustomImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }
     
    /**
     * 사용자 위시리스트 전체 조회
     */
    @Override
    public List<WishlistDto> findWishlist(String m_id) { 
         QProduct product = QProduct.product;
         QImage image = QImage.image;

         return queryFactory
                 .select(
                         Projections.fields(
                        		 WishlistDto.class,
                                 wishlist.w_no,
                                 wishlist.m_id,
                                 product.p_no,
                                 product.p_name,
                                 product.p_price,
                                 image.im_name.min().as("im_name")
                         )
                 )
                 .from(wishlist)
                 .join(product).on(wishlist.product.p_no.eq(product.p_no))
                 .leftJoin(image).on(product.p_no.eq(image.product.p_no))
                 .where(wishlist.m_id.eq(m_id)
                         .and(image.im_div.eq("IMG")))
                 .groupBy(wishlist.w_no, wishlist.m_id, product.p_no, product.p_name, product.p_price)
                 .fetch();
    }
}
