/*
회원 로그인
*/
$("#member_login_form").on("submit", function(event) {
    if ($("input[name='username']").val() == "" || $("input[name='password']").val() == "") {
		Toast.fire({ icon: "warning", title: "아이디와 비밀번호를 모두 입력해주세요." });
        event.preventDefault(); 
        return false;
    }
    return true;
});

/*
회원가입
*/
$('#member_register_form').on('submit',function(e){
	if(!member_validation()){
		$(this).find("[type='submit']").blur();
		return false;
	}
	
	let m_verified = $("#member_register_form [name=m_verified]").val();
	if(m_verified == "N"){
		Toast.fire({ icon: "warning", title: "이메일을 인증해주세요." });
		return false;
	}
	
	// 폼 데이터를 JSON 객체로 변환
	const formData = $(this).serializeArray();
	const jsonData = {};
	formData.forEach(item => {
	    jsonData[item.name] = item.value;
	});
	
	$.ajax({ 
		url: "member_register_rest", 
		method: "post", 
		contentType: "application/json", // JSON 형식으로 전송
		data: JSON.stringify(jsonData), 
		dataType: "json", 
		headers: {'X-CSRF-TOKEN': $("input[name='_csrf']").val()}, // CSRF 토큰을 헤더에 추가
		success:function(resultObj){ 
			console.log(resultObj); 
			if(resultObj.errorCode > 0){ 
				let id = $("#m_id").val();
				Toast.fire({ icon: "success", title: resultObj.errorMsg }).then((result) => {
					$(".m_check").each(function(i, v){
						$(v).val("");
					});
					// 인증 초기화 
					$("#m_email").val("").attr("readonly", false);
					$("#member_register_form [name=m_verified]").val("N");
					$("#verification_code").val("");
					$("#verification_msg").text("인증코드 6글자를 입력하세요.");
					$(".verification_mail").text("인증");
					
					$("input[name=username]").focus();
				});
			} else {
				$("#register_msg").text("등록 실패 : " + resultObj.errorMsg);
				$("#m_id").focus();
			}
		}
	});
	e.preventDefault();
});

function member_validation(){
	let result = true;
	$(".m_check").each(function(i, v){
		if($(v).val().trim() == ""){
			Toast.fire({ icon: 'warning', title: "필수 입력값을 입력하지 않았습니다.\n 모든 값을 입력해주세요." });
			result = false;
			return false;
		}
	});
	if(!result){
		return result;
	}
	let regPhone = /^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$/; 
	if(!regPhone.test($(".phone_number").val().trim())){
		Toast.fire({ icon: 'warning', title: "휴대폰 번호 형식이 유효하지 않습니다" });
		$(".phone_number").val("");
		return false;
	}
	return true;
}

/*
회원정보 수정 
*/
$("#member_update_btn").on("click", function(e){
	if(!member_update_valiidation()){
		return false;
	}
	
	ToastConfirm.fire({ icon: 'question', 
		title: "회원정보를 변경 하시겠습니까?"}).then((result) => {
			// 폼 데이터를 JSON 객체로 변환
			const formData = $("#member_accountDetail_form").serializeArray();
			const jsonData = {};
			formData.forEach(item => {
			    jsonData[item.name] = item.value;
			});
			
			$.ajax({ 
				url: "member_account_update_rest", 
				method: "post", 
				contentType: "application/json",  // JSON 형식으로 전송
				data: JSON.stringify(jsonData), 
				dataType: "json", 
				headers: {'X-CSRF-TOKEN': $("input[name='_csrf']").val()}, // CSRF 토큰을 헤더에 추가
				success:function(resultObj){ 
					console.log(resultObj); 
					if(resultObj.errorCode > 0){ 
						Toast.fire({ icon: "success", title: resultObj.errorMsg }).then((result) => {
							location.href = "account_details";
						});
					} else {
						Toast.fire({ icon: "error", title: resultObj.errorMsg });
					}
				}
			});
		})
});

function member_update_valiidation(){
	let result = true;
	$(".m_u_check").each(function(i, v){
		if($(v).val().trim() == ""){
			Toast.fire({ icon: 'warning', title: "필수 입력값을 입력하지 않았습니다." });
			result = false;
			return false;
		}
	});
	if(!result){
		return result;
	}
	let regPhone = /^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$/; 
	if(!regPhone.test($(".phone_number").val().trim())){
		Toast.fire({ icon: 'warning', title: "휴대폰번호 형식이 유효하지 않습니다" });
		$(".phone_number").val("");
		return false;
	}
	// 비밀번호 변경
	let newPass = $("#newPass").val().trim();
	let confirmPass = $("#confirmPass").val().trim();
	if(newPass != "" || confirmPass != ""){
		if(newPass == ""){
			Toast.fire({ icon: 'warning', title: "변경할 비밀번호를 입력해주세요" });
			$("#newPass").focus();
			return false;
		}
		if(confirmPass == ""){
			Toast.fire({ icon: 'warning', title:"비밀번호 확인을 입력해주세요" });
			$("#confirmPass").focus();
			return false;
		}
		if(newPass != confirmPass){
			Toast.fire({ icon: 'warning', title:"새 비밀번호와 비밀번호 확인이 일치하지 않습니다" });
			$("#confirmPass").focus();
			return false;
		}
	}
	return result;
}

/*
회원탈퇴
*/
$("#withdrawal_btn").on("click", function(){
	const jsonData = {"m_id": $("#m_id").val()};
	
	ToastConfirm.fire({ icon: 'question', 
		title: "회원을 탈퇴하시겠습니까?"}).then((result) => {
		if(result.isConfirmed){
			$.ajax({ 
				url: "member_withdrawal_rest", 
				method: "post", 
				contentType: "application/json",  // JSON 형식으로 전송
				data: JSON.stringify(jsonData),
				dataType: "json", 
				headers: {'X-CSRF-TOKEN': $("input[name='_csrf']").val()}, // CSRF 토큰을 헤더에 추가
				success:function(resultObj){ 
					console.log(resultObj); 
					if(resultObj.errorCode > 0){ 
						Toast.fire({ icon: "success", title: resultObj.errorMsg }).then((result) => {
							location.href = "index";
						});
					} else {
						Toast.fire({ icon: "error", title: resultObj.errorMsg });
					}
				}
			});
		}
	});
});

/*
인증코드 메일 발송
*/
$(".verification_mail").on("click", function(e){
	let verified_val = $("#member_register_form [name=m_verified]").val();
	if(verified_val == "Y"){
		Toast.fire({ icon: "info", title: "이미 인증이 완료되었습니다." });
		return;
	}
	
	let email = $("#m_email").val();
	if (!email) {
		Toast.fire({ icon: "info", title: "이메일을 입력해주세요." });
		return;
	}
	// 유효성 검사
	var emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
	if (!emailPattern.test(email)) {
		Toast.fire({ icon: "info", title: "유효한 이메일 주소를 입력해주세요." });
		return;
	}
	// 메일 발송
	$.ajax({
	    url: "member_send_verification_email",
	    method: "post", 
	    contentType: "application/json",
	    data: email,
		dataType: "json", 
		headers: {'X-CSRF-TOKEN': $("input[name='_csrf']").val()}, // CSRF 토큰을 헤더에 추가
		beforeSend: function() {
	        $('.loading-overlay').show();
	    },
	    success: function(resultObj) {
			if(resultObj.errorCode > 0){ 
				Toast.fire({ icon: "success", title: resultObj.errorMsg });
				$(".verification_code_group").show();
				$("#verification_code").val("");
				$("#verification_msg").text("인증코드 6글자를 입력하세요.");
				stopCountdown(); // 기존 카운트 중지
				startCountdown(); // 5분 카운트
			} else {
				Toast.fire({ icon: "error", title: resultObj.errorMsg });
			}
	    },
		complete: function() {
	        $('.loading-overlay').hide();
	    }
	});
});

/*
카운트 함수 (인증코드 기한)
*/
let countdownInterval; 
function startCountdown() {
	let countdownTime = 300; // 5분(300초) 설정
    let countdownElement = $("#countdown_timer");
	
    countdownInterval = setInterval(function () {
        // 분과 초 계산
        let minutes = Math.floor(countdownTime / 60);
        let seconds = countdownTime % 60;
        // 초가 한 자리 수일 때 앞에 0을 추가
        seconds = seconds < 10 ? "0" + seconds : seconds;
        // 카운트다운 텍스트 업데이트
        countdownElement.text(minutes + ":" + seconds);
        // 시간이 다 되었을 경우 카운트다운 중지
        if (countdownTime <= 0) {
            clearInterval(countdownInterval);
			$(".verification_code_group").hide();
			$("#verification_code").val("");
			$("#verification_msg").text("인증코드 6글자를 입력하세요.");
        }
        countdownTime--;
    }, 1000); // 1초마다 실행
}

/*
카운트 함수 중지 (인증코드 기한)
*/
function stopCountdown() {
    clearInterval(countdownInterval); 
}

/*
인증코드 입력 이벤트
*/
$("#verification_code").on('keyup', function() {
    let inputValue = $(this).val(); // 입력된 값
	let length = inputValue.length;
	
	if(length == 6){
		let jsonData = {
			"email" : $("#m_email").val()
			, "code" : inputValue
		}
		
		$.ajax({
		    url: "member_email_verification",
		    method: "post", 
		    contentType: "application/json",
		    data: JSON.stringify(jsonData),
			dataType: "json", 
			headers: {'X-CSRF-TOKEN': $("input[name='_csrf']").val()}, // CSRF 토큰을 헤더에 추가
		    success: function(resultObj) {
				if(resultObj.errorCode > 0){ 
					Toast.fire({ icon: "success", title: resultObj.errorMsg });
					$(".verification_mail").text("인증완료");
					$("#member_register_form [name=m_verified]").val("Y");
					$(".verification_code_group").hide();
					$("#m_email").attr("readonly", true);
					stopCountdown();
				} else {
					$("#verification_msg").text(resultObj.errorMsg);
				}
		    }
		});
	}else{
		$("#verification_msg").text("인증코드 6글자를 입력하세요.");
		return;
	}
});

/*
메일 입력시 이벤트
*/
$("#m_email").on("focus", function(e){
	let verified_val = $("#member_register_form [name=m_verified]").val();
	if(verified_val == "Y"){
		let inputEmail = $("#m_email").val();
		ToastConfirm.fire({ icon: "info", title: "메일 인증 완료 상태입니다. 메일 수정시 재인증이 요구됩니다.\n수정하시겠습니까?" }).then((result) => {
						if (result.isConfirmed) {
							// 인증 초기화 
							$("#m_email").val("").attr("readonly", false);
							$("#member_register_form [name=m_verified]").val("N");
							$("#verification_code").val("");
							$("#verification_msg").text("인증코드 6글자를 입력하세요.");
							$(".verification_mail").text("인증");
						}
					});
	}
});

/*
아이디 찾기 모달
*/
$("#find_id").on("click", function() {
    $("#findModal").modal("show");
	$("#findModalLabel").text("아이디 찾기");
	$("#findForm [name=find_type]").val("id");
});

/*
비밀번호 찾기 모달
*/
$("#find_pw").on("click", function() {
    $("#findModal").modal("show");
	$("#findModalLabel").text("비밀번호 찾기");
	$("#findForm [name=find_type]").val("pw");
});

/*
모달 오픈시 이벤트
*/
$('#findModal').on('show.bs.modal', function(e) {
	$("#find_pw_container, #findPwSubmit").hide();
	$("#findForm")[0].reset();
	$("#findSendSubmit").show();
	$("#find_email").attr("readonly", false);
});

/*
아이디/비밀번호 찾기
*/
$("#findSendSubmit, #findPwSubmit").on("click", function() {
	let type = $("#findForm [name=find_type]").val();
	let email = $("#find_email").val();
	
	if (!email) {
		Toast.fire({ icon: "info", title: "이메일을 입력해주세요." });
		return false;
	}
	
	// 유효성 검사
	var emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
	if (!emailPattern.test(email)) {
		Toast.fire({ icon: "info", title: "유효한 이메일 주소를 입력해주세요." });
		return false;
	}
	
	let clickedId = $(this).attr("id");
	let jsonData;
	let url;
	let successFunc;
	
	if(clickedId == "findSendSubmit"){
		jsonData = {
			"type" : type
			, "email" : email
		};
		url = "member_find_send_email";
		successFunc = find_mail_success;
		
	}else if(clickedId == "findPwSubmit"){
		let temp_pw = $("#temp_pw").val();
		let new_pw = $("#new_pw").val();
		let new_pw_check = $("#new_pw_check").val();
		
		if(new_pw != new_pw_check){
			Toast.fire({ icon: "warning", title: "비밀번호와 비밀번호 확인이 일치하지 않습니다" });
			return false;
		}
		
		jsonData = {
			"type" : type
			, "email" : email
			, "temp_pw" : temp_pw
			, "new_pw" : new_pw
		};
		url = "member_find_password";
		successFunc = find_pw_success;
	}

	// 메일 발송
	$.ajax({
	    url: url,
	    method: "post", 
	    contentType: "application/json",
	    data: JSON.stringify(jsonData),
		dataType: "json", 
		headers: {'X-CSRF-TOKEN': $("input[name='_csrf']").val()}, // CSRF 토큰을 헤더에 추가
		beforeSend: function() {
	        $('.loading-overlay').show();
	    },
	    success: successFunc,
		complete: function() {
	        $('.loading-overlay').hide();
	    }
	});
});

function find_mail_success(resultObj){
	if(resultObj.errorCode > 0){ 
		let type = resultObj.data.type;
		Toast.fire({ icon: "success", title: resultObj.errorMsg });
		if(type == "id"){
			$("#findModal").modal("hide");
		}else if(type == "pw"){
			$("#find_pw_container, #findPwSubmit").show();
			$("#findSubmit").text("비밀번호 찾기");
			$("#findSendSubmit").hide();
			$("#find_email").attr("readonly", true);
		}
	} else {
		Toast.fire({ icon: "error", title: resultObj.errorMsg });
	}
}

function find_pw_success(resultObj){
	if(resultObj.errorCode > 0){ 
		Toast.fire({ icon: "success", title: resultObj.errorMsg });
		$("#findModal").modal("hide");
	} else {
		Toast.fire({ icon: "error", title: resultObj.errorMsg });
	}
}