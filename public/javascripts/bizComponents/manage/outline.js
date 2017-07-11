function initBaseInfo(){
	$('.topbar-btn-a').click(function(event) {
		var $hide = $(this).next('div.ng-hide');
		if($hide.is(':visible')){
			$hide.hide();
			$(this).removeClass('topbar-btn-h');
		}else{
			$hide.show();
			$(this).addClass('topbar-btn-h');
		}
	});
	$('.sidebar-fold').click(function(){
		var $hide = $('.sidebar-title-text');
		if($hide.is(':visible')){
			var options = {container:'body',placement:'right'};
			$('.sidebar-trans').tooltip(options);
			$hide.hide();
			$('.viewFramework-body').removeClass('viewFramework-sidebar-full').addClass('viewFramework-sidebar-mini');
			$(this).css('width','50px').find('.iconfont').removeClass('trans');
		}else{
			$hide.show();
			$('.viewFramework-body').removeClass('viewFramework-sidebar-mini').addClass('viewFramework-sidebar-full');
			$(this).css('width','180px').find('.iconfont').addClass('trans');
			$('.sidebar-trans').tooltip('destroy');
		}
	});
	$('.sidebar-title').click(function(){
		var $hide = $(this).next('ul');
		if($hide.is(':visible')){
			$hide.hide();
			$(this).find('.caret').removeClass('caret-d').addClass('caret-c');
		}else{
			$hide.show();
			$(this).find('.caret').removeClass('caret-c').addClass('caret-d');
		}
	});
	$('.product-nav-list>ul>li>div').click(function(){		
		if($(this).next().is(':visible')){
			$(this).next().hide();
			$(this).find('span').addClass('icon-arrow-right');
		}else{
			$(this).next().show();
			$(this).find('span').removeClass('icon-arrow-right');
		}		
	});
	//去掉form默认enter提交
	$(document).on('keypress','input',function(e){
		if(e.keyCode == 13){
			return;
		}
	});
	// 关键字查询商品
	$(document).on('click', '#listSearchBtn', function() {
		var itemNum = $('#').val();
		if(itemNum!=''&&!/^[0-9a-zA-Z_-]+$/.test(itemNum)||Tr.countWords(itemNum)>8) return;
		var itemTitle = $('#itemTitle').val();
		if(Tr.countWords(itemTitle)>10) return;
		loadList(1);
	});
	// 全选按钮
	$(document).on('change', '#choolseAll', function() {
		var checked = $(this).prop('checked');
		$.each($('#listContainer .iCheck'), function(index, element) {
			$(element).prop('checked', checked);
		});
	});
	// 全选状态改变
	$(document).on('change', '.iCheck', function() {
		$('#choolseAll').prop('checked', false);
	});
	// 删除选中
	$(document).on('click', '#deleteBtn', function() {
		if (!confirm('确定删除商品吗？')) {
			return;
		}
		var ids = new Array();
		$.each($('#listContainer tr'), function(index, element) {
			var $isCheck = $(element).find('.iCheck').eq(0);
			if ($isCheck.prop('checked') == true) {
				ids.push($(element).attr('mid'));
			}
		});
		if (!ids || ids.length <= 0) {
			alert('请选择要删除的项');
			return;
		}
		Tr.post('/sys/item/dellist', {
			"itemIds": ids.join(',')
		}, function(data) {
			if (data.code != 200) {
				alert('删除失败');
				return;
			}
			alert('删除成功');
			loadList(CDT.currPageNo);
		}, {
			loadingMask: false
		});
	});
	// 上架选中
	$(document).on('click', '#onlineBtn', function() {
		var ids = new Array();
		$.each($('#listContainer tr'), function(index, element) {
			var $isCheck = $(element).find('.iCheck').eq(0);
			if ($isCheck.prop('checked') == true) {
				ids.push($(element).attr('mid'));
			}
		});
		if (!ids || ids.length <= 0) {
			alert('请选择要上架的项');
			return;
		}
		Tr.post('/sys/item/online', {
			"itemIds": ids.join(',')
		}, function(data) {
			if (data.code != 200) {
				alert('上架失败');
				return;
			}
			alert('上架成功');
			loadList(CDT.currPageNo);
		}, {
			loadingMask: false
		});
	});
}
//加载列表
function loadList(pageNo) {
	var searchVo = {
		'vo.pageNo':pageNo,
		'vo.pageSize':CDT.pageSize,
		'vo.outNo': $('#itemNum').val(),
		'vo.id': $('#titleId').val(),
		'vo.title': $('#itemTitle').val(),
		'vo.aboutBrand': $('#brandKey').val()
	};
	Tr.get('/sys/item/query', searchVo, function(data) {
		if (data.code != 200 || !data.results) return;
		// 保存到缓存
		CDT.loadedListCache = data.results;
		var output = Mustache.render(CDT.itemTemp, $.extend(data, {
			formatTimer: function() {
				if (this.updateTime) {
					return new Date(this.updateTime).Format('yyyy-MM-dd hh:mm:ss');
				}
				return '----';
			},
			formatPrice: function() {
				var iprice=0;
				if(this.distPriceUse&&this.distPrice>0){
						iprice = this.distPrice;
					}else if(this.priceRangeUse&&this.priceRanges&&this.priceRanges.length>0){
						iprice = this.priceRanges[0].price
					}else{
						iprice = this.retailPrice;
					}
				return iprice/100;
			}
		}));
		$('#listContainer tbody').html(output);
		if (data.totalCount <= 0) {
			$('.pagination').hide();
			return;
		}
		$('.pagination').show();
		$('.pagination').pagination(data.totalCount, {
			items_per_page: CDT.pageSize,
			num_display_entries: 5,
			current_page: pageNo,
			num_edge_entries: 5,
			callback: loadListCallBack,
			callback_run: false,
			prev_text: ' '
		});
		CDT.currPageNo = pageNo;
	}, {
		loadingMask: false
	});
}

function loadListCallBack(index, jq) {
	loadList(index + 1);
}

$(function(){
	loadList(1);
	CDT.itemTemp = $('#itemTemp').remove().val();
	Mustache.parse(CDT.itemTemp);
	initBaseInfo();
});