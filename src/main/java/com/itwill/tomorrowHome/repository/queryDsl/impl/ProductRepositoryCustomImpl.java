package com.itwill.tomorrowHome.repository.queryDsl.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.itwill.tomorrowHome.domain.Product;
import com.itwill.tomorrowHome.domain.QProduct;
import com.itwill.tomorrowHome.repository.queryDsl.ProductRepositoryCustom;
import com.itwill.tomorrowHome.util.Utility;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

@Repository
@Qualifier("productRepositoryCustom")
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {
	private final JPAQueryFactory queryFactory;
	private final QProduct product = QProduct.product;
	
    public ProductRepositoryCustomImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }
	
    /**
     * 조건별 상품 전체 개수 조회
     */
    @Override
    public int countProductList(Map<String, String> params) {
        BooleanBuilder builder = new BooleanBuilder();

        Integer cgNo = Utility.parseInteger(params.get("cg_no"));
        Integer lowPrice = Utility.parseInteger(params.get("lowPrice"));
        Integer highPrice = Utility.parseInteger(params.get("highPrice"));
        String color = params.get("color");
        String brand = params.get("brand");
        Integer score = Utility.parseInteger(params.get("score"));
        String search = params.get("search");

        // 조건 (where)
        if (cgNo != null) {
            builder.and(product.cg_no.eq(cgNo));
        }
        if (lowPrice != null && highPrice != null) {
            builder.and(product.p_price.between(lowPrice, highPrice));
        }
        if (color != null && !color.isEmpty()) {
            builder.and(product.p_color.eq(color));
        }
        if (brand != null && !brand.isEmpty()) {
            builder.and(product.p_brand.eq(brand));
        }
        if (score != null) {
            builder.and(product.p_avg_score.eq(score.doubleValue()));
        }
        if (search != null && !search.isEmpty()) {
            builder.and(product.p_name.contains(search));
        } 

        return (int) queryFactory
                .selectFrom(product)
                .where(builder)
                .fetch()
                .size();
    }
    
	/**
	 * 상품 리스트 조회
	 */
	@Override
	public List<Product> findProductList(Map<String, String> params) {
		BooleanBuilder builder = new BooleanBuilder();

        Integer cgNo = Utility.parseInteger(params.get("cg_no"));
        Integer lowPrice = Utility.parseInteger(params.get("lowPrice"));
        Integer highPrice = Utility.parseInteger(params.get("highPrice"));
        String color = params.get("color");
        String brand = params.get("brand");
        Integer score = Utility.parseInteger(params.get("score"));
        String search = params.get("search");
        String sortBy = params.get("sortBy");
        Integer pageScale = Utility.parseInteger(params.get("page_scale"));
        Integer start = Utility.parseInteger(params.get("start"));

        // 조건 (where)
        if (cgNo != null) {
            builder.and(product.cg_no.eq(cgNo));
        }
        if (lowPrice != null && highPrice != null) {
            builder.and(product.p_price.between(lowPrice, highPrice));
        }
        if (color != null && !color.isEmpty()) {
            builder.and(product.p_color.eq(color));
        }
        if (brand != null && !brand.isEmpty()) {
            builder.and(product.p_brand.eq(brand));
        }
        if (score != null) {
            builder.and(product.p_avg_score.eq(score.doubleValue()));
        }
        if (search != null && !search.isEmpty()) {
            builder.and(product.p_name.contains(search));
        }

        // 정렬 (order by)
        OrderSpecifier<?> orderSpecifier = null;
        if (sortBy != null && !sortBy.isEmpty()) {
            switch (sortBy) {
                case "price_hl":
                    orderSpecifier = product.p_price.desc();
                    break;
                case "price_lh":
                    orderSpecifier = product.p_price.asc();
                    break;
                case "score":
                    orderSpecifier = product.p_avg_score.desc();
                    break;
            }
        }

        // Execute query
        var query = queryFactory.selectFrom(product).where(builder);

        if (orderSpecifier != null) {
            query = query.orderBy(orderSpecifier);
        }

        return query
                .offset(start != null ? start : 0)
                .limit(pageScale != null ? pageScale : 9)
                .fetch();
    }
	
}
