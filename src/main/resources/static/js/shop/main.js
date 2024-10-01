$(function(){
	setInterval(function(){
		get_now_time();
	}, 1000);
});

function get_now_time(){
	$.ajax({
		url: "get_now_time",
		method: "get",
		success:function(resultData){ 
			let timeArr = resultData.split("-");
			$(".main_time .month").html(timeArr[1]);
			$(".main_time .day").html(timeArr[2]);
			$(".main_time .hours").html(timeArr[3]);
			$(".main_time .minutes").html(timeArr[4]);
			$(".main_time .seconds").html(timeArr[5]);
		}
	});
}

/*
main -> add To Cart 
*/ 
$(".main_add_to_cart").on("click", function(e){
	addToCart({
		"p_no": $(this).data("p_no"),
		"qty": 1
	});
	$(this).blur();
	e.preventDefault();
});

/*
main -> add To Wish 
*/ 
$(".main_add_to_wish").on("click", function(e){
	addToWishList({
		"p_no": $(this).data("p_no"),
	});
	$(this).blur();
	e.preventDefault();
});