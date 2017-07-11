$.validator.setDefaults({
	ignore: ""
});
//异步验证id
$.validator.addMethod("checkId", function(value, element){
  var result = true;
  var customMsg = '';
  // 设置同步
  $.ajaxSetup({
      async: false
  });
  var param = {
      "id": value
  };
  $.get("/dpl/item/query", param, function(data){
    if(data.code == 200){
    	var $choosedHtml = '';
			$.each(data.results.skus, function(index, obj) {
				$choosedHtml += '<option>' + obj.color + '</option>';
			});
			$('#selSku').html($choosedHtml);
    	result = true;
    	if(!data.results.freightTemp){
	    	customMsg = Tr.error('该商品未完善运费信息，无法计算运费');
	    	result = false;
	    }
    }else{
    	customMsg = Tr.error('商品不存在');
			result = false;
    }
  });
  // 恢复异步
  $.ajaxSetup({
      async: true
  });
  $.validator.messages.checkId = customMsg;
  return result;
});
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
		$(this).parents('tr').data('isDelete',true).hide();
		var arr = $('.tbl tbody tr:visible');
		if(arr.length==1){
			$('.tbl tbody tr .delbtn').hide();
		}
	});
	//导出excel弹框
	$(document).on('click','#exportExcelbtn',function () {
		Tr.popup('exportExcel');
	});
	$(document).on('click','#Createbtn',function () {
		Tr.popup('addOrder');
	});
	//关闭弹框
	$('.wnd_Close_Icon').click(function() {
		$(this).parents('.popWrapper').hide();
		$('html').removeClass('overflow-hidden');
	});
	//导出excel
	$(document).on('click','#btnExport',function () {
		var chk_value =[]; 
		$('input[name="excel_item"]:checked').each(function(){ 
			chk_value.push($(this).val()); 
		}); 
		var excelString = chk_value.join(',');
		var tradeId = $('input[name=trade]').val();
		if(excelString&&tradeId){
			$('#btnExport').attr('href','/sys/order/excel?tradeId=' + tradeId + '&columns='+excelString).css('background-color','#ffce00');
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
	//校验添加订单弹框
	var Validator = $('#frmAddOrder').validate({
		onfocusout: function(element){
      $(element).valid();
    },
		rules: {
			itemId: {
				required: true,
				digits: true,
				checkId: true
			},
			outOrderNo: {
				required: true,
				alphanumeric: true
			},
			sku: {
				required: true
			},
			num: {
				required: true,
				digits: true
			},
			discount: {
				number: true
			},
			name: {
				required: true,
				truename: true
			},
			contact: {
				required: true,
				checkContact: true
			},
			province: {
				required: true
			},
			address: {
				required: true
			},
			createTime: {
				times: true
			}
		}
	});
	//添加订单
	$(document).on('click','#btnOrder',function(){
		if (!Validator.form()) {
			return;
		}
		var index = $(".orderList table tbody tr:last").data('order-no') + 1;
		var html;
    html = '<tr class="" data-order-no='+ index +'>';
		html += '<td><div name="id"></div></td><td><div class="changeItem changenum" name="outOrderNo">'+$('#txtOutOrderNo').val()+'</div></td><td><div class="changeItem changeTotalfee" name="totalFee"></div></td>';
		html += '<td><div class="changeItem changefee" name="shippingFee"></div></td><td><div class="changeItem changeExpress" name="express"><div class="op_express_delivery_dropdown_container"></div></div></td><td><div class="changeItem changeexpNo" name="expNo"></td>';
		html += '<td><div></div></td><td><div class="changeItem changeId" name="itemId">'+$('#txtItemId').val()+'</div></td><td><div class="changeItem changeSku" name="sku">'+$('#selSku').val()+'</div></td>';
		html += '<td><div class="changeItem changenum" name="num">'+$('#txtNum').val()+'</div></td><td><div class="changeItem changetxt" name="name">'+$('#txtName').val()+'</div></td><td><div class="changeItem changephone" name="contact">'+$('#txtContact').val()+'</div></td>';
		html += '<td><input type="hidden" name="province_id" value="'+$('#province_id').val()+'"/><div class="changeItem changePro" name="province">'+$('#province').val()+'</div></td><td><input type="hidden" name="city_id" value=""/><div class="changeItem changecity" name="city">'+$('#city').val()+'</div></td><td><div class="changeItem changeregion" name="region">'+$('#region').val()+'</div></td>';
		html += '<td><div class="addresswidth changeItem changelenght" name="address">'+$('#txtAddress').val()+'</div></td><td><div class="changeItem changelenght" name="note">'+$('#txtNote').val()+'</div></td><td><div class="changeItem changetime" name="createTimeStr">'+$('#txtCreateTime').val()+'</div></td>';
		html += '<td><div><a class="delbtn"><i class="iconfont mr5">&#xe606;</i>删除</a></div></td></tr>';
		$('.orderList tbody').append(html);
		$(this).parents('.popWrapper').hide();
		$('html').removeClass('overflow-hidden');

	});
	//修改订单
	$(document).on('dblclick','.changenum',function(){
		if($(this).find('input').length>0) return;
		var item = $(this).text();
		$(this).html('<input type="text" class="changeinput numinput" value="'+item+'" maxlength="20"/>');
		$('.numinput').focus();
	});
	//修改快递名称
	$(document).on('dblclick','.changeExpress',function(){
		$(this).html('<div class="op_express_delivery_dropdown_container"></div>');
		$('.op_express_delivery_dropdown_container').load('/expressList');
	});
	$(document).on('click','.c-dropdown2-option',function(){
		$(this).parents('.changeExpress').attr('express',$(this).data('value'));
		$(this).parents('.changeExpress').html($(this).html());
	});
	$(document).on('dblclick','.changefee',function(){
		if($(this).find('input').length>0) return;
		var item = $(this).text();
		$(this).html('<input type="text" class="changeinput feeinput" data-value="'+item+'" value="'+item+'" maxlength="20"/>');
		$('.feeinput').focus();
	});
	$(document).on('dblclick','.changeTotalfee',function(){
		if($(this).find('input').length>0) return;
		var item = $(this).text();
		$(this).html('<input type="text" class="changeinput totalFeeinput" data-value="'+item+'" value="'+item+'" maxlength="20"/>');
		$('.totalFeeinput').focus();
	});
	$(document).on('dblclick','.changetxt',function(){
		if($(this).find('input').length>0) return;
		var item = $(this).text();
		$(this).html('<input type="text" class="changeinput txtinput" value="'+item+'" maxlength="20"/>');
		$('.txtinput').focus();
	});
	$(document).on('dblclick','.changephone',function(){
		if($(this).find('input').length>0) return;
		var item = $(this).text();
		$(this).html('<input type="text" class="changeinput phoneinput" value="'+item+'" maxlength="11"/>');
		$('.phoneinput').focus();
	});
	$(document).on('dblclick','.changeexpNo',function(){
		if($(this).find('input').length>0) return;
		var item = $(this).text();
		$(this).html('<input type="text" class="changeinput expNOinput" value="'+item+'" maxlength="20"/>');
		$('.expNOinput').focus();
	});
	$(document).on('dblclick','.changeId',function(){
		if($(this).find('input').length>0) return;
		var item = $(this).text();
		$(this).html('<input type="text" class="changeinput idinput" value="'+item+'" maxlength="10"/>');
		$('.idinput').focus();
	});
	//规格
	$(document).on('dblclick','.changeSku',function(){
		var $me = $(this);
		var id = $me.parent().prev().find('.changeId').text();
		var val = $me.text();
		Tr.get("/dpl/item/query", {'id':id}, function(data){
	    if(data.code == 200){
	    	var $choosedHtml = '';
				$.each(data.results.skus, function(index, obj) {
					$choosedHtml += '<option>' + obj.color + '</option>';
				});
				$me.html('<select class="changeSkus" value=""></select>');
				$me.find('.changeSkus').html($choosedHtml);
				$me.find('.changeSkus').prepend('<option selected>-- 请选择 --</option>');
	    }
	  });
	});
	$(document).on('dblclick','.changelenght',function(){
		if($(this).find('input').length>0) return;
		var item = $(this).text();
		$(this).html('<input type="text" class="changeinput txtinput" value="'+item+'" maxlength="60"/>');
		$('.txtinput').focus();
	});
	$(document).on('dblclick','.changetime',function(){
		if($(this).find('input').length>0) return;
		$(this).html('<input type="text" class="changeinput timeinput" readonly value="" maxlength="60"/>');
	});
	//修改省
	$(document).on('dblclick','.changePro',function(){
		if($(this).find('input').length>0) return;
		var item = $(this).text();
		var id = $(this).prev().val();
		$(this).html('<input type="hidden" value="'+id+'"/><select class="province tablePro" name="province" value=""><option>'+item+'</option></select>');
		$(this).parents('tr').find('.changecity').html('<input type="hidden"/><select class="city tableCity" name="city"><option>-- 城市 --</option></select>');
		$(this).parents('tr').find('.changeregion').html('<input type="hidden"/><select class="region tableRegion" name="region"><option>-- 区域 --</option></select>');
		$(this).parents('tr').find('.addresswidth').html('<input type="text" class="changeinput txtinput" value="" maxlength="60"/>');
		loadProviceHTML();
	});
	//修改市
	$(document).on('dblclick','.changecity',function(){
		if($(this).find('input').length>0) return;
		var $me = $(this);
		var id = $me.parent().prev().find('input').val();
		var item = $me.parent().prev().find('.changePro').text();
		$me.parents('tr').find('.changePro').html('<input type="hidden" value="'+id+'"/><select class="province tablePro" name="province" value=""><option>'+item+'</option></select>');
		$me.html('<input type="hidden"/><select class="city tableCity" name="city"><option>-- 城市 --</option></select>');
		$me.parents('tr').find('.changeregion').html('<input type="hidden"/><select class="region tableRegion" name="region"><option>-- 区域 --</option></select>');
		$me.parents('tr').find('.addresswidth').html('<input type="text" class="changeinput txtinput" value="" maxlength="60"/>');
		loadProviceHTML();
		Tr.get('/dpl/region', {
			id: id
		}, function(data) {
			if (data.code != 200) return;
			if(data.results&&data.results.length>0){
				$.each(data.results,function(index,obj){
					$me.find('.city').append('<option rid="'+obj.id+'">'+obj.name+'</option>');
				});
				$me.children().eq(1).attr('selected',true);
			}			
		}, {
			loadingMask: false
		});
	});
	//修改区
	$(document).on('dblclick','.changeregion',function(){
		if($(this).find('input').length>0) return;
		var $me = $(this);
		var id = $me.parent().prev().prev().find('input').val();
		var item = $me.parent().prev().prev().find('.changePro').text();
		$me.parents('tr').find('.changePro').html('<input type="hidden" value="'+id+'"/><select class="province tablePro" name="province" value=""><option>'+item+'</option></select>');
		$me.parents('tr').find('.changecity').html('<input type="hidden"/><select class="city tableCity" name="city"><option>-- 城市 --</option></select>');
		$me.html('<input type="hidden"/><select class="region tableRegion" name="region"><option>-- 区域 --</option></select>');
		$me.parents('tr').find('.addresswidth').html('<input type="text" class="changeinput txtinput" value="" maxlength="60"/>');
		loadProviceHTML();
		Tr.get('/dpl/region', {
			id: id
		}, function(data) {
			if (data.code != 200) return;
			if(data.results&&data.results.length>0){
				$.each(data.results,function(index,obj){
					$me.parents('tr').find('.city').append('<option rid="'+obj.id+'">'+obj.name+'</option>');
				});
				$me.change();
			}			
		}, {
			loadingMask: false
		});
	});
	$(document).on('change','.tableRegion',function(){
		var val = $(this).val();
		$(this).parent().html(val);
	});
	$(document).on('change','.timeinput',function(){
		var val = $(this).val();
		if(val){
			$(this).parent().html(val);
		}
	});
	//校验修改数据input
	//数字
	$(document).on('blur','.numinput',function(){
		var reg = /^\+?[1-9]\d*$/;
		var $me = $(this),
				item = $me.val();
		if(!item) return;
		if(!reg.test(item)){
			$me.before('<div class="prompt">请输入大于0的数字</div>');
			setTimeout(function(){
				$me.prev().remove();
			},2000);
			return;
		}else{
			$me.val(parseInt(item));
			$(this).parent().html(item);
		}	
	});
	//金额
	$(document).on('blur','.feeinput',function(){
		var reg = /^-?(?:\d+|\d{1,3}(?:,\d{3})+)?(?:\.\d+)?$/;
		var $me = $(this),
				item = $me.val();
		var oldPirce = $me.data('value');
		var change = 0;
		var dis = $('#discountPrice').val();
		if(!item) return;
		if(!reg.test(item)){
			$me.before('<div class="prompt">请输入金额</div>');
			setTimeout(function(){
				$me.prev().remove();
			},2000);
			return;
		}else{
			change = parseFloat(oldPirce) - parseFloat(item);
			$('#discountPrice').val(parseFloat(dis)+change);
			$('#discountPrice').change();
			$me.val(parseFloat(item));
			$(this).parent().html(item);
		}	
	});
	$(document).on('blur','.totalFeeinput',function(){
		var reg = /^-?(?:\d+|\d{1,3}(?:,\d{3})+)?(?:\.\d+)?$/;
		var $me = $(this),
				item = $me.val();
		var oldPirce = $me.data('value');
		var change = 0;
		var dis = $('#discountPrice').val();
		if(!item) return;
		if(!reg.test(item)){
			$me.before('<div class="prompt">请输入金额</div>');
			setTimeout(function(){
				$me.prev().remove();
			},2000);
			return;
		}else{
			change = parseFloat(oldPirce) - parseFloat(item);
			$('#discountPrice').val(parseFloat(dis)+change);
			$('#discountPrice').change();
			$me.val(parseFloat(item));
			$(this).parent().html(item);
		}	
	});
	//号码
	$(document).on('blur','.phoneinput',function(){
		var reg = /^\d{4,8}/;
		var $me = $(this),
				item = $me.val();
		if(!item) return;
		if(!reg.test(item)){
			$me.before('<div class="prompt">请输入正确的联系号码</div>');
			setTimeout(function(){
				$me.prev().remove();
			},2000);
			return;
		}else{
			$me.val(item);
			$(this).parent().html(item);
		}	
	});
	//快递单号
	$(document).on('blur','.expNOinput',function(){
		var reg = /^[a-zA-Z0-9]+$/;
		var $me = $(this),
				item = $me.val();
		if(!item) return;	
		if(!reg.test(item)){
			if(!item){
				$(this).parent().html(item);
			}
			$me.before('<div class="prompt">请输入正确的快递单号</div>');
			setTimeout(function(){
				$me.prev().remove();
			},2000);
		}else{
			$me.val(item);
			$(this).parent().html(item);
		}	
	});
	//文字
	$(document).on('blur','.txtinput',function(){
		if(!$(this).val()) return;
		var reg = /^([\u4e00-\u9fa5]+|([a-zA-Z]+?.\s?)+)$/;
		var $me = $(this),
				item = $me.val();
		if(!item) return;
		if(!reg.test(item)){
			$me.before('<div class="prompt">请输入正确的格式</div>');
			setTimeout(function(){
				$me.prev().remove();
			},2000);
			return;
		}else{
			$me.val(parseInt(item));
			$(this).parent().html(item);
		}	
	});
	//id
	$(document).on('blur','.idinput',function(){
		var reg = /^\d+$/;
		var $me = $(this),
				item = $me.val();
		if(!item) return;
		var param = {
      "id": item
	  };
	  Tr.get("/dpl/item/query", param, function(data){
	    if(data.code == 200){
	    	if(!data.results.freightTemp){
	    		$me.before('<div class="prompt">该商品未完善运费信息，无法计算运费</div>');
	    		setTimeout(function(){
						$me.prev().remove();
					},2000);
	    		$me.parent().parent().next().find('.changeSkus').html('');
	    		return;
		    }
	    	var $choosedHtml = '';
				$.each(data.results.skus, function(index, obj) {
					$choosedHtml += '<option>' + obj.color + '</option>';
				});
				$me.parent().parent().next().find('.changeSku').html('<select class="changeSkus" value=""></select>');
				$me.parent().parent().next().find('.changeSkus').html($choosedHtml);
				$me.parent().parent().next().find('.changeSkus').prepend('<option selected>-- 请选择 --</option>')
				$me.parent().html(item);
	    	
	    }else{
	    	$me.before('<div class="prompt">商品不存在</div>');
	    	setTimeout(function(){
					$me.prev().remove();
				},2000);
	    	$me.parent().parent().next().find('.changeSkus').html('');
	    }
	  });
	});
	//规格
	$(document).on('change','.changeSkus',function(){
		var val = $(this).val();
		$(this).parent().html(val);
	});
	//校验应付款input
	var dis = true;
	$(document).on('change','#discountPrice',function(){
		var reg = /^(?!0+(?:\.0+)?$)(?:[1-9]\d*|0)(?:\.\d{1,2})?$/;
		var total = $('#totalPrice em').text();
		var results = $('#toPay em').text();
		var $me = $(this),
				price = $me.val();
		if(!reg.test(price)){
			dis = false;
			$me.before('<div class="prompt">输入格式错误</div>');
    	setTimeout(function(){
				$me.prev().remove();
			},2000);
		}else{
			$me.val(price);
			dis = true;
			results = total - price;
			$('#toPay em').text(results);
		}	
	});
	//保存应付款
	$(document).on('click','#savebtn',function(){
		if(!dis){
			alert('折扣输入有误，请重新输入');
			return;
		}
		Tr.post('/sys/trade/fee/save',{
			'tradeId': $('.orderNo').text(),
			'payment': $('#toPay em').text()
		},function(data){
			if (data.code == 200) {
				alert('保存成功');
			}else {
				alert(data.msg);
			}
		});
	});
	//提交当前数据
	$(document).on('click','#btnSubmit',function(){
		var len = $(".orderList table tbody tr input:visible");
		if(len.length>0){
			alert('订单修改未完成');
			return;
		}
		var orderList = $(".orderList table tbody tr");
		var visibleList = $(".orderList table tbody tr:visible");
		if(visibleList.length==0){
			alert('无可保存的订单数据');
			return;
		}
		var params = {
			'tradeId': $('.orderNo').text(),
			'payment': $('#toPay').val()
		};
		$.each(orderList,function(index,obj){
			params['orders['+index+'].isDelete'] = $(obj).data('isDelete')?$(obj).data('isDelete'):false;
			params['orders['+index+'].id'] = $(obj).find('div[name=id]').text();
			params['orders['+index+'].outOrderNo'] = $(obj).find('div[name=outOrderNo]').text();
			params['orders['+index+'].totalFee'] = $(obj).find('div[name=totalFee]').text();
			params['orders['+index+'].shippingFee'] = $(obj).find('div[name=shippingFee]').text();
			params['orders['+index+'].express'] = $(obj).find('div[name=express]').attr('express');
			params['orders['+index+'].expNo'] = $(obj).find('div[name=expNo]').text();
			params['orders['+index+'].itemId'] = $(obj).find('div[name=itemId]').text();
			params['orders['+index+'].skuStr'] = $(obj).find('div[name=sku]').text();
			params['orders['+index+'].num'] = $(obj).find('div[name=num]').text();
			params['orders['+index+'].buyerName'] = $(obj).find('div[name=name]').text();
			params['orders['+index+'].contact'] = $(obj).find('div[name=contact]').text();
			params['orders['+index+'].province'] = $(obj).find('div[name=province]').text();
			params['orders['+index+'].provinceId'] = $(obj).find('input[name=province_id]').val();
			params['orders['+index+'].city'] = $(obj).find('div[name=city]').text();
			params['orders['+index+'].region'] = $(obj).find('div[name=region]').text();
			params['orders['+index+'].address'] = $(obj).find('div[name=address]').text();
			params['orders['+index+'].note'] = $(obj).find('div[name=note]').text();
			params['orders['+index+'].createTimeStr'] = $(obj).find('div[name=createTimeStr]').text();
		});
		Tr.post('/sys/trade/order/save',params,function(data){
			if (data.code == 200) {
				window.location.href = '/sys/order/list';
			}else if(data.code == 8001){
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
					alert(data.msg);
				}
		});
	});
	//选择快递
	$(document).on('click','.c-dropdown2-btn',function(){
		$('.c-dropdown2-menu').show();
	});
	$(document).on('click','.c-dropdown2-option',function(){
		$('.c-dropdown2-menu').hide();
		$('.c-dropdown2-btn').html($(this).html());
		$('#txtExpress').val($(this).data('value'));
	});
	$('body').on('click',function(e) {
		if (!$(e.srcElement).is('.op_express_delivery_dropdown_container *') || !$(e.target).is('.op_express_delivery_dropdown_container *') || !e.srcElement) {
			$('.c-dropdown2-menu').hide();	
		}
	});
	
}

$(function() {
	initBase();
	loadProviceHTML();
	var arr = $('.tbl tbody tr');
	if(arr.length==1){
		$('.tbl tbody tr .delbtn').hide();
	}
	// 选择省份
	$(document).on('change', '.province', function() {
		var id = $(this).find('option:selected').attr('rid');
		if(!id){
			return;
		}
		var $me = $(this),
			$city = $me.siblings('.city').eq(0),
			$region = $me.siblings('.region').eq(0);
		$me.prev().val(id);
		$me.parent().prev().val(id);
		if($me.hasClass('tablePro')){
			$city = $me.parents('tr').find('.city').eq(0),
			$region = $me.parents('tr').find('.region').eq(0);
			$me.parent().html($me.val());
		}else{
			$city.find('option').slice(1).remove();
			$region.find('option').slice(1).remove();
		}
		if (!$me.find('option:selected').val()) {
			return;
		}
		Tr.get('/dpl/region', {
			id: id
		}, function(data) {
			if (data.code != 200) return;
			if(data.results&&data.results.length>0){
				$.each(data.results,function(index,obj){
					$city.append('<option rid="'+obj.id+'">'+obj.name+'</option>');
				});
				if(!$me.hasClass('tablePro')){
					$city.children().eq(1).attr('selected',true);
				}
				$city.change();
			}			
		}, {
			loadingMask: false
		});
		$('div[name="selProvince"]').children('span').remove();
		$('#selProvince_id').removeAttr('aria-required').removeAttr('aria-invalid').removeAttr('aria-describedby').removeClass('error');
	});
	// 选择城市
	$(document).on('change', '.city', function() {
		var id = $(this).find('option:selected').attr('rid');
		if(!id){
			return;
		}
		var $me = $(this),
			$region = $me.siblings('.region').eq(0);
		$me.parent().prev().val(id);
		if($me.hasClass('tableCity')){		
			$region = $me.parents('tr').find('.region').eq(0);
			$me.parent().html($me.val());
		}else{
			$region.find('option').slice(1).remove();
		}
		var id = $me.find('option:selected').attr('rid');
		if (!$me.find('option:selected').val()) {
			return;
		}
		Tr.get('/dpl/region', {
			id: id
		}, function(data) {
			if (data.code != 200) return;
			if(data.results&&data.results.length>0){
				$.each(data.results,function(index,obj){
					$region.append('<option rid="'+obj.id+'">'+obj.name+'</option>');
				});
				if(!$me.hasClass('tableCity')){
					$region.children().eq(1).attr('selected',true);
					$region.change();
				}				
			}			
		}, {
			loadingMask: false
		});
	});
});
function loadProviceHTML() {
	var $provice = $('.province');
	Tr.get('/dpl/region', {
		id: 1
	}, function(data) {
		if (data.code != 200) return;
		if (data.results && data.results.length > 0) {
			$.each(data.results, function(index, obj) {
				$provice.append('<option rid="' + obj.id + '">' + obj.name + '</option>');
			});
			var loadV= $provice.data('val');
			if(loadV){
				$.each($provice.children(), function(index, obj) {
					if($(obj).val()==loadV){
						$(obj).attr('selected', true);
					}
				});
			}else{
				$provice.children().eq(0).attr('selected', true);
			}
			
		}
	}, {
		loadingMask: false
	});
}