// 覆盖hidden input 不校验
$.validator.setDefaults({
	ignore: ""
});
$.extend($.validator.messages, {
	digits: Tr.error('格式为纯数字')
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
  Tr.get("/dpl/item/query", param, function(data){
    if(data.code == 200){
    	var $choosedHtml = '',i = data.results;
			$.each(i.skus, function(index, obj) {
				$choosedHtml += '<div class="albumImg"><img src="'+obj.img+'"/><div class="tip"> <b></b><em>' + obj.color + '</em></div></div>';
			});
			$('#imgcont img').attr('src',i.picUrl);
			$('#itemTitle').text(i.title);
			$('.price').text(i.retailPrice/100);
			$('#selSku').html($choosedHtml);
    	result = true;
    	if(!data.results.freightTemp){
	    	customMsg = Tr.error('该商品未完善运费信息，无法计算运费');
	    	$('#imgcont img').attr('src','');
				$('#itemTitle').text('商品名称');
				$('.price').text(0);
	    	$('#selSku').html('<div class="albumImg"><img src=""/><div class="tip"> <b></b><em>商品规格</em></div></div>');
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
	// 鼠标悬停显示商品标题
	$(document).on('hover', '.albumImg', function() {
		$(this).find('.tip').toggle();
	});
	var Validator = $('#addOrder').validate({
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
			name: {
				required: true,
				truename: true
			},
			contact: {
				required: true,
				checkContact: true
			},
			provinceId: {
				required: true
			},
			address: {
				required: true
			}
		}
	});
	//选择规格
	$(document).on('click','.albumImg',function(){
		if($(this).find('img').attr('src')=='') return;
		$('.albumImg').removeClass('active');
		$('#txtSku').val($(this).find('img').attr('src'));
		$('div[for=txtSku]').children('span').remove();
		$('#txtSku').data('title',$(this).find('em').text());
		$(this).addClass('active');
	});
	//删除已添加订单
	$(document).on('click','.delbtnicon',function(){
		if (!confirm('确定删除该订单吗？')) {
			return;
		}
		var index = $(this).parents('.orderItem').data('index');
		$('#submitList').find('.containerList').eq(index).remove();
		$(this).parents('.orderItem').remove();
	});
	//添加订单
	$(document).on('click','#btnOrder',function(){
		if (!Validator.form()) {
			return;
		}
		var index = $('#orderList').find('.orderItem').length;
		var hiddenInput = '';
		hiddenInput = '<input type="hidden" name="orders['+index+'].outOrderNo" value="'+$('#outOrderNo').val()+'"/>';
		hiddenInput += '<input type="hidden" name="orders['+index+'].itemId" value="'+$('#itemId').val()+'"/>';
		hiddenInput += '<input type="hidden" name="orders['+index+'].num" value="'+$('#txtNum').val()+'"/>';
		hiddenInput += '<input type="hidden" name="orders['+index+'].skuStr" value="'+$('#txtSku').data('title')+'"/>';
		hiddenInput += '<input type="hidden" name="orders['+index+'].buyerName" value="'+$('#txtName').val()+'"/>';
		hiddenInput += '<input type="hidden" name="orders['+index+'].contact" value="'+$('#txtContact').val()+'"/>';
		hiddenInput += '<input type="hidden" name="orders['+index+'].province" value="'+$('#province').val()+'"/>';
		hiddenInput += '<input type="hidden" name="orders['+index+'].provinceId" value="'+$('#province_id').val()+'"/>';
		hiddenInput += '<input type="hidden" name="orders['+index+'].city" value="'+$('#city').val()+'"/>';
		hiddenInput += '<input type="hidden" name="orders['+index+'].region" value="'+$('#region').val()+'"/>';
		hiddenInput += '<input type="hidden" name="orders['+index+'].address" value="'+$('#txtAddress').val()+'"/>';
		hiddenInput += '<input type="hidden" name="orders['+index+'].note" value="'+$('#txtNote').val()+'"/>';
		var div = $('<div class="containerList"></div>');
		div.append(hiddenInput);
		$('#submitList').append(div);
		var form = $('#addOrder');
		var flyElm = $('#addOrder').clone().css({'opacity':0.5,'border':'1px solid #888','background-color':'#fff'});
		$('body').append(flyElm);
		flyElm.css({
			'z-index': 100,
			'display': 'block',
			'position': 'absolute',
			'top': form.offset().top +'px',
			'left': form.offset().left +'px',
			'width': '450px',
			'height': form.height()+'px'
		});
		flyElm.animate({
			top: $('#orderList').offset().top+60,
			left: $('#orderList').offset().left+300,
			width: '20px',
			height: '30px'
		}, 800, function() {
			flyElm.remove();
		});
		setTimeout('createItem('+index+')',500);
		setTimeout('reset()',700);
	});
	// 选择省份
	$(document).on('change', '.province', function() {
		var id = $(this).find('option:selected').attr('rid');
		if(!id){
			return;
		}
		$('#province_id').val(id);
		var $me = $(this),
			$city = $me.siblings('.city').eq(0),
			$region = $me.siblings('.region').eq(0);
		$me.prev().val(id);
		$city.find('option').slice(1).remove();
		$region.find('option').slice(1).remove();
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
				$city.children().eq(1).attr('selected',true);
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
		var id = $me.find('option:selected').attr('rid');
		$region.find('option').slice(1).remove();
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
				$region.children().eq(1).attr('selected',true);
				$region.change();
			}			
		}, {
			loadingMask: false
		});
	});
	//提交进行下一步
	$(document).on('click','#btnSubmit',function(){
		var len = $('#orderList').find('.orderItem').length;
		if(!len||len==0){
			alert('请添加订单');
			return;
		}
		var param = {};
		$.each($('.containerList'),function(index,obj){
			param['orders['+index+'].outOrderNo'] = $(obj).find('input[name*="outOrderNo"]').val();
			param['orders['+index+'].itemId'] = $(obj).find('input[name*="itemId"]').val();
			param['orders['+index+'].num'] = $(obj).find('input[name*="num"]').val();
			param['orders['+index+'].skuStr'] = $(obj).find('input[name*="skuStr"]').val();
			param['orders['+index+'].buyerName'] = $(obj).find('input[name*="buyerName"]').val();
			param['orders['+index+'].contact'] = $(obj).find('input[name*="contact"]').val();
			param['orders['+index+'].province'] = $(obj).find('input[name*="province"]').val();
			param['orders['+index+'].provinceId'] = $(obj).find('input[name*="provinceId"]').val();
			param['orders['+index+'].city'] = $(obj).find('input[name*="city"]').val();
			param['orders['+index+'].region'] = $(obj).find('input[name*="region"]').val();
			param['orders['+index+'].address'] = $(obj).find('input[name*="address"]').val();
			param['orders['+index+'].note'] = $(obj).find('input[name*="note"]').val();
		});
		Tr.post('/retailer/order/usergenerate',param, function(data) {
			if (data.code == 200) {
				window.location.href = '/retailer/order/list';
			}else{
				alert(data.msg);
			}
		});
	});
}
$(function(){
	initBase();
	loadProviceHTML();
	//减
	$(document).on('click','.minus',function() {
		var $me = $(this);
		var $parent = $(this).parent();
		var index = $me.parents('.orderItem').data('index');
		var num = parseInt($parent.find(".qnty").val());
		num--;
		$parent.find(".qnty").val(num);
		$('input[name="orders['+index+'].num"]').val(num);
		if (parseInt($parent.find(".qnty").val()) < 1 || isNaN($parent.find(".qnty").val())) {
			$parent.find(".qnty").val(1);
		}
	});
	//加
	$(document).on('click','.plus',function() {
		var $me = $(this);
		var $parent = $(this).parent();
		var index = $me.parents('.orderItem').data('index');
		var num = parseInt($parent.find(".qnty").val());
		num++;
		$parent.find(".qnty").val(num);
		$('input[name="orders['+index+'].num"]').val(num);
		if (parseInt($parent.find(".qnty").val()) > 999) {
			$parent.find(".qnty").val(999);
		}
	});
	//输入框
	$(".qnty").keyup(function(){
		var reg = /^[0-9]+$/;
		if(!reg.test($(this).val())){
				$(this).val('');
		}
	});
	$('body').on('click',function(e) {
		$('.qnty').each(function(){ 
			if($(this).val()==''|| !$(this).val()){
				if (!$(e.srcElement).is('.qnty *') || !$(e.target).is('.qnty *') || !e.srcElement) {
					$(this).val(1);
				}
			}
		});
	});
});
//加载省份
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
//重置输入框
function reset(){
	$('#txtSku').val('');
	$('#txtSku').data('title','');
	$('#itemId').val('');
	$('#txtName').val('');
	$('#txtContact').val('');
	$('#outOrderNo').val('');
	$('#txtNum').val('');
	$('#txtAddress').val('');
	$('#txtNote').val('');
	$('#imgcont img').attr('src','');
	$('#itemTitle').text('商品名称');
	$('.price').text(0);
	$('.province').find('option').slice(1).remove();
	$('.city').find('option').slice(1).remove();
	$('.region').find('option').slice(1).remove();
	$('#province_id').val('');
	loadProviceHTML();
	$('#selSku').html('<div class="albumImg"><img src=""/><div class="tip"> <b></b><em>商品规格</em></div></div>');
	$('.validateLine').html('');
}
//创建已添加订单
function createItem(index){
	var src = $('#txtSku').val();
	var sku = $('#txtSku').data('title');
	var id = $('#itemId').val();
	var title = $('#itemTitle').text();
	var name = $('#txtName').val();
	var phone = $('#txtContact').val();
	var num = $('#txtNum').val();
	var html= ''
	html = '<div class="orderItem" data-index="'+index+'"><div class="product"><div class="imgcont"><img src="'+src+'"></div>';
	html +=	'<div class="itemtext"><p class="itemTitle">商品ID <span class="productId">'+id+'</span></p><p class="itemTitle">'+title+'</p>';
	html +=	'<div class="skus"><p>规格 <span class="sku">'+sku+'</span></p><div class="numbtn" style=""><div class="down minus">-</div><div class="qntys"><input class="inputText qnty" type="text" value="'+num+'" maxlength="3" /></div><div class="up plus">+</div></div></div></div></div><div class="clearfix"></div>';
	html +=	'<div class="user"><i class="iconfont mr10" style="color:#444">&#xe62b;</i><span class="name">'+name+'</span><span class="phone">'+phone+'</span></div><div class="delbtnicon"><i class="iconfont">&#xe606;</i></div><div class="clearfix"></div></div>';
	$('#orderList').append(html);
}