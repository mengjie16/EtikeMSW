CDT = {
	provinceCache: {}
};
var able = 0;
function initBase(){
	// $('#gnb1').hover(function() {
	// 	$('.depth2_new').show();
	// }, function() {
	// 	$('.depth2_new').hide();
	// });
	$('.sort-box').hover(function() {
		$(this).parent().find('ul').toggle();
	});
	$('.sort-box ul li a').click(function() {
		$('.current-sort').text($(this).text());
	});
	//切换图片
	$(document).on('click','.chooseimg',function(){
		var src = $(this).find('img').data('src');
		$('.chooseimg img').removeClass('active');
		$(this).find('img').addClass('active');
		$('.boriContPhoto img').attr('src',src+'?imageView2/2/w/350/q/100');
		$('.imgArea img').attr('src',src);
		$('.largeViewBody ul li img').attr('src',src+'?imageView2/2/w/50/q/100');
	});
	//价格切换
	//显示批发价
	$(document).on('click','.changeBtn',function(){
		$('.priceTable').show().next().hide();
		$('.priceTable').addClass('active');
		choosePriceMath();
	});
	//显示代销价
	$(document).on('click','.replaceBtn',function(){
		$('.priceTable').hide().next().show();
		$('.priceTable').removeClass('active');
		choosePriceMath();
	});
	//收藏
	$(document).on('click','.b-cartIn',function(){
		$(this).find('span').text('已收藏');
		$(this).find('i').html('&#xe619;');
	});
	// 去url锚点
	$(window).scroll(function() {
		var nheight = $('#detail_tab1_link').offset().top;
		if ($("body").scrollTop() < nheight) {
			var pathname = window.location.pathname;
			window.history.pushState(null, null, pathname);
		}
	});
	//加入购物车弹出弹框
	$(document).on('click','.b-buyNow',function(){
		if(able==0){
			var num = 0;
			$('.qnty').each(function(){ 
		    if($(this).val() !=''){ 
		     num += parseInt($(this).val()); 
		    } 
		  }); 
		  if(!num){
		  	alert('请选择商品！');
		  	return;
		  }
			$('.alertCart').show();
		}
		//$('html').addClass('overflow-hidden');
	});
	//关闭加入购物车弹框
	$(document).on('click','.closeBtn_a',function(){
		$('.alertCart').hide();
		//$('html').removeClass('overflow-hidden');
	});
	$(document).on('click','.long-buy',function(){
		$('.alertCart').hide();
		//$('html').removeClass('overflow-hidden');
	});
	//放大图片
	$(document).on('click','.boriMorebtn',function(){
		$('.largeViewLayer').css('display', 'block');
	});
	//关闭图片
	$(document).on('click','.closeBtn',function(){
		$(this).parent().css('display', 'none');
	});
	//点击选择省份
	$(document).on('click','.choosePro',function(){
		$('.provinceList').show();
		$(this).parent().addClass('active');
	});
	//关闭省份弹框
	$(document).on('click','.iconclosebtn',function(){
		$('.provinceList').hide();
		$('.provinceBox').removeClass('active');
	});
	$('body').on('click',function(e) {
		if (!$(e.srcElement).is('.provinceBox *') || !$(e.target).is('.provinceBox *') || !e.srcElement) {
			$('.provinceList').hide();
			$('.provinceBox').removeClass('active');	
		}
	});
	//清空省份
	$(document).on('click','.emptyPro',function(){
		$('.freightprice').text('按实际收费');
		$('.provinceList').hide();
		$('.provinceBox').removeClass('active');	
	});
	//选择省份
	$(document).on('click','.provinceList li',function(){
		var price = $(this).data('price');
		if(price==0){
			$('.freightprice').text('免运费');
		}else if(!price||price==-1){
			$('.freightprice').text('按实际收费');
		}else{
			$('.freightprice').text('￥'+price);
		}
		var province = $(this).text();
		$('.choosePro span').text(province);
		$('.provinceList').hide();
		$('.provinceBox').removeClass('active');	
	});
}
// 批发价格计算价格
function totalPrice(){
	// 无价格计算
	if(!$('.pricerange')||$('.pricerange').length<=0){
		// 这里也是要处理的，如果连价格都没有，就应该不显示购物车功能
		return;
	}
	//小于起批量处理
	var number = 0,
		totalPrice = 0,
		needPrice = 0;
	// 计算当前数量总数
	$('.qnty').each(function(){ 
	    if($(this).val() !=''){ 
	     number += parseInt($(this).val()); 
	    } 
  	});
  	// 价格区间抉择
	var $itemPrices = $('.pricerange');
	var check = false;
	$.each($itemPrices,function(index,object){
		var range = parseInt($(object).data('range')),
			price = parseInt($(object).data('price'))/100;
		// 找到对应价格区间的价格
		if(index == 0 && number < range){
			// 如果小于起批量，当前不计算
		  check = true;
			return;
		}
		// 如果没有下一个区间的价格了，取当前区间价格
		if($itemPrices.length==(index+1)){
			needPrice = price;
			check = false;
			return;
		}
		var $nextRange = $itemPrices[index+1];
		if(!$nextRange){
			return;
		}
		if(number<=$($nextRange).data('range')){
			needPrice = price;
			check = false;
		}
	});
	if(check){
		$('.errortext').show().text('* 数量低于商家最低起订规则');
		$('.total-txt').hide().next().hide();
		$('.b-buyNow').css({'background-color':'#aaa','border':'1px solid #ccc'});
		return;
	}

	totalPrice = number*needPrice;
  	// 无计算资格
  $('.total-txt').show().next().show();
  $('.errortext').hide();
	$('#TotalPrice').text(totalPrice);
	$('.b-buyNow').css({'background-color':'#FFC907','border':'1px solid #F7B300'});
	
}
// 代销计算
function disPrice() {
	var price = parseInt($('#disPrice').text()),
			number = 0;
	// 无价格计算
	if(!price||isNaN(price)){
		return;
	}
	$('.qnty').each(function(){ 
    if($(this).val() !=''){ 
     number += parseInt($(this).val()); 
    } 
  }); 
	$('#TotalPrice').text(price*number);
}
//减号点击
function jian() {
	$('.minus').bind({
		click: function() {
			var $me = $(this);
			var $parent = $(this).parents('.sku_line');
			var num = parseInt($parent.find(".qnty").val());
			num--;
			$parent.find(".qnty").val(num);
			$parent.find(".plus b").css("border-bottom-color", "#666");
			if (parseInt($parent.find(".qnty").val()) < 0 || isNaN($parent.find(".qnty").val())) {
				$parent.find(".qnty").val(0);

			}
			if (parseInt($parent.find(".qnty").val()) <= 0) {
				$parent.find(".minus b").css("border-top-color", "#bebebe");
			}else{
				choosePriceMath();
			}
			
		},
	});
	
}
// 加号点击
function jia() {
	$('.plus').bind({
		click: function() {
			var $me = $(this);
			var $parent = $(this).parents('.sku_line');
			var num = parseInt($parent.find(".qnty").val());
			num++;
			if (num > parseInt($parent.find('.commInventory').text())) {
				//Tr.prompt($('.alertBlock'), '库存不足!');
				$parent.find(".plus b").css("border-bottom-color", "#bebebe");
				return;
			}
			$parent.find(".qnty").val(num);
			if (num > parseInt($parent.find('.commInventory').text())-1) {
				$parent.find(".plus b").css("border-bottom-color", "#bebebe");
			}
			$parent.find(".minus b").css("border-top-color", "#666");

			// 选择价格计算方式
			choosePriceMath();
		},
	});
	
}
// 选择价格计算方式
function choosePriceMath(){
	var number=0;
	$('.qnty').each(function(){ 
    if($(this).val() !=''){ 
    	number += parseInt($(this).val()); 
    } 
	});
	var range = parseInt($('.pricerange:first').data('range'));
	// 两个价格都有
	if($('.priceTable .pricerange').length>0&&$('.replacePrice').length>0){
		if(number>=range){
			totalPrice();
		}else {
			disPrice();
		}
	}else if($('.priceTable .pricerange').length>0){
		totalPrice();
	}else if($('.replacePrice').length>0){

		disPrice();
	}
}
// 宝贝数量变化
function numChange() {

	// 数量变化 加或减号图标颜色变化
	if (parseInt($(".qnty").val()) > 1) {
		$(".minus b").css("border-top-color", "#666");
	}

	var reg = /^[0-9]+$/;
	$(".qnty").keyup(function(){
		if(!reg.test($(this).val())){
				$(this).val('');
		}else {
			choosePriceMath();
		}
	});

	$('body').on('click',function(e) {
		$('.qnty').each(function(){
			var $parent = $(this).parents('.sku_line');
			$(this).val(parseInt($(this).val()));
			if($(this).val()>0){
				$parent.find(".minus b").css("border-top-color", "#666");
			}
			if($(this).val()==''|| !$(this).val()){
				if (!$(e.srcElement).is('.qnty *') || !$(e.target).is('.qnty *') || !e.srcElement) {
					$(this).val(0);
					choosePriceMath();
				}
			}
		});
	});
	$(".qnty").keyup(function(){
		var $me = $(this);
		var $parent = $(this).parents('.sku_line');
		var num = parseInt($me.val());
		if (num > parseInt($parent.find('.commInventory').text())) {
			$me.val(parseInt($parent.find('.commInventory').text()));
			$parent.find(".minus b").css("border-top-color", "#666");
			$parent.find(".plus b").css("border-bottom-color", "#bebebe");
		}
	});
	jian();
	jia();
}
$(function() {
	initBase();
	numChange();
});
