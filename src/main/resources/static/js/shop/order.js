/* 
주문 다음 버튼 
*/ 
$(".order_next").on("click", function(e){
	let url = "";
	let form_id = "";
	switch(e.target.id){
		case "orderer_next_btn" : url = "checkout_3"; form_id = "orderer_info_form"; break; 
		case "reciever_next_btn" : url = "checkout_4"; form_id = "reciever_info_form"; break; 
		case "payment_next_btn" : url = "checkout_5"; form_id = "payment_info_form"; break; 
	}
	if(checkValidation()){
	 	move_order_next_step(form_id, url);
 	}
	e.preventDefault();  
});

/*
유효성검사
*/
function checkValidation(){
	let result = true;
	$(".o_check").each(function(i, v){
		if($(v).val().trim() == ""){
			Toast.fire({ icon: 'warning', title: "필수 입력값을 입력하지 않았습니다" });
			result = false;
			return false;
		}
	});
	if(!result){
		return result;
	}
	if($(".phone_number").length != 0){
		let regPhone = /^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$/; 
		if(!regPhone.test($(".phone_number").val().trim())){
			Toast.fire({ icon: 'warning', title: "휴대폰 번호 형식이 유효하지 않습니다" });
			$(".phone_number").val("");
			return false;
		}
	}
	if($(".panel-body input[type='checkbox']").length != 0){
		if($(".panel-body input[type='checkbox']:checked").length == 0){
			Toast.fire({ icon: 'warning', title: "결제방식을 선택해주세요" });
			return false;
		}
	}
	return result;
}

function move_order_next_step(form_id, url){
	$("#" + form_id).attr("action", url);
	$("#" + form_id).submit();
}

/*
주문 뒤로가기 버튼 
*/
$(".order_back").on("click", function(e){
	history.back();
	e.preventDefault();  
});

/*
결제방법 체크방식 변경
*/
$(".payment_method [type=checkbox]").on("click", function(e){
	if($(e.target).prop("checked")){
		$(".payment_method [type=checkbox]").prop("checked", false);
		$(e.target).prop("checked", true);
	}  
});

/*
결제방식 default체크
*/
$("input:checkbox[id='customCheck1']").prop("checked", true); 

/*
주문 등록 
*/
$("#order_create_btn").on("click", function(e){
	ToastConfirm.fire({ icon: 'question', 
				title: "주문하시겠습니까?\n주문 내역을 확인해주세요"}).then((result) => {
				if(result.isConfirmed){
					$("#order_create_form").attr("action", "create_order");
					$("#order_create_form").submit();
				}
			});
	e.preventDefault();  
});

/*
주문 상세 모달 출력 전 데이터 조회 
*/
$('#order_detail_modal').on('show.bs.modal', function(e){
	let o_no = $(e.relatedTarget).data("o_no");
	$.ajax({
		url: "order_detail_rest",
		method: "post",
		async: false,
		data: {"o_no" : o_no},
		dataType: "json",
		headers: {'X-CSRF-TOKEN': $("input[name='_csrf']").val()}, // CSRF 토큰을 헤더에 추가
		success:function(resultObj){
			console.log(resultObj);
			if(resultObj.errorCode > 0){
				let order = resultObj.data;
				let fDate = formatDate(order.o_date);
				let pay = order.o_pay_method == "CoD" ? "Cash on Delevery" : order.o_pay_method;
				$("#orderer_info_body td")[0].innerHTML = order.o_desc;
				$("#orderer_info_body td")[1].innerHTML = fDate;
				$("#orderer_info_body td")[2].innerHTML = order.m_id;
				$("#orderer_info_body td")[3].innerHTML = pay;
				$("#orderer_info_body td")[4].innerHTML = order.o_message;
				$("#receiver_info_body td")[0].innerHTML = order.o_rv_name;
				$("#receiver_info_body td")[1].innerHTML = order.o_rv_phone;
				$("#receiver_info_body td")[2].innerHTML = order.o_rv_address;
				
				let o_sub_price = 0;
				let itemHtmlBuffer = ``;
				order.orderItemList.forEach(function(item, i){
					o_sub_price += parseInt(item.oi_qty) * parseInt(item.product.p_price);
					itemHtmlBuffer += `<tr>
							              <th scope="row"><img style="width:37px;height:37px;" src="img/p_img/${item.product.imageList[0].im_name}" /></th>
							              <td><a href="product_detail?p_no=${item.product.p_no}">${item.product.p_name}</a></td>
							              <td>${item.oi_qty}</td>
							              <td>&#8361;${numberWithCommas(item.oi_qty * item.product.p_price)}</td>
							           </tr>`;
				});
				isShippingPay = o_sub_price < 50000;
				itemHtmlBuffer += `<tr>
						              <th scope="row" colspan="4">total : &#8361;${numberWithCommas(order.o_price) }`;
				if(isShippingPay){
					itemHtmlBuffer += ` (shipping : &#8361;${numberWithCommas(2500)})`;
				}		      
				itemHtmlBuffer += `</th></tr>`;
				$("#item_info_body").html(itemHtmlBuffer);
				$(e.target).find('#order_delete_btn').data('o_no',o_no);
			}else{
				Toast.fire({ icon: 'error', title: resultObj.errorMsg });
			}
		}
	});
});

/*
개별 주문 삭제 
*/
$('#order_delete_btn').on('click',function(e){
	let o_no = $(e.target).data('o_no');
	ToastConfirm.fire({ icon: 'question', 
			title: "주문을 삭제하시겠습니까?"}).then((result) => {
			if(result.isConfirmed){
				let size = $("#order_list_tbody").children().length;
				let count = 0;
				$.ajax({
					url: 'order_item_delete_action_rest',
					method: 'POST',
					data: {'o_no': o_no},
					dataType: 'json',
					headers: {'X-CSRF-TOKEN': $("input[name='_csrf']").val()}, // CSRF 토큰을 헤더에 추가
					success: function(resultObj){
						if(resultObj.errorCode > 0){
							Toast.fire({ icon: 'success', title: resultObj.errorMsg });
							$("#order_detail_modal").modal("hide");
							$(`#order_${o_no}`).fadeOut(500, function(){
								$(this).remove();
							});
							count++;
							if(size == count){
								let htmlBuffer = `<tr>
				                        			<td colspan="5">등록된 주문목록이 없습니다 🙂</td>
				                        		  </tr>`;
								$("#order_list_tbody").html(htmlBuffer);
								$("#order_all_delete_btn").fadeOut();
							}
						} else {
							Toast.fire({ icon: 'error', title: resultObj.errorMsg });
						}
					}
				});
			}
		});
	});

/*
전체 주문 삭제
*/
$('#order_all_delete_btn').on('click',function(e){
	ToastConfirm.fire({ icon: 'question', 
			title: "주문 목록을 모두 삭제하시겠습니까?"}).then((result) => {
			if(result.isConfirmed){
				$.ajax({
					url:'order_all_delete_action_rest',
					method:'POST',
					dataType:'json',
					headers: {'X-CSRF-TOKEN': $("input[name='_csrf']").val()}, // CSRF 토큰을 헤더에 추가
					success:function(resultObj){
						if(resultObj.errorCode > 0){
							Toast.fire({ icon: 'success', title: resultObj.errorMsg });
							$("#order_list_tbody").children().fadeOut(500, function(){
								let htmlBuffer = `<tr>
							            			<td colspan="5">등록된 주문목록이 없습니다 🙂</td>
							            		  </tr>`;
								$("#order_list_tbody").html(htmlBuffer);
							});						
							$("#order_all_delete_btn").fadeOut();
						} else {
							Toast.fire({ icon: 'error', title: resultObj.errorMsg });
						}
					}
				});
			}
		});
});

/*
수취인 정보가 주문자와 동일할 시 정보 입력
*/
$("#reciever_info_check").on("click", function(){
	let status = $(this).prop("checked");
	if(status == true){
		$.ajax({
			url:'member_info_rest',
			method:'POST',
			dataType:'json',
			headers: {'X-CSRF-TOKEN': $("input[name='_csrf']").val()}, // CSRF 토큰을 헤더에 추가
			success:function(resultObj){
				let member = resultObj.data;
				if(resultObj.errorCode > 0){
					$("#full_name").val(member.m_name);
					$("#phone_number").val(member.m_phone);
					$("#o_rv_address").val(member.m_address);
					$("#o_rv_post").val(member.m_post);
				}
			}
		});
	}else{
		$("#full_name").val("");
		$("#phone_number").val("");
		$("#o_rv_address").val("");
		$("#o_rv_post").val("");
	}
});