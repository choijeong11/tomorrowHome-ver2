package com.itwill.tomorrowHome.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itwill.tomorrowHome.domain.Qna;
import com.itwill.tomorrowHome.repository.queryDsl.QnaRepositoryCustom;

public interface QnaRepository extends JpaRepository<Qna, Integer>, QnaRepositoryCustom {
	/**
	 * 게시물 조회수 증가
	 */
	@Modifying
	@Query("UPDATE Qna q SET q.q_count = q.q_count + 1 WHERE q.q_no = :q_no")
	int incrementQnaCount(@Param("q_no") int q_no);
	
	/**
	 * 게시물 step 정보 변경
	 */
	@Modifying
    @Query("UPDATE Qna q SET q.q_step = q.q_step + 1 WHERE q.q_step > :q_step AND q.q_group_no = :q_group_no")
    int incrementQStepByGroupNo(@Param("q_step") int q_step, @Param("q_group_no") int q_group_no);
}
