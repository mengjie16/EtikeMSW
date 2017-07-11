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
	//导出excel弹框
	$(document).on('click','#exportExcelbtn',function () {
		Tr.popup('exportExcel');
	});
	//关闭弹框
	$('.wnd_Close_Icon').click(function() {
		$(this).parents('.popWrapper').hide();
		$('html').removeClass('overflow-hidden');
	});
	$(document).on('click','#btnExport',function () {
		var chk_value =[]; 
		$('input[name="excel_item"]:checked').each(function(){ 
			chk_value.push($(this).val()); 
		}); 
		var excelString = chk_value.join(',');
		var tradeId = $('input[name=trade]').val();
		if(excelString&&tradeId){
			$('#btnExport').attr('href','/retailer/order/excel?tradeId=' + tradeId + '&columns='+excelString).css('background-color','#ffce00');
			$(this).parents('.popWrapper').hide();
			$('html').removeClass('overflow-hidden');
		}	
	});
	//默认全部选中
	$('input[name="excel_item"]').attr('checked',true);
	//点击选中checkbox
	$(document).on('change','input[name="excel_item"]',function () {
		var chk_value =[]; 
		$('input[name="excel_item"]:checked').each(function(){ 
			chk_value.push($(this).val()); 
		}); 
		var excelString = chk_value.join(',');
		var tradeId = $('input[name=trade]').val();
		if(excelString&&tradeId){
			$('#btnExport').attr('href','/retailer/order/excel?tradeId=' + tradeId + '&columns='+excelString).css('background-color','#ffce00');
		}else {
			$('#btnExport').attr('href','javascript:void(0)').css('background-color','#bbb');
		}
	});
}

$(function() {
	initBase();
});