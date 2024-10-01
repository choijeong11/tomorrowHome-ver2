$(function(){
	$("#check_all").click(); 
});

$("#check_all").on("click", function(e){ 
	let status = e.target.checked; 
	$("tbody [type=checkbox]").prop("checked", status);  
});  

/*
체크박스 상태 변화시 이벤트
*/
$("tbody [type=checkbox]").on("change", function(){
	changeCheckAll();
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
선택된 check Array 반환 
*/
function getSelectedWishListArr(){
	let check_wish_item_array = [];
	$("tbody [type=checkbox]:checked").each(function(i, v){
		check_wish_item_array.push(v);
	});
	console.log(check_wish_item_array);
	return check_wish_item_array;
}

/*
개별 Add to Cart 
*/
$(".wish_add_to_cart_btn").on("click", function(e){
	addToCart({
		"p_no": $(this).data("p_no"),
		"qty": 1
	});
	$(this).blur();
	e.preventDefault();
});

/*
선택 Add to Cart 
*/
$(".wish_item_sel_cart_btn").on("click", function(e){
	let checkArr = getSelectedWishListArr();
	if(checkArr.length == 0){
		Toast.fire({ icon: 'info', title: "선택한 아이템이 없습니다.\n 아이템을 선택해주세요." });
		$(this).blur();
		e.preventDefault();
		return false;
	}
	let p_no_array = [];
	checkArr.forEach(function(v,i){
		p_no_array.push($(v).data("p_no"));
	});
	addToCart({
		"p_no": p_no_array,
		"qty": 1
	});
	$(this).blur();
	$("#check_all").click(); 
	$("#check_all").click(); 
	e.preventDefault();
});

/*
위시 아이템 개별 삭제
*/
$(".wish_item_del_btn").on("click", function(e){
	delete_wish_item($(this).data("w_no"), "one");
	e.preventDefault();
});

/*
위시 아이템 선택 삭제
*/
$(".wish_item_sel_del_btn").on("click", function(e){
	let checkArr = getSelectedWishListArr();
	if(checkArr.length == 0){
		Toast.fire({ icon: 'info', title: "선택한 아이템이 없습니다.\n 아이템을 선택해주세요." });
		$(this).blur();
		e.preventDefault();
		return false;
	}
	let w_no_array = [];
	checkArr.forEach(function(v,i){
		w_no_array.push($(v).data("w_no"));
	});
	delete_wish_item(w_no_array, "select");
	e.preventDefault();
});

/*
위시아이템 삭제 함수
*/
function delete_wish_item(param, type){
	let size = $("tbody").children().length;
	let count = 0;
	$.ajax({
		url: "delete_wishList_rest",
		method: "post",
		traditional: true,
		data: {"param": param},
		dataType: "json",
		headers: {'X-CSRF-TOKEN': $("input[name='_csrf']").val()}, // CSRF 토큰을 헤더에 추가
		success:function(resultObj){
			console.log(resultObj);
			if(resultObj.errorCode > 0){
				if(type == "one"){
					$("#wish_item_" + param).fadeOut(500, function(){
						$(this).remove();
					});
					count++;
				}else{
					param.forEach(function(w_no, i){
						$("#wish_item_" + w_no).fadeOut(500, function(){
							$(this).remove();
						});
						count++;
					});
					$(".wish_item_sel_del_btn").blur();
				}
				if(size == count){
					let htmlBuffer = `<tr>
                                		<td colspan="6">등록된 위시리스트가 없습니다. 위시리스트를 등록해주세요 🙂</td>
                                	  </tr>`;
					$("tbody").html(htmlBuffer);
					$(".back-to-shop").fadeOut(500, function(){
						$(this).remove();
					});
				}
			}else{
				Toast.fire({ icon: 'error', title: resultObj.errorMsg });
			}
		}
	});
}


