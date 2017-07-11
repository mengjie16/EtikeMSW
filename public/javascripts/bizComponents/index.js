
$(function() {
	$('.depth2_new').show();
  $('#gnb1').hover(function() {
    $('.depth2_new').show();
  }, function() {
    $('.depth2_new').show();
  });
  var unslider = $('.main_visual').unslider({
    speed: 500,               //  滚动速度
    delay: 5000,              //  动画延迟
    complete: function() {},  //  动画完成的回调函数
    keys: false,               //  启动键盘导航
    dots: true,               //  显示点导航
    fluid: false,              //  支持响应式设计
    arrows: true,
    loop:true,
    prev: '←', // 上一页按钮的文字 (string)
    next: '→', // 下一页按钮的文字
  });
  $('.btn_next').click(function(){
		var $lis = $('#koost_area_index>li');
		$('#koost_area_index>li:lt(4)').insertAfter($lis.last());
  });
  $('.btn_prev').click(function(){
		var $lis = $('#koost_area_index>li');
	  $('#koost_area_index>li:gt(-5)').insertBefore($lis.first());
  });
  $('.navLink').addClass('active');
  function animate(){
    $('.shop_li').animate({
      top: '-103px'
    }, 3000, function() {
      var $lis = $('.shop_li>li');
      $lis.eq(0).insertAfter($lis.last());
      $('.shop_li').css('top', 0);
    });
  }
  var animation = false;
  $(window).scroll(function(){
    if($(window).scrollTop()>($('.shop_li').offset().top+$('.shop_li').outerHeight())||($(window).scrollTop()+$(window).height())<$('.shop_li').offset().top){
    animation = false;
  }else{
    animate();
    animation = true;
  }
  if(animation){
    var timer = setInterval(function() {
      $('.shop_li').animate({
        top: '-103px'
      }, 3000, function() {
        var $lis = $('.shop_li>li');
        $lis.eq(0).insertAfter($lis.last());
        $('.shop_li').css('top', 0);
        if(!animation){
          clearInterval(timer);
          $('.shop_li').css('top', 0);
        }
      });
    }, 3000);
  }
  });
  
  
});
window.onload = function(){
  grayscale($('.grayscale'));
  $('.grayscale').hover(function(){
      grayscale.reset(this);
  }, function(){
      grayscale(this);
  });
  //grayscale.prepare($('.grayscale'));
};

function tapFx(i, n, t) {
    $(i + n).find('li').find('img.lazy').each(function () {
        if ($(this).attr('data-src') != undefined) {
            var dImage = $(this).attr('data-src');
            $(this).attr('src', dImage).removeAttr('data-src');
        }
    });               
    $(i).find("li").eq(n).addClass("on").siblings("li").removeClass("on");
    if (t != undefined && t == "img") {
        $(i).children("li").not(".on").each(function () {
            $(this).children("a").children("img").attr("src", $(this).children("a").children("img").attr("offimg"));
        });
        $(i).children("li").eq(n).children("a").children("img").attr("src", $(i).children("li").eq(n).children("a").children("img").attr("onimg"));
    };
    $(i + n).show().siblings("div").hide();
};
function tapFx2(i, n, t) {
    // 동적 이미지 로딩 처리 (영환 140602)
    jQuery(i + n).find('li').find('img.lazy').each(function () {
        if ($(this).attr('data-src') != undefined) {
            var dImage = $(this).attr('data-src');
            $(this).attr('src', dImage).removeAttr('data-src');
        }
    }); 
    $(i).find("li").eq(n).addClass("on").siblings("li").removeClass("on");
    if (t != undefined && t == "img") {
        $(i).children("li").not(".on").each(function () {
            $(this).children("a").children("img").attr("src", $(this).children("a").children("img").attr("offimg"));
        });
        $(i).children("li").eq(n).children("a").children("img").attr("src", $(i).children("li").eq(n).children("a").children("img").attr("onimg"));
    };
    $(i + n).show().siblings("div").hide();
};
$(window).resize(function() {

});
