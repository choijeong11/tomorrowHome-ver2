package com.itwill.tomorrowHome.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.tomorrowHome.domain.Qna;
import com.itwill.tomorrowHome.dto.QnaDto;
import com.itwill.tomorrowHome.repository.QnaRepository;
import com.itwill.tomorrowHome.repository.queryDsl.QnaRepositoryCustom;
import com.itwill.tomorrowHome.util.PageMaker;
import com.itwill.tomorrowHome.util.PageMakerDto;

import jakarta.persistence.EntityNotFoundException;

@Service
public class QnaService {
	@Autowired
	private QnaRepository qnaRepository;
	@Autowired
	@Qualifier("qnaRepositoryCustom")
	private QnaRepositoryCustom qnaRepositoryCustom;

	/**
	 * 새로운 게시물을 추가
	 */
	@Transactional
	public void insertNewQna(Qna qna) throws Exception {
		// 게시물 등록
		Qna saveQna = qnaRepository.save(qna);
		// date, group_no 세팅
		qnaRepository.save(Qna.builder()
						.q_no(saveQna.getQ_no())
						.q_title(saveQna.getQ_title())
						.q_content(saveQna.getQ_content())
						.q_date(new Date())
						.q_count(saveQna.getQ_count())
						.q_group_no(saveQna.getQ_no())
						.q_step(saveQna.getQ_step())
						.q_depth(saveQna.getQ_depth())
						.m_id(saveQna.getM_id())
						.build());
	}

	/**
	 * 게시물 번호에 해당하는 게시물 정보를 반환
	 */
	@Transactional
	public Qna selectQna(int q_no) throws Exception {
		Optional<Qna> optionalQna = qnaRepository.findById(q_no);
		Qna qna = optionalQna.orElseThrow(() -> new EntityNotFoundException());
		return qna;
	}

	/**
	 * 답글 게시물을 추가
	 */
	@Transactional
	public void insertReply(Qna qna) throws Exception {
		// 댓글을 작성할 대상글(원글)의 정보를 조회
		Qna upperQna = selectQna(qna.getQ_no());
		// 영향을 받는 기존 글들의 논리적인 순서 번호 변경
		qnaRepository.incrementQStepByGroupNo(upperQna.getQ_step(), upperQna.getQ_group_no());
		// 댓글 삽입
		qnaRepository.save(Qna.builder()
				.q_title(qna.getQ_title())
				.q_content(qna.getQ_content())
				.q_date(new Date())
				.q_count(0)
				.q_group_no(upperQna.getQ_group_no())
				.q_step(upperQna.getQ_step() + 1)
				.q_depth(upperQna.getQ_depth() + 1)
				.m_id(qna.getM_id())
				.build());
	}

	/**
	 * 게시물 리스트를 반환
	 */
	@Transactional
	public PageMakerDto<QnaDto> selectQnaList(int currentPage, String search_type, String search_value) throws Exception {
		// 1.전체글의 갯수
		int totRecordCount = qnaRepository.countQnaList(search_type, search_value); 
		// 2.paging계산(PageMaker 유틸클래스)  
		PageMaker pageMaker = new PageMaker(totRecordCount, currentPage, 10, 10);
		// 3.게시물데이타 얻기
		Map<String, Object> map = new HashMap<>();
		map.put("start", (pageMaker.getPageBegin() - 1)); // mysql limit이 0부터 시작
		map.put("page_scale", pageMaker.getPAGE_SCALE());
		map.put("search_type", search_type);
		map.put("search_value", search_value);
		map.put("totRecordCount", totRecordCount);
		List<QnaDto> qnaDtoList = qnaRepository.findQnaList(map);
		PageMakerDto<QnaDto> pageMakerQnaList = new PageMakerDto<QnaDto>(qnaDtoList, pageMaker, totRecordCount);
		return pageMakerQnaList;
	}

	/**
	 * 게시물을 삭제
	 */
	@Transactional
	public void deleteQna(int q_no) throws Exception {
		qnaRepository.deleteById(q_no);
	}

	/**
	 * 게시물 내용을 수정
	 */
	@Transactional
	public void updateQna(Qna qna) throws Exception {
		Qna beforeQna = selectQna(qna.getQ_no());
		qnaRepository.save(Qna.builder()
						.q_no(beforeQna.getQ_no())
						.q_title(qna.getQ_title())
						.q_content(qna.getQ_content())
						.q_date(beforeQna.getQ_date())
						.q_count(beforeQna.getQ_count())
						.q_group_no(beforeQna.getQ_group_no())
						.q_step(beforeQna.getQ_step())
						.q_depth(beforeQna.getQ_depth())
						.m_id(beforeQna.getM_id())
						.build());
	}

	/**
	 * 게시물 조회수를 1 증가
	 */
	@Transactional
	public void updateReadCount(int q_no) throws Exception {
		qnaRepository.incrementQnaCount(q_no);
	}

}
