/*
동적 이벤트 등록
*/
$("#review_list").on("click", ".review_btn.update_form", update_review_form);
$("#review_list").on("click", ".review_btn.delete", delete_review);

/*
상품의 리뷰 관련 정보 수정 
*/
function updateReviewInfo(p_no){
	$.ajax({
		url: "update_review_info", 
		method: "post",
		data: {
			"p_no":p_no
		},
		dataType: "json",
		headers: {'X-CSRF-TOKEN': $("input[name='_csrf']").val()}, // CSRF 토큰을 헤더에 추가
		success:function(resultObj){
			console.log(resultObj);
			if(resultObj.errorCode > 0){
				let buffer = "";
				for(let i=0; i<resultObj.data.avg_score; i++){
					buffer += `<i class="fa fa-star" aria-hidden="true"></i>`;
				}
				buffer += `<span class="text-muted">&nbsp;(${resultObj.data.count}개의 리뷰)</span>`;
				$("#review_avg_score_star").html(buffer);
				$("#review_count_tab").html(`(${resultObj.data.count})`);
			}else{
				Toast.fire({ icon: 'error', title: resultObj.errorMsg });
			}
		}
	});
}

/*
리뷰 등록 
*/
$(".submit_a_review_area.mt-50").on("submit", "#review_form", function(e){
	if(!check_session()){
		ToastConfirm.fire({ icon: 'question', 
				title: "로그인이 필요한 페이지입니다.\n 로그인 하시겠습니까?"}).then((result) => {
				if(result.isConfirmed){
					location.href = "login_form";
				}
			});
		e.preventDefault();
		return;
	}
	let score = document.review_form.star.value;
	let title = document.review_form.title.value.trim();
	let content = document.review_form.content.value.trim();
	
	if(score == "" || title == "" || content == ""){
		Toast.fire({ icon: 'warning', title: "별점, 제목, 내용을 모두 입력해주세요" });
		return false;
	}
	
	const jsonData = {
		"r_score":score,
		"r_title":title,
		"r_content":content,
		"m_id":document.review_form.m_id.value,
		"p_no":document.review_form.p_no.value
	};
	
	$.ajax({
		url: "insert_review",
		method: "post",
		contentType: "application/json",
		data: JSON.stringify(jsonData),
		dataType: "json",
		headers: {'X-CSRF-TOKEN': $("input[name='_csrf']").val()}, // CSRF 토큰을 헤더에 추가
		success:function(resultObj){
			console.log(resultObj);
			let review = resultObj.data;
			if(resultObj.errorCode > 0){
				Toast.fire({ icon: 'success', title: resultObj.errorMsg });
				let fDate = formatDate(review.r_date);
				let htmlBuffer = `<li id="review_li_${review.r_no}">
				                <div class="single_user_review mb-15">
				                    <div class="review-rating">`;
				for(let i=0; i<review.r_score; i++){
					htmlBuffer += `<i class="fa fa-star" aria-hidden="true"></i>`;
				}
				htmlBuffer += `&nbsp;<span>${review.r_title}</span> &nbsp;
				                        <input type="button" value="수정" class="review_btn update_form" data-r_no="${review.r_no}">&nbsp;
				                		<input type="button" value="삭제" class="review_btn delete" data-r_no="${review.r_no}" data-p_no="${review.product.p_no}" data-m_id="${review.m_id}">
				                    </div>
				                    <div class="review-details">
				                        <p>by ${review.m_id} on <span>${fDate}</span></p>
				                    </div>
				                    <div class="review-details">
				                        <p>${review.r_content}</p>
				                    </div>
				                </div>
				            </li>`;
				$("#review_list").append(htmlBuffer);
				// 작성폼 초기화
				$("#review_form input[name='star']").prop("checked", false);
				$("#review_form input[name='title']").val("");
				$("#review_form [name='content']").val("");
				// 리뷰정보 가져오기
				updateReviewInfo(review.product.p_no);
			}else{
				Toast.fire({ icon: 'error', title: resultObj.errorMsg });
			}
		}
	});
	e.preventDefault();
});

/*
리뷰 삭제
*/
function delete_review(e){
	ToastConfirm.fire({ icon: 'question', 
		title: "리뷰를 삭제하시겠습니까?"}).then((result) => {
		if(result.isConfirmed){
			let r_no = $(e.target).data("r_no");
			let p_no = $(e.target).data("p_no");
			$.ajax({
				url: "delete_review",
				method: "post",
				data: {
					"r_no":r_no
				},
				dataType: "json",
				headers: {'X-CSRF-TOKEN': $("input[name='_csrf']").val()}, // CSRF 토큰을 헤더에 추가
				success:function(resultObj){
					console.log(resultObj);
					if(resultObj.errorCode > 0){
						Toast.fire({ icon: 'success', title: resultObj.errorMsg });
						$(e.target).parent().parent().parent().fadeOut(350, function(){
							$(this).remove();
						});
						setReviewSubmitForm(p_no, $(e.target).data("m_id"));
						updateReviewInfo(p_no);
					}else{
						Toast.fire({ icon: 'error', title: resultObj.errorMsg });
					}
				}
			});
		}
	});
}

/*
리뷰 수정 폼
*/
function update_review_form(e){
	let r_no = $(e.target).data("r_no");
	console.log(r_no);
	$.ajax({
		url: "update_review_form", 
		method: "post",
		data: {
			"r_no":r_no
		},
		dataType: "json",
		headers: {'X-CSRF-TOKEN': $("input[name='_csrf']").val()}, // CSRF 토큰을 헤더에 추가
		success:function(resultObj){
			console.log(resultObj);
			if(resultObj.errorCode > 0){
				let review = resultObj.data;
				let score = review.r_score;
				let htmlBuffer = `<h4><font color="red">✔️</font>Update Your Review</h4>
                                    <form action="" method="post" id="review_update_form" name="review_update_form">
                                    	<input type="hidden" name="p_no" value="${review.product.p_no}" >
                                    	<input type="hidden" name="r_no" value="${review.r_no}" >
                                        <div class="form-group">
                                            <span>Your Ratings</span>
                                            <div class="stars" id="submit_stars">
                                                <input type="radio" name="star" class="star-1" id="star-1" value="1">
                                                <label class="star-1" for="star-1">1</label>
                                                <input type="radio" name="star" class="star-2" id="star-2" value="2">
                                                <label class="star-2" for="star-2">2</label>
                                                <input type="radio" name="star" class="star-3" id="star-3" value="3">
                                                <label class="star-3" for="star-3">3</label>
                                                <input type="radio" name="star" class="star-4" id="star-4" value="4">
                                                <label class="star-4" for="star-4">4</label>
                                                <input type="radio" name="star" class="star-5" id="star-5" value="5">
                                                <label class="star-5" for="star-5">5</label>
                                                <span></span>
                                            </div>
                                        </div>
                                        <div class="form-group">
                                            <label for="m_id">Member ID</label>
                                            <input type="text" class="form-control" name="m_id" value="${review.m_id}" readonly>
                                        </div>
                                        <div class="form-group">
                                            <label for="title">Title</label>
                                            <input type="text" class="form-control" name="title" value="${review.r_title}" maxlength="20">
                                        </div>
                                        <div class="form-group">
                                            <label for="comments">Comments</label>
                                            <textarea class="form-control" name="content" id="comments" rows="5" maxlength="200">${review.r_content}</textarea>
                                        </div>
                                        <button type="submit" id="review_update_btn" class="btn btn-primary">Update Review</button>
                                    	<button type="button" class="btn btn-outline-primary mb-1" onclick="setReviewSubmitForm(${review.product.p_no}, '${review.m_id}')" style="float:right">Cancel</button>
                                    </form>`;
                  $(".submit_a_review_area.mt-50").html(htmlBuffer).fadeIn(500);
                  $(`input:radio[id='star-${review.r_score}']`).prop("checked",true);
			}else{
				Toast.fire({ icon: 'error', title: resultObj.errorMsg });
			}
		}
	});
}

/*
리뷰수정
*/
$(".submit_a_review_area.mt-50").on("submit", "#review_update_form", function(e){
	let score = document.review_update_form.star.value;
	let title = document.review_update_form.title.value;
	let content = document.review_update_form.content.value;
	
	if(score == "" || title == "" || content == ""){
		Toast.fire({ icon: 'warning', title: "별점, 제목, 내용을 모두 입력해주세요" });
		return false;
	}
	
	const jsonData = {
		"r_score":score,
		"r_title":title,
		"r_content":content,
		"m_id":document.review_update_form.m_id.value,
		"p_no":document.review_update_form.p_no.value,
		"r_no":document.review_update_form.r_no.value
	};

	$.ajax({
		url: "update_review",
		method: "post",
		contentType: "application/json",
		data: JSON.stringify(jsonData),
		dataType: "json",
		headers: {'X-CSRF-TOKEN': $("input[name='_csrf']").val()}, // CSRF 토큰을 헤더에 추가
		success:function(resultObj){
			console.log(resultObj);
			if(resultObj.errorCode > 0){
				Toast.fire({ icon: 'success', title: resultObj.errorMsg });
				let review = resultObj.data;
				let fDate = formatDate(review.r_date);
				let htmlBuffer =`<div class="single_user_review mb-15">
	                                <div class="review-rating">`;
				for(let i=0; i<review.r_score; i++){
					htmlBuffer += `<i class="fa fa-star" aria-hidden="true"></i>`;
				} 
				htmlBuffer += `&nbsp;<span>${review.r_title}</span> &nbsp;
									<input type="button" value="수정" class="review_btn update_form" data-r_no="${review.r_no}" >&nbsp;
                            		<input type="button" value="삭제" class="review_btn delete" data-r_no="${review.r_no}" data-p_no="${review.product.p_no}" data-m_id="${review.m_id}">
                                </div>
                                    <div class="review-details">
                                        <p>by ${review.m_id} on <span>${fDate}</span></p>
                                    </div>
                                    <div class="review-details">
                                        <p>${review.r_content}</p>
                                    </div>
                                </div>`;
				$(`#review_li_${review.r_no}`).html(htmlBuffer);
				setReviewSubmitForm(review.product.p_no, review.m_id);
				updateReviewInfo(review.product.p_no);
			}else{
				Toast.fire({ icon: 'error', title: resultObj.errorMsg });
			}
		}
	});
	e.preventDefault();
});

/*
리뷰 등록 폼 출력 
*/
function setReviewSubmitForm(p_no, m_id){
	let submit_form_html = `<h4>Submit A Review</h4>
                    <form action="" method="post" id="review_form" name="review_form">
                    	<input type="hidden" name="p_no" value="${p_no}" >
                        <div class="form-group">
                            <span>Your Ratings</span>
                            <div class="stars" id="submit_stars">
                                <input type="radio" name="star" class="star-1" id="star-1" value="1">
                                <label class="star-1" for="star-1">1</label>
                                <input type="radio" name="star" class="star-2" id="star-2" value="2">
                                <label class="star-2" for="star-2">2</label>
                                <input type="radio" name="star" class="star-3" id="star-3" value="3">
                                <label class="star-3" for="star-3">3</label>
                                <input type="radio" name="star" class="star-4" id="star-4" value="4">
                                <label class="star-4" for="star-4">4</label>
                                <input type="radio" name="star" class="star-5" id="star-5" value="5">
                                <label class="star-5" for="star-5">5</label>
                                <span></span>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="m_id">Member ID</label>
                            <input type="text" class="form-control" name="m_id" value="${m_id}" readonly>
                        </div>
                        <div class="form-group">
                            <label for="title">Title</label>
                            <input type="text" class="form-control" name="title" maxlength="20">
                        </div>
                        <div class="form-group">
                            <label for="comments">Comments</label>
                            <textarea class="form-control" name="content" id="comments" rows="5" maxlength="200"></textarea>
                        </div>
                        <button type="submit" id="review_write_btn" class="btn btn-primary">Submit Review</button>
                    </form>`;
$(".submit_a_review_area.mt-50").html(submit_form_html);
}

