function initBase() {
	//批发价
	$('.title_o').on('tap',function(){
		$('.price_container_o').show().next().hide();
		$(this).addClass('active').next().removeClass('active');
		$('#hidType').val('more');
		totalPrice();
		rangePrice();
	});
	//一件代发
	$('.title_t').on('tap',function(){
		$('.price_container_t').show().prev().hide();
		$(this).addClass('active').prev().removeClass('active');
		$('#hidType').val('one');
		totalPrice();
		remove();
	});
	//选择规格
	$('.typeBlock ul li').on('tap',function(){
		$(this).find('div').addClass('active');
		$(this).siblings().find('div').removeClass('active');
		var colorTxt = $(this).find('img').eq(0).data('color');
		if(colorTxt) {
			$('#colorsel').text(colorTxt);
		}
	});
	//加入购物车
	// $(document).on('tap','.submit-box',function(){
	// 	var num = parseInt($("#qnty input").val());
	// 	if(!num||num==0){
	// 		alert('请选择商品！');
	// 	}
	// });
	//点击banner图片发大
	$('.item').on('click',function(){
		$('.bigbanner').show();
		$('.mask').show();
		var swipe = $('.banner_slider').eq(0).clone();
	  var index = $(this).index();   
	  $('.banners').html(swipe);
	  var mySwiper = new Swiper ('.bigbanner .swiper-container', {
    // 如果需要分页器
	    pagination: '.bigbanner .swiper-pagination',
	    initialSlide :index,
	    width : window.innerWidth,
	  });
	});
	//收起放大图片
	$('.bigbanner').click(function(){
		$(this).hide();
		$('.mask').hide();
	});
	//返回顶部
	$(window).scroll(function(){
    if ($("body").scrollTop()>1000){
       $(".u-backtop").show(500);
    }
    else{
       $(".u-backtop").hide(500);
    }
	});
}
//选择数量
//点击减按钮
function minus() {
	var id = 0;
	var mid = 0;
	$('#minus').bind({
		tap: function() {
			var num = parseInt($("#qnty input").val());
			num--;
			$("#qnty input").val(num);
			$("#plus").css("color", "#F4C301");
			if (parseInt($("#qnty input").val()) <= 0 || isNaN($("#qnty input").val())) {
				$("#qnty input").val(0);
			}
			if (parseInt($("#qnty input").val()) <= 0) {
				$("#minus").css("color", "#bebebe");
			}
			totalPrice();
			if($('.title_o').hasClass('active')){
				rangePrice();
			}
		}
	});
}
//点击加按钮
function plus() {
	var id = 0;
	var mid = 0;
	$('#plus').bind({
		tap: function() {
			var num = parseInt($("#qnty input").val());
			num++;
			if (num > parseInt($('#commInventory').text())) {
				//Tr.prompt($('.alertBlock'), '库存不足!');
				return;
			}
			$("#qnty input").val(num);
			if (num > parseInt($('#commInventory').text())-1) {
				$("#plus").css("color", "#bebebe");
			}
			$("#minus").css("color", "#F4C301");
			totalPrice();
			if($('.title_o').hasClass('active')){
				rangePrice();
			}
			
		}
	});
}
//控制按钮与手动输入
function numChange() {
	if (parseInt($("#qnty input").val()) > 1) {
		$("#minus").css("color", "#F4C301");
	}
	var reg = /^[0-9]+$/;
	$("#qnty input").on('keyup',function(){
		if(!reg.test($("#qnty input").val())){
				$("#qnty input").val('');
		}else{
			$("#minus").css("color", "#F4C301");
			totalPrice();
			if($('.title_o').hasClass('active')){
				rangePrice();
			}
		}
	});
	$("#qnty input").on('blur',function(){
		if(!reg.test($("#qnty input").val())){
				$("#qnty input").val(0);
		}else{
			$("#minus").css("color", "#F4C301");
			totalPrice();
			if($('.title_o').hasClass('active')){
				rangePrice();
			}
		}
	});
	$("#qnty input").on('change',function(){
		if(!reg.test($("#qnty input").val())){
				$("#qnty input").val(0);
		}else{
			$("#minus").css("color", "#F4C301");
			totalPrice();
			if($('.title_o').hasClass('active')){
				rangePrice();
			}
		}
	});
	$('body').on('tap',function(e) {
		if($("#qnty input").val()==''|| !$("#qnty input").val()){
			if (!$(e.srcElement).is('#qnty input *') || !$(e.target).is('#qnty input *') || !e.srcElement) {
				$("#qnty input").val(0);
				totalPrice();
				if($('.title_o').hasClass('active')){
					rangePrice();
				}
			}
		}
	});
	$("#qnty input").keyup(function(){
		var num = parseInt($("#qnty input").val());
			if (num > parseInt($('#commInventory').text())) {
				$("#qnty input").val(parseInt($('#commInventory').text()));
				$("#minus").css("color", "#F4C301");
				$("#plus").css("color", "#bebebe");
			}
	});
	minus();
	plus();
}
//价格处理
function totalPrice(){
	var doType = $('#hidType').val();
	var num = parseInt($('#qnty input').val());
	var totalPrice = 0;
	//---------------------- 批发价格处理
	var $item = $('.price_item');
	if($item&&doType=="more"){
		$.each($item,function(index,object){
			var range = parseInt($(object).data('range')),
					price = parseInt($(object).data('price'));
			if(num<=range||index==$item.length-1){
				totalPrice = num*price;
				return false;
			}
		});
	}// -------------------- 一件代发价格处理
	else{
		var $one = $('.price_t');
		if($one){
			var price = parseInt($('.price_t').text());
			totalPrice = num*price;
		}
	}
	// 要做处理()总价等于0的时候
	$('.priceContainer').text(totalPrice);
}
function rangePrice(){
	//小于起批量处理
	var ranges = $('.price_item').eq(0).data('range'),
			number = parseInt($('#qnty input').val());
	if(number&&ranges&&number<ranges){
		$('.range_error').text('* 数量低于商家最低起批规则');
		$('.submit-box').css({'background-color':'#aaa','border':'1px solid #aaa'});
		$('.totalPrice').hide();
		return;
	}else {
		$('.range_error').text('');
		$('.submit-box').css({'background-color':'#ffa200','border':'1px solid #ffa200'});
		$('.totalPrice').show();
	}
}
function remove(){
	$('.range_error').text('');
	$('.submit-box').css({'background-color':'#ffa200','border':'1px solid #ffa200'});
	$('.totalPrice').show();
}
$(function(){
	initBase();
	numChange();
	//滑动图片
	var mySwiper = new Swiper ('.banner .swiper-container', {
    pagination: '.banner .swiper-pagination'
  });  
  if($('.price_item').length>0){
  	$('#hidType').val('more');
  }else {
  	$('#hidType').val('one');
  }
})