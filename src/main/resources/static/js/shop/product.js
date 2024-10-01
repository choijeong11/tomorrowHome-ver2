/*
quickview modal 보여지기 전 상품데이터 조회
*/
$('#quickview').on('show.bs.modal', function(e){
	console.log(e.relatedTarget);
	let p_no = $(e.relatedTarget).data("p_no");
	$.ajax({
		url: "product_detail_rest",
		method: "get",
		async: false,
		data: {"p_no" : p_no},
		dataType: "json",
		headers: {'X-CSRF-TOKEN': $("input[name='_csrf']").val()}, // CSRF 토큰을 헤더에 추가
		success:function(resultObj){
			console.log(resultObj);
			if(resultObj.errorCode > 0){
				let product = resultObj.data;
				$(e.target).find('.badge-new').html(product.p_concept);
				$(e.target).find('.title').html(product.p_name);
				$(e.target).find('.price').html("&#8361; " + numberWithCommas(product.p_price));
				$(e.target).find('.p_desc').html(product.p_desc);
				let scoreBuffer = "";
				for(let i = 0; i < product.p_avg_score; i++){
					scoreBuffer += `<i class="fa fa-star" aria-hidden="true"></i>`;
				}
				if(scoreBuffer == "") scoreBuffer = "등록된 리뷰가 없습니다";
				$(e.target).find('.top_seller_product_rating.mb-15').html(scoreBuffer);
				$(e.target).find('.p_detail_link').on("click", function(e){
					location.href = "product_detail?p_no=" + p_no;
					e.preventDefault();
				});
				$(e.target).find('#p_no').val(p_no);
				$(e.target).find('.q_view_to_wish_btn').data("p_no", p_no);
				$(e.target).find(".first_img").attr("src", `img/p_img/${product.imageList[0].im_name}`);
				$(e.target).find(".hover_img").attr("src", `img/p_img/${product.imageList[1].im_name}`);
			} else {
				Toast.fire({ icon: 'error', title: resultObj.errorMsg });
			}
		}
	});
});

/*
list -> Add to Cart 
*/
$(".row.justify-content-center").on("click", ".list_add_to_cart_btn", function(e){
	addToCart({
		"p_no": $(e.target).data("p_no"),
		"qty": 1
	});
	$(this).blur();
	e.preventDefault();
});

/*
detail -> Add to Cart 
*/
function detail_addToCart(e){
	let params = $("#addcart_and_buynow_form").serialize(); // p_no, qty
	$(e.target).blur();
	addToCart(params);
}

/*
quick view -> Add to Cart 
*/
$("#q_view_cart_add_form").on("submit", function(e){
	let params = $(this).serialize(); // p_no, qty
	addToCart(params);
	$(".cart-submit").blur();
	e.preventDefault();
});

/*
list -> Add to wish
*/
$(".row.justify-content-center").on("click", ".list_to_wish_btn", function(e){
	addToWishList({
		"p_no": $(this).data("p_no"),
	});
	$(this).blur();
	e.preventDefault();
});

/*
detail -> Add to wish
quick view -> Add to wish
*/
$(".q_view_to_wish_btn, .add_to_wishlist").on("click", function(e){
	addToWishList({
		"p_no": $(this).data("p_no"),
	});
	$(this).blur();
	e.preventDefault();
});

/*
상품 바로 구매
*/
function buyNowProduct(){
	if(!check_session()){
		ToastConfirm.fire({ icon: 'question', 
				title: "로그인이 필요한 페이지입니다.\n 로그인 하시겠습니까?"}).then((result) => {
				if(result.isConfirmed){
					location.href = "login_form";
				}
			});
		return;
	}
	ToastConfirm.fire({ icon: 'question', 
				title: "주문을 진행하시겠습니까?"}).then((result) => {
				if(result.isConfirmed){
					$("#addcart_and_buynow_form").attr("action", "checkout_2");
					$("#addcart_and_buynow_form").submit(); // p_no, qty, buyType(direct)
				}
			});
}

/*
조건검색 체크방식 변경
*/
let conditionArr = ["p_category", "p_color", "p_brand", "p_score"];
conditionArr.forEach(function(v, i){
	$(`.widget-desc.${v} [type=checkbox]`).on("click", function(e){
		if($(e.target).prop("checked")){
			$(`.widget-desc.${v} [type=checkbox]`).prop("checked", false);
			$(e.target).prop("checked", true);
		}
	});
});

/*
조건 선택시 이벤트 
*/
$(".widget-desc, #sortBy").on("change", function(e){
	changeProductList();
});

$( ".slider-range" ).on( "slidechange", function( event, ui ) {
	changeProductList();
} );

/*
상품 리스트 변경
*/
function changeProductList(pageno){
	console.log(pageno);
	let checkJson = setCheckCondition();
	if(pageno != undefined || pageno != null){
		checkJson.pageno = pageno;
	}
	$.ajax({
		url: "product_list_rest",
		method: "get",
		data: $.param(checkJson),
		dataType: "json",
		headers: {'X-CSRF-TOKEN': $("input[name='_csrf']").val()}, // CSRF 토큰을 헤더에 추가
		success:function(resultObj){  
			console.log(resultObj.data);
			if(resultObj.errorCode > 0){
				let data = resultObj.data;
				if(data.itemList.length != 0){
					let htmlBuffer = "";
					data.itemList.forEach(function(product, i){
						console.log(product);
						let price = numberWithCommas(product.p_price);
						//let price = product.p_price;
						let brand = product.p_brand == null ? "no brand" : product.p_brand;
						htmlBuffer += `<div class="col-9 col-sm-12 col-md-6 col-lg-4">
		                                <div class="single-product-area mb-30">
		                                    <div class="product_image">
		                                        <!-- Product Image -->
		                                        <img class="normal_img" src="img/p_img/${product.imageList[0].im_name}" alt="">
		                                        <img class="hover_img" src="img/p_img/${product.imageList[1].im_name}" alt="">
		                                        <!-- Product Badge -->
		                                        <div class="product_badge">
		                                            <span>${product.p_concept}</span>
		                                        </div>
		                                        <!-- Wishlist -->
		                                        <div class="product_wishlist">
		                                            <a href="" class="list_to_wish_btn" data-p_no="${product.p_no}"><i class="icofont-heart"></i></a>
		                                        </div>
		                                    </div>
		                                    <!-- Product Description -->
		                                    <div class="product_description">
		                                        <!-- Add to cart -->
		                                        <div class="product_add_to_cart">
		                                            <a href="#" class="list_add_to_cart_btn" data-p_no="${product.p_no}"><i class="icofont-shopping-cart"></i> Add to Cart</a>
		                                        </div>
		                                        <!-- Quick View -->
		                                        <div class="product_quick_view">
		                                            <a href="#" data-toggle="modal" data-target="#quickview" data-p_no="${product.p_no}"><i class="icofont-eye-alt"></i> Quick View</a>
		                                        </div>
		                                        <p class="brand_name">${brand}</p>
		                                        <a href="product_detail?p_no=${product.p_no}" data-p_no="${product.p_no}">${product.p_name}</a>
		                                        <h6 class="product-price">&#8361;${price}<span></span></h6>
		                                    </div>
		                                </div>
		                            </div>`;
		                    
					});
					$(".row.justify-content-center").html(htmlBuffer);
					
					let paginationBuffer = "";
					if(data.pageMaker.prevPage > 0){
						paginationBuffer += `<li class="page-item">
			                                    <button class="page-link" onclick="changeProductList(${data.pageMaker.prevPage});"><i class="fa fa-angle-left" aria-hidden="true"></i></button>
			                               	 </li>`;
					}
					for(let no = data.pageMaker.blockBegin; no <= data.pageMaker.blockEnd; no++){
						if(data.pageMaker.curPage == no){
							paginationBuffer += `<li class="page-item active"><button class="page-link" href="#">${no}</button></li>`;
						}
						if(data.pageMaker.curPage != no){
							paginationBuffer += `<li class="page-item"><button class="page-link" onclick="changeProductList(${no});">${no}</button></li>`;
						}
					}
					if(data.pageMaker.curPage < data.pageMaker.totPage){
						paginationBuffer += `<li class="page-item">
						                        <button class="page-link" onclick="changeProductList(${data.pageMaker.nextPage});"><i class="fa fa-angle-right" aria-hidden="true"></i></button>
					                    	 </li>`;
					}
					$(".pagination.pagination-sm.justify-content-center").html(paginationBuffer);
				}else{
					$(".row.justify-content-center").html(`<p>해당 조건의 상품이 존재하지 않습니다 😢</p>`);
					$(".pagination.pagination-sm.justify-content-center").html("");
				}
			}else{
				Toast.fire({ icon: 'error', title: resultObj.errorMsg });
			}
		}
	});
}
	
/*
All Reset 선택시 이벤트
*/
$("#all_reset_btn").on("click", function(e){
	sessionStorage.clear();
	location.href = "product_list?pageno=1";
});

/*
선택 조건값을 저장 
*/
function setCheckCondition(){
	let p_category = $(".widget-desc.p_category [type=checkbox]:checked").data("cg_no");
	let p_color = $(".widget-desc.p_color [type=checkbox]:checked").next().text().toLowerCase().trim();
	let p_brand = $(".widget-desc.p_brand [type=checkbox]:checked").next().text().toLowerCase().trim();
	let p_score = $(".widget-desc.p_score [type=checkbox]:checked").data("score");
	let priceRangeArr = $(".range-price").text().split(":")[1].split("-"); 
	let lowPrice = priceRangeArr[0].replace(/[^0-9]/g,"").trim();
	let highPrice = priceRangeArr[1].replace(/[^0-9]/g,"").trim();
	let sortBy = $("#sortBy").val();
	let search = $("#header_search_form .form-control").val().trim();
	
	let checkJson = {}; 
	checkJson.cg_no = p_category; 
	checkJson.color = p_color; 
	checkJson.brand = p_brand; 
	checkJson.score = p_score; 
	checkJson.lowPrice = lowPrice; 
	checkJson.highPrice = highPrice; 
	checkJson.sortBy = sortBy; 
	checkJson.search = search; 
	checkJson.pageno = $(".page-item.active > a").text() == "" ? "1" : $(".page-item.active > a").text();
	console.log(checkJson); 
	
	return checkJson; 
}

/*
main에서 접근시 선택 카테고리 정보 세팅,
상품 검색 시 검색 정보 세팅
*/
$(() => {
	let cg_no = getParameterByName("cg_no");
	if(cg_no){
		$(`.widget-desc.p_category input:checkbox[id='customCheck${cg_no}']`).prop("checked", true);
	}
	let search = sessionStorage.getItem("search");
	if(search){
		$(".search-btn.header").click();
		$("#header_search_form .form-control").val(search);
	}
});

