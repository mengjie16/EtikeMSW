CDT = {
	static_freightTemp: null,
	dynamic_freightTemp: null,
	provinceTemp: null,
	loadedFreightCache: {},
	loadedProvinceCache: {},
	provinceCache: {},
	provinceMap: {}
};
function initBase(argument) {
	//点击创建
	$(document).on('click', '#CreateBtn', function() {
		//var name = prompt("请输入模板名称");
		$('#txtTitle').val('');
		Tr.popup('addFreight');
	});
	//确认创建
	$(document).on('click','#btnFreight',function(){
		var name = $('#txtTitle').val(),
			$type = $("input[name='freight.type']:checked");
		var itemname = [];
		var $itemnames = $('.titleName');
		$itemnames.each(function(){
			itemname.push( $( this ).val());
		});
		var has = false;
		$.each(itemname,function(index,obj){
			if($.trim(name)==$.trim(obj)){
				alert('模板名称重复');
				has = true;
			}
		});
		if(has){
			return;
		}
		if(!name){
			alert('请命名模板');
			return;
		}
		var $temp = '';
		if(name!=''&&$type.val()=='static'){
			$temp = '<div class="freightTemp" style="border:none"><input type="hidden" value="static" name="tempType"><input class="inputText"  name="tempId"  value="" type="hidden" /><h4 class="pl10">模板名称：<input type="text" value="'+name+'" class="inputText titleName" style="display:inline-block"/> <span class="freightType ml10">模板类型：静态</span><a href="javascript:void(0)" class="extendbtn mr20"><i class="iconfont up">&#xe626;</i></a></h4><div class="extendcontent" style="display:block"><table class="tbl_type01 tbHoverable">';
			$temp += '<thead><tr><th><div class="thcell">地区</div></th><th><div class="thcell">价格(元)</div></th></tr></thead>';
			$temp += '<tbody><tr><td><div class="tdcell"><div class="province">未选择</div><input type="hidden" name="provinceId"/></div></td><td><div class="tdcell"><input type="text" class="inputText short priceitem" name="" value="0" /><i class="iconfont icondelbtn" style="display:inline-block">&#xe606;</i></div></td></tr><tr class="addTemp"><td colspan="2"><div class="tdcell additem"><i class="iconfont">&#xe60b;</i></div></td></tr></tbody></table><a href="javascript:void(0)" class="evaluate fr ml4 delbtn">删除</a><a href="javascript:void(0)" class="evaluate fr submitbtn" style="display:inline-block">保存</a></div></div>';
		}else if(name!=''&&$type.val()=='dynamic'){
			$temp = '<div class="freightTemp" style="border:none"><input type="hidden" value="dynamic" name="tempType"><input class="inputText"  name="tempId"  value="" type="hidden" /><h4 class="pl10">模板名称：<input type="text" value="'+name+'" class="inputText titleName" style="display:inline-block"/> <span class="freightType ml10">模板类型：动态</span><a href="javascript:void(0)" class="extendbtn mr20"><i class="iconfont up">&#xe626;</i></h4><div class="extendcontent" style="display:block">';
			$temp += '<table class="tbl_type01 move tbHoverable"><thead><tr><th class="width240"><div class="thcell">地区</div></th><th><div class="thcell">首重(kg)</div></th><th><div class="thcell">首重价格(元)</div></th><th><div class="thcell">续重(kg)</div></th><th><div class="thcell">续重价格(元)</div></th></tr></thead>';
			$temp += '<tbody><tr><td class="width240"><div class="tdcell"><div class="province">未选择</div><input type="hidden" name="provinceId" value=""></div></td><td><div class="tdcell"><input type="text" class="inputText short firstWeight" name="" value="0" /></div></td><td><div class="tdcell"><input type="text" class="inputText short firstPrice" name="" value="0" /></div></td>';
			$temp += '<td><div class="tdcell"><input type="text" class="inputText short nextWeight" name="" value="0" /></div></td><td><div class="tdcell"><input type="text" class="inputText short nextPrice" name="" value="0" /><i class="iconfont icondelbtn">&#xe606;</i></td></tr><tr class="addTemp"><td colspan="5"><div class="tdcell additem"><i class="iconfont">&#xe60b;</i></div></td></tr></tbody></table><a href="javascript:void(0)" class="evaluate fr ml4 delbtn">删除</a><a href="javascript:void(0)" class="evaluate fr submitbtn">保存</a></div></div>';
		}
		$('#freightTempList').append($temp);
		$('#addFreight').hide();
		$('html').removeClass('overflow-hidden');
	});
	//关闭弹框
	$('.wnd_Close_Icon').click(function() {
		$(this).parents('.popWrapper').hide();
		$('html').removeClass('overflow-hidden');
	});
	//展开模板列表
	$(document).on('click','.extendbtn',function(){
		var $me = $(this);
		var $parents = $me.parents('.freightTemp');
		var txt = $me.find('i');
		if(txt.hasClass('down')){
			$parents.find('.extendcontent').show();
			$me.html('<i class="iconfont up">&#xe626;</i>');
			$parents.css('border','1px solid #FFF');
			$parents.find('.titleName').removeAttr('readonly');
			$parents.find('.titleName').css('border','1px solid #bebebe');
		}else {
			$parents.find('.extendcontent').hide();
			$me.html('<i class="iconfont down">&#xe627;</i>');
			$parents.css('border','1px solid #ccc');
			$parents.find('.titleName').attr('readonly',true);
			$parents.find('.titleName').css('border','1px solid #fff');
		}
	});
	//点击添加行
	$(document).on('click', '.additem', function() {
		var $me = $(this),
			  $parents = $me.parents('.freightTemp');
		var $trs = $parents.find('tbody>tr');
		var provinceId = [];
		$.each($trs,function(index,obj){
			var province = $(obj).find('input[name=provinceId]').val();
			if(!province) return;
			provinceId = $.merge(provinceId,province.split(','));
		});
		var mtype = $parents.find('input[name=tempType]').val();
		if(mtype=='static'){
			$(this).parents('tr').before('<tr><td><div class="tdcell"><div class="province">未选择</div><input type="hidden" name="provinceId"/></div></td><td><div class="tdcell"><input type="text" class="inputText short priceitem" name="" value="0" /><i class="iconfont icondelbtn" style="display:inline-block">&#xe606;</i></div></td></tr>');
		}else{
			$(this).parents('tr').before('<tr><td><div class="tdcell"><div class="province">未选择</div><input type="hidden" name="provinceId" value=""></div></td><td><div class="tdcell"><input type="text" class="inputText short firstWeight" name="" value="0" /></div></td><td><div class="tdcell"><input type="text" class="inputText short firstPrice" name="" value="0" /></div></td><td><div class="tdcell"><input type="text" class="inputText short nextWeight" name="" value="0" /></div></td><td><div class="tdcell"><input type="text" class="inputText short nextPrice" name="" value="0" /><i class="iconfont icondelbtn">&#xe606;</i></div></td></tr>');
		}
		if(provinceId.length == CDT.provinceCache.length-1){
			$me.parents('.addTemp').remove();
		}
	});
	//点击选择省份
	$(document).on('click', '.province', function() {
		var $me = $(this);
		var $parents = $me.parents('.freightTemp');
		var index = $me.parents('tr').index();
		var $index = $parents.index();
		$me.parents('tr').attr('data-tid',index);
		$('input[name=province]').val($me.parents('tr').data('tid'));
		//读取本条tr已选省份
		var chooseIdArr = $(this).next().val().split(',');
		var $chooseHtml = '';
		$.each(chooseIdArr, function(index, id) {
			// 通过id 获取数据
			var pname = CDT.provinceMap[id];
			if (pname) {
				$chooseHtml += '<li class="chooseitem" data-id="' + id + '"><div class="tip">移除</div><span>' + pname + '</span></li>';
			}
		});
		$('#chooseList ul').html($chooseHtml);
		// 显示未选择
		//读取本表所有tr已选省份
		var choosedIdall = $(this).parents('tbody').find('input[type=hidden]');
		var choosedIdallArr = [];
		$.each(choosedIdall,function(index,obj){
			var currArr = $(obj).val().split(',');
			choosedIdallArr = $.merge(choosedIdallArr,currArr);
		});
		var $choosedHtml = '';
		$.each(CDT.provinceCache, function(index, obj) {
			if($.inArray(obj.id+"",choosedIdallArr)==-1){
				$choosedHtml += '<li class="provenceItem" data-id="' + obj.id + '"><div class="tip">选择</div><span>' + obj.name + '</span></li>';
			}
		});
		$('#addProvence').data('index', $index);
		$('#leaveList ul').html($choosedHtml);
		Tr.popup('addProvence');
	});

	//hover选择省份样式
	$(document).on('hover','#leaveList ul li',function(){
		$(this).find('.tip').toggle();
	});
	$(document).on('hover','#chooseList ul li',function(){
		$(this).find('.tip').toggle();
	});
	//点击添加省份
	$(document).on('click', '.provenceItem', function() {
		$(this).addClass('chooseitem').removeClass('provenceItem').appendTo($('#chooseList ul'));
		$(this).find('.tip').text('移除');
	});
	//点击去除省份
	$(document).on('click', '.chooseitem', function() {
		$(this).addClass('provenceItem').removeClass('chooseitem').appendTo($('#leaveList ul'));
		$(this).find('.tip').text('选择');
	});
	//确认添加省份
	$(document).on('click', '#btnProvence', function() {
		var index = $('input[name=province]').val();
		var $index = $('#addProvence').data('index');
		var choosed = [],
			list = [],
			plen = $('#leaveList li').length;
		var $parents = $('#freightTempList .freightTemp').eq($index).find('tbody tr');
		var $province = $parents.eq(index).find('.province');
		$.each($('#chooseList ul li'), function(index, obj) {
			choosed.push($(obj).data('id'));
			list.push($(obj).find('span').text());
		});
		if (choosed.length <= 0 && list.length <= 0) {
			$province.text('未选择');
		} else {
			$province.text(list.join(' , '));
		}
		$province.next().val(choosed.join(','));
		var i = 0,
				provinceId = [];
		$.each($parents,function(index,obj){
			var province = $(obj).find('input[name=provinceId]').val();
			if(!province) return;
			provinceId = $.merge(provinceId,province.split(','));
		});
		if(provinceId.length>=CDT.provinceCache.length){
			$('.addTemp').remove();
		}else{
			var $container = $('#freightTempList .freightTemp').eq($index).find('tbody');
			var mtype = $('#freightTempList .freightTemp').eq($index).find('input[name=tempType]').val();
			var $add = '';
			if(mtype=='static'){
				$add = $('<tr class="addTemp"><td colspan="2"><div class="tdcell additem"><i class="iconfont">&#xe60b;</i></div></td></tr>');
			}else {
				$add = $('<tr class="addTemp"><td colspan="5"><div class="tdcell additem"><i class="iconfont">&#xe60b;</i></div></td></tr>');
			}
		 	if($container.find('.addTemp').length<1&&choosed.length >= 0){
		 		$container.append($add);
		 	}
		}
		if(choosed.length==0&&plen==1) {
 			$('.addTemp').remove();
 		}
		if(plen==0){
			$.each($parents,function(index,obj){
				if($(obj).find('.province').text()=='未选择'){
					$(obj).remove();
				}
			});
		}
		$('#addProvence').hide();
		$('html').removeClass('overflow-hidden');
	});
	//校验价格Input
	$(document).on('change','.short',function(){
		var reg = /^(?:\d+|\d{1,3}(?:,\d{3})+)?(?:\.\d+)?$/;
		var $me = $(this),
				price = $me.val();
		if(!reg.test(price)){
			$me.data('valid',false);
			$me.before('<div class="prompt">格式不正确</div>');
			setTimeout(function(){
				$me.prev().remove();
			},2000);
		}else{
			$me.data('valid',true);
			$me.val(price);
			$me.parent().attr('data-price',price);
		}	
	});
	//保存当前模板
	$(document).on('click', '.submitbtn', function() {
		var arr = [];
		$.each($('.short'),function(i,obj){
			if($(obj).data('valid')===false){
				arr.push($(obj));
			}
		});
		if(arr.length>0){
			alert('当前有不正确的输入项，请修改后保存');
			arr = [];
			return;
		}
		var $me = $(this),
			  $parents = $me.parents('.freightTemp');
		//检查模板名称是否重复
		var itemname = [];
		var $itemnames = $parents.siblings().find('.titleName');
		$itemnames.each(function(){
			itemname.push( $( this ).val());
		});
		var has = false;
		var name = $parents.find('.titleName').val();
		if(name==''){
			alert('模板名称不能为空');
			return;
		}
		$.each(itemname,function(index,obj){
			if($.trim(name)==$.trim(obj)){
				alert('模板名称重复');
				has = true;
			}
		});
		if(has) return;
		// 校验
		var pricehas = true,weighthas = true;
		var inputPrice = $parents.find('input[name=price]');
		$.each(inputPrice,function(index,obj){
			if(!$(obj).val()) {
				pricehas = false;
			}
		});
		if(!pricehas){
			alert('价格填写不完整');
			return;
		}
		var inputWeight = $parents.find('input[name=weight]');
		if(inputWeight){
			$.each(inputWeight,function(index,obj){
				if(!$(obj).val()) {
					weighthas = false;
				}
			});
		}
		if(!weighthas){
			alert('重量填写不完整');
			return;
		}
		//参数
		var index = $parents.index();
		var tempId = $parents.find('input[name=tempId]').val();
		var tempType = $parents.find('input[name=tempType]').val();
		var $trs = $parents.find('tbody>tr');
		var title = $parents.find('.titleName').val();
		if(!$trs.length) return;
		var params = {
			'freightTemp._id':tempId,
			'freightTemp.tempName':title,
			'freightTemp.tempType':tempType
		}
		var i = 0,
				provinceId = [];
		$.each($trs,function(index,obj){
			var province = $(obj).find('input[name=provinceId]').val();
			if(!province) return;
			provinceId = $.merge(provinceId,province.split(','));
			params['freightTemp.freights['+i+'].provinces'] = province;
			if(tempType=='static'){
				var price = $(obj).find('.priceitem').val();
				params['freightTemp.freights['+i+'].price'] = price;
			}else {
				var firstWeight = $(obj).find('.firstWeight').val();
				var firstPrice = $(obj).find('.firstPrice').val();
				var nextWeight = $(obj).find('.nextWeight').val();
				var nextPrice = $(obj).find('.nextPrice').val();
				params['freightTemp.freights['+i+'].firstWeight'] = firstWeight;
				params['freightTemp.freights['+i+'].fwPrice'] = firstPrice;
				params['freightTemp.freights['+i+'].addedWeight'] = nextWeight;
				params['freightTemp.freights['+i+'].awPrice'] = nextPrice;
			}
			i++;
		});
		if(provinceId.length<CDT.provinceCache.length){
			alert('还有地区没有设置！');
			return;
		}
		Tr.post('/supplier/freighttemp/save',params, function(data) {
				if (data.code != 200) {
					alert(data.msg);
					return;
				}
				alert('保存成功');
				loadFreightList();
			},{
				loadingMask:false
			});
		
	});
	//删除当前行
	$(document).on('click','.icondelbtn',function(){
		if (!confirm('确定删除当前行吗？')) {
			return;
		}
		$me = $(this);
		var $container = $me.parents('.freightTemp').find('tbody');
		var mtype = $me.parents('.freightTemp').find('input[name=tempType]').val();
		var $add = '';
		if(mtype=='static'){
			$add = $('<tr class="addTemp"><td colspan="2"><div class="tdcell additem"><i class="iconfont">&#xe60b;</i></div></td></tr>');
		}else {
			$add = $('<tr class="addTemp"><td colspan="5"><div class="tdcell additem"><i class="iconfont">&#xe60b;</i></div></td></tr>');
		}
	 	if($container.find('.addTemp').length<1){
	 		$container.append($add);
	 	}
	 	$me.parents('tr').remove();
	});
	//删除模板
	$(document).on('click','.delbtn',function(){
		var $freightTemp = $(this).parents('.freightTemp');
		var tempId  = $freightTemp.find('input[name=tempId]').val();
		if(!tempId){
			$freightTemp.remove();
			return;
		}
		if (!confirm('确定删除该模板吗？')) {
			return;
		}
		Tr.post('/supplier/freighttemp/delete', {
			'id': tempId
		}, function(data) {
			if (data.code != 200) {
				alert(data.msg);
				return;
			}
			alert('删除成功');
			loadFreightList();
		});
	});
}
$(function() {
	CDT.static_freightTemp = $('#staticFreightTemp').remove().val();
	Mustache.parse(CDT.static_freightTemp);
	CDT.dynamic_freightTemp = $('#dynamicFreightTemp').remove().val();
	Mustache.parse(CDT.dynamic_freightTemp);
	loadProvinceData();
	initBase();
});

function loadProvinceData() {
	if (CDT.provinceCache && CDT.provinceCache.length > 0) {
		return;
	}
	//加载省份
	Tr.get('/dpl/province', {
	}, function(data) {
		if (data.code != 200) return;
		CDT.provinceCache = data.results;
		// 建立键值对
		$.each(CDT.provinceCache, function(index, obj) {
			CDT.provinceMap[obj.id] = obj.name;
		});
		loadFreightList();
	});
}
//加载模板列表
function loadFreightList(){
	Tr.get('/supplier/freighttemp/query', {}, function(data) {
		if (data.code != 200 || !data.results) return;
		// 保存到缓存
		CDT.loadedFreightCache = data.results;
		var freightTempList = "";
		$.each(data.results,function(index,temp){
			if(temp.tempType&&temp.tempType=='dynamic'){
				// 动态模版
				freightTempList+=loadDynamicFreight(temp);
			}else{
				// 静态模版
				freightTempList+=loadStaticFreight(temp);
			}

		});
		$('#freightTempList').html(freightTempList);
	});
}


function loadDynamicFreight(temp){
	// 输出到html
	var temp = {'temp':temp};
	var output = Mustache.render(CDT.dynamic_freightTemp, $.extend(temp, {
		provincesStr:function(){
			// [1000,2000]
			var provinceArr = this.provinces;
			return provinceArr.join(',');
		},
		provincesName:function(){
			var provinceArr = this.provinces;
			var provincesName = [];
			// 循环省份ID列表，转化为省份名称列表
			$.each(provinceArr,function(index,obj){
				provincesName.push(CDT.provinceMap[obj]);
			}); 
			return provincesName.join(' , ');
		},
		'awPriceStr':function(){
			if(this.awPrice){
				return this.awPrice/100;
			}
			return 0;
		},
		'fwPriceStr':function(){
			if(this.fwPrice){
				return this.fwPrice/100;
			}
			return 0;
		}
	}));
	return output;
	
}

function loadStaticFreight(temp){
	// 输出到html
	var temp = {'temp':temp};
	var output = Mustache.render(CDT.static_freightTemp, $.extend(temp, {
		provincesStr:function(){
			// [1000,2000]
			var provinceArr = this.provinces;
			return provinceArr.join(',');
		},
		provincesName:function(){
			var provinceArr = this.provinces;
			var provincesName = [];
			// 循环省份ID列表，转化为省份名称列表
			$.each(provinceArr,function(index,obj){
				provincesName.push(CDT.provinceMap[obj]);
			}); 
			return provincesName.join(' , ');
		},
		'priceStr':function(){
			if(this.price){
				return this.price/100;
			}
			return 0;
		}
	}));
	return output;
}









