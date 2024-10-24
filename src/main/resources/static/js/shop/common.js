/*
숫자 형식 포맷 메서드
*/
function numberWithCommas(price) { 
	return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ","); 
}

/*
날짜 형식 포맷 메서드
*/
function formatDate(date) { 
	let d = new Date(date),  
		month = '' + (d.getMonth() + 1), 
		day = '' + d.getDate(), 
		year = d.getFullYear(); 
	if (month.length < 2) 
		month = '0' + month; 
	if (day.length < 2) 
		day = '0' + day; 
		
	return [year, month, day].join('-'); 
}

/*
로그인체크  
*/
$(".login_check").on("click", function(e){
	ToastConfirm.fire({ icon: 'question', 
			title: "로그인이 필요한 페이지입니다.\n 로그인 하시겠습니까?"}).then((result) => {
			if(result.isConfirmed){
				location.href = "login_form";
			}
		});
	e.preventDefault();
});

/*
ajax 로그인체크
*/
function check_session(){
	let result = false;
	$.ajax({
		url: "check_session",
		method: "post",
		async: false,
		headers: {'X-CSRF-TOKEN': $("input[name='_csrf']").val()}, // CSRF 토큰을 헤더에 추가
		success:function(resultData){ 
			console.log(resultData);
			if(resultData == "Y"){
				result = true;
			}
		}
	});
	return result;
}

/*
파라미터값 얻기
*/
function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

/*
상품 장바구니 추가
*/
function addToCart(params){
	if(!check_session()){
		ToastConfirm.fire({ icon: 'question', 
				title: "로그인이 필요한 페이지입니다.\n 로그인 하시겠습니까?"}).then((result) => {
				if(result.isConfirmed){
					location.href = "login_form";
				}
			});
		return;
	}
	$.ajax({
		url: "add_cart_rest",
		method: "post",
		traditional: true,
		data: params, 
		dataType: "json",
		headers: {'X-CSRF-TOKEN': $("input[name='_csrf']").val()}, // CSRF 토큰을 헤더에 추가
		success:function(resultObj){
			console.log(resultObj);
			if(resultObj.errorCode > 0){
				Toast.fire({ icon: 'success', title: resultObj.errorMsg });
				// 공통헤더 정보 변경 
				update_common_header_cart_info(resultObj.data);
			}else{
				Toast.fire({ icon: 'error', title: resultObj.errorMsg });
			}
		}
	});
}

/*
상품 위시리스트 추가  
*/
function addToWishList(param){
	if(!check_session()){
		ToastConfirm.fire({ icon: 'question', 
				title: "로그인이 필요한 페이지입니다.\n 로그인 하시겠습니까?"}).then((result) => {
				if(result.isConfirmed){
					location.href = "login_form";
				}
			});
		return;
	}
	$.ajax({
		url: "add_wishList_rest",
		method: "post",
		data: param,
		dataType: "json",
		headers: {'X-CSRF-TOKEN': $("input[name='_csrf']").val()}, // CSRF 토큰을 헤더에 추가
		success:function(resultObj){
			console.log(resultObj);
			if(resultObj.errorCode > 0){
				Toast.fire({ icon: 'success', title: resultObj.errorMsg });
			} else {
				Toast.fire({ icon: 'info', title: resultObj.errorMsg });
			}
		}
	});
}

/*
공통 헤더 카트 주문 등록
*/
$(".cart-area").on("click", "#header_checkout_btn", function(e){
	if($(this).attr("size") == 0){
		Toast.fire({ icon: 'warning', title: "카트에 담긴 상품이 없습니다.\n 카트에 상품을 추가해주세요" });
		e.preventDefault();  
		return false;
	}
	  ToastConfirm.fire({ icon: 'question', 
	          title: "주문을 진행하시겠습니까?"}).then((result) => {
	          if(result.isConfirmed){
             	$("#header_order_create_form").attr("action", "checkout_2");
				$("#header_order_create_form").submit(); // buyType = cart
	          }
       });
	e.preventDefault();  
});

/*
공통 헤더 카트 정보 변경
*/
function update_common_header_cart_info(cartJsonArr){
	let tot_price = 0;
	let item_total = 0;
	let size = cartJsonArr.length;
	let htmlBuffer = "";
	
	// CSRF 토큰
	var csrfHeader = $("input[name='_csrf']").attr("name"); 
	var csrfToken = $("input[name='_csrf']").val();  
	
	$(".cart--btn").html(`<a href="cart_view"><i class="icofont-cart"></i><span class="cart_quantity">${size}</span></a>`);
	htmlBuffer += `<ul class="cart-list">`;
	cartJsonArr.forEach(function(cart, i){
		tot_price += parseInt(cart.product.p_price) * parseInt(cart.c_qty);
		item_total = parseInt(cart.c_qty) * parseInt(cart.product.p_price);
		htmlBuffer += `<li id="header_cart_item_${cart.c_no}">
							<div class="cart-item-desc">
								<a href="product_detail?p_no=${cart.product.p_no}" class="image"> <img
									src="img/p_img/${cart.product.imageList[0].im_name}" class="cart-thumb" alt="">
								</a>
								<div>
									<a href="product_detail?p_no=${cart.product.p_no}">${cart.product.p_name}</a>
									<p>
										${cart.c_qty} x - <span class="price">&#8361;${numberWithCommas(item_total)}</span>
									</p>
								</div>
							</div> <span class="dropdown-product-remove" c_no="${cart.c_no}"><i class="icofont-bin"></i></span>
						</li>`;
		});
		
		let shippingPrice = tot_price < 50000 && tot_price > 0 ? 2500 : 0;
		let all_total = tot_price + shippingPrice;
		
		htmlBuffer += `</ul>
						<div class="cart-pricing my-4">
							<ul>
								<li><span>Sub Total:</span> 
									<span id="header_cart_sub_tot">&#8361;${numberWithCommas(tot_price)}</span> 
								</li>
								<li><span>Shipping:</span> 
									<span id="header_cart_shipping">&#8361;${numberWithCommas(shippingPrice)}</span>
								</li>
								<li><span>Total:</span><span id="header_cart_tot">&#8361;${numberWithCommas(all_total)}</span></li>
							</ul>
						</div>
						<div class="cart-box">
							<a href="" id="header_checkout_btn" class="btn btn-primary d-block" size="${size}">Checkout</a>
							<form id="header_order_create_form" method="post">
								<input type="hidden" name="${csrfHeader}" value="${csrfToken}"/>
								<input type="hidden" name="buyType" value="cart" />
							</form>
						</div>`;
	$(".cart-dropdown-content").html(htmlBuffer);
}

/* 
공통헤더 장바구니 상품 삭제
*/
$(".cart-dropdown-content").on("click", ".dropdown-product-remove", function(e){
	let c_no = $(this).attr("c_no");
	$.ajax({
		url: "cart_remove_rest",
		method: "post",
		traditional: true,
		data: {"param": c_no},
		dataType: "json",
		headers: {'X-CSRF-TOKEN': $("input[name='_csrf']").val()}, // CSRF 토큰을 헤더에 추가
		success:function(resultObj){
			console.log(resultObj);
			if(resultObj.errorCode > 0){
				let cartList = resultObj.data;
				$(`#header_cart_item_${c_no}`).fadeOut(500, function(){
					$(this).remove();
					update_common_header_cart_info(cartList);
				});
				$("#header_checkout_btn").attr("size", cartList.length);
				// 장바구니 페이지에서 삭제를 실행하는 경우
				if($(`#cart_item_${c_no}`).length != 0){
					let size = $("#cart_item_tbody").children().length;
					let count = 0;
					$(`#cart_item_${c_no}`).fadeOut(500, function(){
						$(this).remove();
						$("#check_all").click(); 
						$("#check_all").click(); 
					});
					count++;
					if(size == count){
					let htmlBuffer = `<tr>
                               			<td colspan="7">카트에 등록된 상품이 없습니다. 상품을 등록해주세요 🙂</td>
                               		  </tr>`;
						$("#cart_item_tbody").html(htmlBuffer);
						$(".cart-total-area.mb-30").fadeOut();
						$("#cart_sel_selet_btn").fadeOut();
					}
				}
			}else{
				Toast.fire({ icon: 'error', title: resultObj.errorMsg });
			}
		}
	});
});

/*
주소검색 api
*/
$(".searchAddr").on("click", function(e){
	new daum.Postcode({
	    oncomplete: function(data) {
			console.log(data);
			let addr; 
            if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
                    addr = data.roadAddress == "" ? data.autoRoadAddress : data.roadAddress;
            } else { // 사용자가 지번 주소를 선택했을 경우(J)
                    addr = data.jibunAddress == "" ? data.autoJibunAddress : data.jibunAddress;
            }
	        $(".address").val(addr);
	        $(".postcode").val(data.zonecode);
	    }
	}).open();
});

/*
유효성 검사
*/
$(".phone_number").on("keyup", function(){
	let value = $(this).val();
	if(isNaN(value)){
		Toast.fire({ icon: 'warning', title: "숫자만 입력 가능합니다" });
		$(this).val("");
	}
});

$(".postcode, .address").on("keyup", function(){
	Toast.fire({ icon: 'info', title: "주소검색 기능을 이용해주세요" }).then((result) => {
		$(".searchAddr").trigger("click");
	});
	$(this).val("");
});

/*
alert 세팅
*/
const Toast =  Swal.mixin({ 
	toast: true, 
	position: 'center', 
	showConfirmButton: true, 
	confirmButtonColor: '#3085d6',
	width: '410px'
 });
 
const ToastConfirm =  Swal.mixin({ 
	toast: true, 
	position: 'center', 
	showConfirmButton: true, 
	confirmButtonColor: '#3085d6',
	showDenyButton: true,
	denyButtonText: 'Cancel',
	width: '410px'
 });


/*
related product -> Add To Cart 
*/
$(".related_product_add_cart").on("click", function(e){
	addToCart({
		"p_no":$(this).attr("p_no"),
		"qty": 1
	});
	e.preventDefault();  
});

/*
related product -> Add To WishList 
*/
$(".related_product_add_wishList").on("click", function(e){
	addToWishList({
		"p_no": $(this).attr("p_no")
	});
	e.preventDefault();  
});

/*
header 상품 검색
*/
$("#header_search_form").on("submit", function(e){
	let value = $("#header_search_form .form-control").val().trim();
	if(value == ""){
		Toast.fire({ icon: 'warning', title: "검색어를 입력해주세요" });
		e.preventDefault();  
	}
	sessionStorage.setItem('search', value);
	$(this).attr("action", "product_list");
});

/*
shop메뉴 클릭시 상품검색 정보 삭제 
*/
$(".header_menu").on("click", function(){
	sessionStorage.clear();
});