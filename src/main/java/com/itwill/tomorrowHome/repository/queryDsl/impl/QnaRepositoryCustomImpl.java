package com.itwill.tomorrowHome.repository.queryDsl.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.itwill.tomorrowHome.domain.QQna;
import com.itwill.tomorrowHome.dto.QnaDto;
import com.itwill.tomorrowHome.repository.queryDsl.QnaRepositoryCustom;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

@Repository
@Qualifier("qnaRepositoryCustom")
public class QnaRepositoryCustomImpl implements QnaRepositoryCustom { 
	private final JPAQueryFactory queryFactory;
	private final QQna qna = QQna.qna;
	
    public QnaRepositoryCustomImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }
     
    /**
     * 게시물 수 조회
     */
    @Override
    public int countQnaList(String search_type, String search_value) {
    	BooleanExpression predicate = qna.isNotNull(); // 기본 조건
    	
		if (search_value != null && !search_value.isEmpty()) {
            switch (search_type) {
                case "search_title":
                    predicate = predicate.and(qna.q_title.containsIgnoreCase(search_value));
                    break;
                case "search_content":
                    predicate = predicate.and(qna.q_content.containsIgnoreCase(search_value));
                    break;
                case "search_all":
                    predicate = predicate.and(qna.q_title.containsIgnoreCase(search_value)
                                           .or(qna.q_content.containsIgnoreCase(search_value)));
                    break;
                case "search_writer":
                    predicate = predicate.and(qna.m_id.containsIgnoreCase(search_value));
                    break;
            }
        }

        return queryFactory
                .select(qna.count())
                .from(qna)
                .where(predicate)
                .fetchOne()
                .intValue();
    }
    
	/**
	 * 게시물 리스트 조회
	 */
	@Override
	public List<QnaDto> findQnaList(Map<String, Object> params) {
		BooleanBuilder builder = new BooleanBuilder();
		 
		String search_value = (String) params.get("search_value");
        String search_type = (String) params.get("search_type");
        Integer pageScale = (Integer) params.get("page_scale");
        Integer start = (Integer) params.get("start");

        if (search_value != null && !search_value.isEmpty()) {
            switch (search_type) {
                case "search_title":
                	builder.and(qna.q_title.contains(search_value));
                    break;
                case "search_content":
                	builder.and(qna.q_content.contains(search_value));
                    break;
                case "search_all":
                	builder.and(qna.q_title.contains(search_value)
                                    .or(qna.q_content.contains(search_value)));
                    break;
                case "search_writer":
                	builder.and(qna.m_id.contains(search_value));
                    break;
            }
        }

        List<QnaDto> qnaDtoList = queryFactory
        		.select(Projections.bean(
                        QnaDto.class,
                        qna.q_no,
                        qna.q_title,
                        qna.m_id,
                        qna.q_date,
                        qna.q_count,
                        qna.q_group_no,
                        qna.q_step,
                        qna.q_depth
                ))
                .from(qna)
                .where(builder)
                .orderBy(qna.q_group_no.desc(), qna.q_step.asc())
                .offset(start != null ? start : 0)
                .limit(pageScale != null ? pageScale : 10)
                .fetch();
        
        int rowNum = (Integer) params.get("totRecordCount") - start;
        for (QnaDto qnaDto : qnaDtoList) {
            qnaDto.setRowNum(rowNum--);
        }
        
        return qnaDtoList;
    }
    
}
