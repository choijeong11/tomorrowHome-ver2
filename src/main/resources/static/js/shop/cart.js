$(function(){
	$("#check_all").click(); 
});

$("#check_all").on("click", function(e){ 
	let status = e.target.checked; 
	$("tbody [type=checkbox]").prop("checked", status);  
	changeTotal();
});  

/*
체크박스 상태 변화시 이벤트
*/
$("tbody [type=checkbox]").on("change", function(){
	changeCheckAll();
	changeTotal();
});

/*
전체선택 체크박스의 상태를 변경
*/
function changeCheckAll(){
	if($("#check_all").prop("checked")){
		$("#check_all").prop("checked", false);
	}
	let checkboxCount = $("tbody [type=checkbox]").length;
	let checkCount = $("tbody [type=checkbox]:checked").length; 
	if(checkboxCount == checkCount){
		$("#check_all").prop("checked", true);
	}
}

/*
선택한 아이템의 총 금액 합계 변경
*/
function changeTotal(){
	let subTotal = 0;
	let total = 0;
	let shipping = 0;
	$("tbody [type=checkbox]:checked").each(function(i, v){
		let parentTr = $(v).parent().parent();
		if(parentTr.attr("style") == undefined){
			total += parseInt(parentTr.find(".item_total").html().replace(/[^0-9]/g,""));
		}
	});
	if(total < 50000 && total > 0){
		shipping = 2500;
	}
	$("#shipping_pay").html("&#8361;" + numberWithCommas(shipping));
	$("#cart_total").html("&#8361;" + numberWithCommas(parseInt(total) + parseInt(shipping)));
	$("#sub_total").html("&#8361;" + numberWithCommas(parseInt(total)));
}

/*
선택된 아이템의 카트번호 
*/
function getSelectedCartNo(){
	let cart_item_no_array = [];
	$("tbody [type=checkbox]:checked").each(function(i, v){
		cart_item_no_array.push($(v).data("c_no"));
	});
	console.log(cart_item_no_array);
	return cart_item_no_array;
}

/*
선택상품 주문
*/
$("#cart_checkout_btn").on("click", function(e){
	let checkArr = getSelectedCartNo();
	if(checkArr.length == 0){
		Toast.fire({ icon: 'info', title: "선택한 아이템이 없습니다.\n 아이템을 선택해주세요." });
		$(this).blur();
		e.preventDefault();
		return false;
	}
	  ToastConfirm.fire({ icon: 'question', 
	          title: "주문을 진행하시겠습니까?"}).then((result) => {
	          if(result.isConfirmed){
	             	let htmlBuffer = `<input type="hidden" name="buyType" value="cart_select"/>`;
					checkArr.forEach(function(c_no,i){
						htmlBuffer += `<input type="hidden" name="cart_no" value="${c_no}"/>`;
					});
					// buyType="cart_select", cart_no=cart_item_no_array
					$("#cart_checkout_form").append(htmlBuffer);
					$("#cart_checkout_form").attr("action", "checkout_2");
					$("#cart_checkout_form").submit();
	          }
	       });
	e.preventDefault();
});

/*
카트 수량 변경 
*/
$(".quantity input[type='number']").on('change', function(e) {
	var c_no = $(e.target).data('c_no');
	var c_qty = $(this).val();
	$.ajax({
		url : "cart_update_action_rest",
		method : 'POST',
		data: {
			"c_no":c_no,
			"c_qty":c_qty
		},
		headers: {'X-CSRF-TOKEN': $("input[name='_csrf']").val()}, // CSRF 토큰을 헤더에 추가
		success:function(resultObj){
			console.log(resultObj);
			if(resultObj.errorCode > 0){
				let cart = resultObj.data.cart;
				let item_total = numberWithCommas(parseInt(cart.c_qty) * parseInt(cart.product.p_price));
				$(e.target).parent().parent().next().html("&#8361;" + item_total);
				changeTotal();
				update_common_header_cart_info(resultObj.data.cartList);
			}else{
				Toast.fire({ icon: 'error', title: resultObj.errorMsg });
			}
		}
	});
});

/*
카트 개별 삭제 
*/
$('.icofont-close').on("click", function(e) {
	delete_cart_item($(this).data('c_no'), "one");
	e.preventDefault();
});

/*
카트 선택삭제
*/
$("#cart_sel_selet_btn").on("click", function(e) {
	delete_cart_item(getSelectedCartNo(), "select");
});

/*
카트아이템 삭제 함수
*/
function delete_cart_item(param, type){
	let size = $("#cart_item_tbody").children().length;
	let count = 0;
	$.ajax({
		url: "cart_remove_rest",
		method: "post",
		traditional: true,
		data: {"param": param},
		dataType: "json",
		headers: {'X-CSRF-TOKEN': $("input[name='_csrf']").val()}, // CSRF 토큰을 헤더에 추가
		success:function(resultObj){
			console.log(resultObj);
			if(resultObj.errorCode > 0){
				if(type == "one"){
					//let cart = resultObj.cart;
					$("#cart_item_" + param).fadeOut(500, function(){
						$(this).remove();
					});
					count++;
				}else{
					param.forEach(function(c_no, i){
						$("#cart_item_" + c_no).fadeOut(500, function(){
							$(this).remove();
						});
						count++;
					});
					$("#cart_sel_selet_btn").blur();
				}
				changeTotal();
				update_common_header_cart_info(resultObj.data);
				if(size == count){
					let htmlBuffer = `<tr>
                               			<td colspan="7">카트에 등록된 상품이 없습니다. 상품을 등록해주세요 🙂</td>
                               		  </tr>`;
					$("#cart_item_tbody").html(htmlBuffer);
					$(".cart-total-area.mb-30").fadeOut();
					$("#cart_sel_selet_btn").fadeOut();
				}
			}else{
				Toast.fire({ icon: 'error', title: resultObj.errorMsg });
			}
		}
	});
}

