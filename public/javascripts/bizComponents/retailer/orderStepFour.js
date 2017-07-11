function initBase(argument) {
	// 关键字查询
	$(document).on('click', '#orderSearchBtn', function() {
		$(".orderList table tbody tr").removeClass('active');
		var sstxt=$('#orderSearch').val();
		var x=$(window).height()/2;
		var index,prev;
		var searched = $(".orderList table tbody tr").filter(":contains('"+sstxt+"')");
		searched.addClass('active');
		if(searched.length!=0){
			var top = searched.eq(0).offset().top;
			$(window).scrollTop(top-x);
		}else {
			return;
		}
	});
	//取消enter默认事件
	$(document).on('keypress','#orderSearch',function(e){
		if(e.keyCode==13){
	    e.preventDefault();
	  }
	});
	//删除当前行
	$(document).on('click','.delbtn',function(){
		if (!confirm('确定删除该行吗？')) {
			return;
		}
		$(this).parents('tr').remove();
	});
	//提交当前数据
	$(document).on('click','#btnSubmit .btnStd',function(){
		//$('.loadingbox').show();
		var orderList = $(".orderList table tbody tr");
		if(orderList.length==0){
			alert('无可提交的订单数据');
			return;
		}
		var orderArr = [];
		$.each(orderList,function(index,obj){
			orderArr.push($(obj).data('order-no'));
		});
		var orderString = orderArr.join(',');
		Tr.post('/retailer/order/generate',{'confirmOrder':orderString},function(data){
			if (data.code == 200) {
				window.location.href = '/retailer/order/list';
			}else if(data.code == 8001){
					//$('.loadingbox').hide();
					var $html = '';
					$.each(data.results, function(index, obj) {
						$html += '<div class="errorItem">' + obj + '</div>';
					});
					$('.errorText').html($html);
					$('.popupError').show();
					var height = $('.popupError').height();
					var top = $(window).height();
					if(height<top-100){
						$('.popupError').css('margin-top',-height/2);
						$('html').css('overflow','hidden');
					}else{
						$('.popupError').css('margin-top',-(top-100)/2);
						$('.errorText').css({'height':top-250,'overflow-y':'auto'});
						$('html').css('overflow','hidden');
					}
					return;
				}else {
					$('.loadingbox').hide();
					alert(data.msg);
				}
		});
	});
	//确认警告
	$(document).on('click','.sureBtn',function () {
		$('.popupError').hide();
		arr = [];
		$('html').css('overflow','auto');
	});	
}

$(function() {
	initBase();
});
