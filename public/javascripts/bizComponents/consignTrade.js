CDT={
	keydown:false
}
function initBase() {
	$('.op_express_delivery_dropdown_container').load('/expressList');
	//选择快递
	$(document).on('click','.c-dropdown2-btn',function(){
		$('.c-dropdown2-menu').hide();
		$(this).parents('.c-dropdown2').find('.c-dropdown2-menu').show();
	});
	$(document).on('hover','.c-dropdown2-option',function(){
		$('.c-dropdown2-option').removeClass('c-dropdown2-selected')
		$(this).addClass('c-dropdown2-selected');
	});
	$(document).on('click','.c-dropdown2-option',function(){
		var $parent = $(this).parents('.c-dropdown2');
		$parent.find('.c-dropdown2-menu').hide();
		$parent.find('.c-dropdown2-btn').html($(this).html());
		$parent.find('.c-dropdown2-btn').attr('express',$(this).data('value'));
	});
	$(document).on('click','.c-dropdown2-btn-icon',function(){
		$('.c-dropdown2-menu').hide();
		$(this).parents('.c-dropdown2').find('.c-dropdown2-menu').show();
	});
	$('body').on('click',function(e) {
		if (!$(e.srcElement).is('.op_express_delivery_dropdown_container *') || !$(e.target).is('.op_express_delivery_dropdown_container *') || !e.srcElement) {
			$('.c-dropdown2-menu').hide();	
		}
	});
	//批量处理
	var status = false;
	$(document).on('click','#batchbtn',function(){
		if(!status){
			var checked = $('input[name=order]:checked:visible');
			if(checked.length == 0){
				alert('请先选择要填单号的订单');
				return;
			}
			$('.choosenum').text(checked.length);
			$('.batchBlock').show();
			$('#batchbtn i').html('&#xe626;');
			status = true;
		}else{
			$('.batchBlock').hide();
			$('#batchbtn i').html('&#xe627;');
			status = false;
		}
		
	});
	//关闭批量
	$(document).on('click','.batchBlock .closebtn',function(){
		$(this).parent().hide();
		$('#batchbtn i').html('&#xe627;');
		status = false;
	});
	//确定
	$(document).on('click','#ensurebtn',function(){
		var val = $(this).parents('.batchBlock').find('.c-dropdown2-btn').html();
		var express = $(this).parents('.batchBlock').find('.c-dropdown2-btn').attr('express');
		var txt = $(this).parents('.batchBlock').find('input[name=expNo]').val();
		if(!val||!txt) return;
		var checked = $('input[name=order]:checked:visible').parents('tr');
		$.each(checked,function(index,obj){
			$(obj).find('.c-dropdown2-btn').html(val);
			$(obj).find('.c-dropdown2-btn').attr('express',express);
			$(obj).find('.expNos input').val(parseInt(txt)+index);
		});
		$('.batchBlock').hide();
		$('#batchbtn i').html('&#xe627;');
	});
	// 全选按钮
	$(document).on('change', '#choolseAll', function() {
		var checked =  $(this).prop('checked');
		$.each($('#deliver .iCheck'),function(index,element){
			$(element).prop('checked',checked);
		});
	});

	// 全选状态改变
	$(document).on('change', '.iCheck', function() {
		$('#choolseAll').prop('checked',false);
	});
	//默认筛选全部选中
	$('input[name="excel_item"]').attr('checked',true);
	//点击筛选
	$(document).on('click','.screen',function(){
		$('.colBlock').show();
	});
	//关闭筛选
	$(document).on('click','.closebtn',function(){
		$(this).parent().hide();
	});
	$(document).on('change','input[name="excel_item"]',function(){
		if(!$(this).is(':checked')){
			var id = $(this).attr('id');
			$('#deliver .'+id+'').hide();
		}else{
			var id = $(this).attr('id');
			$('#deliver .'+id+'').show();
			_w_table_rowspan("#deliver",2);
			_w_table_rowspan("#deliver",6);
			_w_table_rowspan("#deliver",7);
			_w_table_rowspan("#deliver",8);
			_w_table_rowspan("#deliver",9);
			_w_table_rowspan("#deliver",10);
			_w_table_rowspan("#deliver",11);
			table_rowspan("#deliver",12);
			rowspan("#deliver",13);
		}
	});
	$(document).on('change','#merge',function(){
		if($('#merge').is(':checked')){
			var $tr = $('#deliver tbody tr');
			$.each($tr,function(index,obj){
				var id = $(obj).find('.orderNos').text();
				$(obj).find('.checkindex').text(id);
			});
			_w_table_rowspan("#deliver",2);
			_w_table_rowspan("#deliver",6);
			_w_table_rowspan("#deliver",7);
			_w_table_rowspan("#deliver",8);
			_w_table_rowspan("#deliver",9);
			_w_table_rowspan("#deliver",10);
			_w_table_rowspan("#deliver",11);
			table_rowspan("#deliver",12);
			rowspan("#deliver",13);
			_w_table_rowspan("#deliver",1);
			var arr = $('.firstcol:visible');
			$.each(arr,function(index,obj){
				$(obj).find('.checkindex').text(index+1);
				$(obj).find('.expNos input').attr('tabindex',index+1);
			});
		}else{
			$('#deliver td').removeAttr('rowSpan').show();
			var arr = $('.firstcol');
			$.each(arr,function(index,obj){
				$(obj).find('.checkindex').text(index+1);
				$(obj).find('.expNos input').attr('tabindex',index+1);
			});

		}
		$.each($('input[name="excel_item"]'),function(index,obj){
			if(!$(obj).is(':checked')){
				var id = $(obj).attr('id');
				$('#deliver .'+id+'').hide();
			}
		});
	});
	//校验Input
	$(document).on('blur','input[type=text]',function(){
		var reg = /^\w+$/;
		var $me = $(this),
				item = $me.val();
		if(!reg.test(item)){
			if(!item) return;
			$me.val('');
			$me.after('<span style="color:red;position:absolute;top:0;right:0">格式错误</span>');
			setTimeout(function(){
				$me.next().remove();
			},2000);
		}else{
			$me.val(item);
		}	
	});
	//发货完毕
	$(document).on('click','#btnConsign',function(){
		var expNo = $('.expNos input:visible');
		var arr = [];
		$.each(expNo,function(index,obj){
			if(!$(obj).val()){
				arr.push($(obj));
			}
		});
		if(arr.length>0){
			if(!confirm('订单号未填完整，确定完成吗？')){
				return;
			}
			//
		}
		var params = {
			'authenticityToken':$("input[name=authenticityToken]").val()
		};
		var orderList = $("#deliver tbody tr .expNos input:visible");
		$.each(orderList,function(index,obj){
			params['orders['+index+'].express'] = $(obj).parents('tr').find('.c-dropdown2-btn').attr('express');
			params['orders['+index+'].expNo'] = $(obj).parents('tr').find('.expNos input:visible').val();
		});
		Tr.post('/',params,function(data){
			if (data.code == 200) {
				window.location.href = '';
			}
		});
	});
	//shift连选
	var arrchecked = [];
	var lastChecked = null;
	$(document).on('change','input[name="order"]:visible',function(e){
		if(!CDT.keydown){
			arrchecked = [];
			$.each($('input[name="order"]:visible'),function(index,obj){
				if($(obj).is(':checked')){
					arrchecked.push($(obj));
				}
			})
		}
	});
	$(document).on('keydown',function(e){
		if (e.keyCode == 16) {
			CDT.keydown = true;
			lastChecked = arrchecked[arrchecked.length-1];
		}
	});
	$(document).on('keyup',function(e){
		if (e.keyCode == 16) {
			CDT.keydown = false;
		}
	});
	var handleChecked = function(e) {
		if (lastChecked && e.shiftKey&&CDT.keydown) {
			var i = $('input[name="order"]:visible').index(lastChecked);
			var j = $('input[name="order"]:visible').index(e.target);
			var checkboxes = [];
			if (j > i) {
				checkboxes = $('input[name="order"]:visible:gt(' + (i-1) + '):lt(' + (j - i+1) + ')');
			} else {
				checkboxes = $('input[name="order"]:visible:gt(' + j + '):lt(' + (i - j) + ')');
			}
			$('input[name="order"]:visible').removeAttr('checked');
			$(checkboxes).attr('checked', 'checked');
			if (!$(e.target).is(':checked')) {
			  $(e.target).attr('checked', 'checked');
			 }
		}
	}
	$('input[name="order"]').click(handleChecked);
}
$(function(){
	initBase();
	if($('#merge').is(':checked')){
		_w_table_rowspan("#deliver",2);
		_w_table_rowspan("#deliver",6);
		_w_table_rowspan("#deliver",7);
		_w_table_rowspan("#deliver",8);
		_w_table_rowspan("#deliver",9);
		_w_table_rowspan("#deliver",10);
		_w_table_rowspan("#deliver",11);
		table_rowspan("#deliver",12);
		rowspan("#deliver",13);
		_w_table_rowspan("#deliver",1);
		var arr = $('.firstcol:visible');
		$.each(arr,function(index,obj){
			$(obj).find('.checkindex').text(index+1);
			$(obj).find('.expNos input').attr('tabindex',index+1);
		});
	}
	
});
function _w_table_rowspan(_w_table_id, _w_table_colnum) {
	_w_table_firsttd = "";
	_w_table_currenttd = "";
	_w_table_SpanNum = 0;
	_w_table_Obj = $(_w_table_id + " tr td:nth-child(" + _w_table_colnum + ")");
	_w_table_Obj.each(function(i) {
		if (i == 0) {
			_w_table_firsttd = $(this);
			_w_table_SpanNum = 1;
		} else {
			_w_table_currenttd = $(this);
			if (_w_table_firsttd.text() == _w_table_currenttd.text()) {
				_w_table_SpanNum++;
				_w_table_currenttd.hide(); //remove(); 
				_w_table_firsttd.attr("rowSpan", _w_table_SpanNum);
			} else {
				_w_table_firsttd = $(this);
				_w_table_SpanNum = 1;
			}
		}
	});
}
function table_rowspan(_w_table_id, _w_table_colnum) {
	_w_table_firsttd = "";
	_w_table_currenttd = "";
	_w_table_SpanNum = 0;
	_w_table_Obj = $(_w_table_id + " tr td:nth-child(" + _w_table_colnum + ")");
	_w_table_Obj.each(function(i) {
		if (i == 0) {
			_w_table_firsttd = $(this);
			_w_table_SpanNum = 1;
		} else {
			_w_table_currenttd = $(this);
			if (_w_table_firsttd.find('input').val() == _w_table_currenttd.find('input').val()) {
				_w_table_SpanNum++;
				_w_table_currenttd.hide(); //remove(); 
				_w_table_firsttd.attr("rowSpan", _w_table_SpanNum);
			} else {
				_w_table_firsttd = $(this);
				_w_table_SpanNum = 1;
			}
		}
	});
}
function rowspan(_w_table_id, _w_table_colnum) {
	_w_table_firsttd = "";
	_w_table_currenttd = "";
	_w_table_SpanNum = 0;
	_w_table_Obj = $(_w_table_id + " tr td:nth-child(" + _w_table_colnum + ")");
	_w_table_Obj.each(function(i) {
		if (i == 0) {
			_w_table_firsttd = $(this);
			_w_table_SpanNum = 1;
		} else {
			_w_table_currenttd = $(this);
			if (_w_table_firsttd.find('input').attr('name') == _w_table_currenttd.find('input').attr('name')) {
				_w_table_SpanNum++;
				_w_table_currenttd.hide(); //remove(); 
				_w_table_firsttd.attr("rowSpan", _w_table_SpanNum);
			} else {
				_w_table_firsttd = $(this);
				_w_table_SpanNum = 1;
			}
		}
	});
}