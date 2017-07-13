CDT = {
	provinceCache: {}
};
var able = 0;
$(window).load(function() {
    $('.flexslider').flexslider({
        animation: "slide",
        controlNav: "thumbnails"
    });
	});
function initBase(){
	$(".memenu").memenu();
	$('.popup-with-zoom-anim').magnificPopup({
        type: 'inline',
        fixedContentPos: false,
        fixedBgPos: true,
        overflowY: 'auto',
        closeBtnInside: true,
        preloader: false,
        midClick: true,
        removalDelay: 300,
        mainClass: 'my-mfp-zoom-in'
    });

    $('.J_Minus').on('click', function(e) {
        $(this).next().next().removeClass('no-plus');
        if ($(this).next()[0].value > 1) {
            $(this).next()[0].value--;
            if (parseInt($(this).next()[0].value) === 1) {
                $(this).addClass('no-minus');
            }
        } else {
            $(this).addClass('no-minus');
        }
    });
    $('.J_Plus').on('click', function(e) {
        var storeNumber = 12;
        $(this).prev().prev().removeClass('no-minus');
        if ($(this).prev()[0].value < storeNumber) {
            $(this).prev()[0].value++;
            if (parseInt($(this).prev()[0].value) === storeNumber) {
                $(this).addClass('no-plus');
            }
        } else {
            $(this).addClass('no-plus');
        }
    });
	//收藏
	$(document).on('click','.b-cartIn',function(){
		$(this).find('span').text('已收藏');
		$(this).find('i').html('&#xe619;');
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
	
}

$(function() {
	initBase();
});
