CDT = {
	itemTemp: null,
	uptoken: '',
	loadedListCache: {},
	currPageNo: 1,
	pageSize: 10,
	itemStatus:'ONLINE'
};
function initBase(){
	// 商品展示分类
	$(document).on('click', '#online', function() {
		$(this).addClass('active');
		$('#outline').removeClass('active');
		CDT.itemStatus = 'ONLINE';
		$('#hideBtn').show();
		$('#onlineBtn').hide();
		loadList(1);
	});

	// 查询库存宝贝
	$(document).on('click', '#outline', function() {
		$(this).addClass('active');
		$('#online').removeClass('active');
		// 查询状态为下架
		CDT.itemStatus = 'HIDE';
		// 隐藏下架按钮
		$('#hideBtn').hide();
		$('#onlineBtn').show();

		loadList(1);
	});

	// 关键字查询商品
	$(document).on('click', '#listSearchBtn', function() {
		loadList(1);
	});
	// 全选按钮
	$(document).on('change', '#choolseAll', function() {
		var checked =  $(this).prop('checked');
		$.each($('#listContainer .iCheck'),function(index,element){
			$(element).prop('checked',checked);
		});
	});
	// 全选状态改变
	$(document).on('change', '.iCheck', function() {
		$('#choolseAll').prop('checked',false);
	});

	// 删除选中
	$(document).on('click', '#deleteBtn', function() {
		var ids = new Array();
		$.each($('#listContainer tr'),function(index,element){
			var $isCheck = $(element).find('.iCheck').eq(0);
			if($isCheck.prop('checked')==true){
				ids.push($(element).attr('mid'));
			}
		});
		if(!ids||ids.length<=0){
			alert('请选择要删除的项');
			return;
		}
		if (!confirm('确定删除商品吗？')) {
			return;
		}
		Tr.post('/supplier/item/dellist', {
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

	// 下架选中
	$(document).on('click', '#hideBtn', function() {
		var ids = new Array();
		$.each($('#listContainer tr'),function(index,element){
			var $isCheck = $(element).find('.iCheck').eq(0);
			if($isCheck.prop('checked')==true){
				ids.push($(element).attr('mid'));
			}
		});
		if(!ids||ids.length<=0){
			alert('请选择要下架的项');
			return;
		}
		Tr.post('/supplier/item/hidelist', {
			"itemIds": ids.join(',')
		}, function(data) {
			if (data.code != 200) {
				alert('下架失败');
				return;
			}
			alert('下架成功');
			loadList(CDT.currPageNo);
		}, {
				loadingMask: false
			});
	});

	// 上架选中
	$(document).on('click', '#onlineBtn', function() {
		var ids = new Array();
		$.each($('#listContainer tr'),function(index,element){
			var $isCheck = $(element).find('.iCheck').eq(0);
			if($isCheck.prop('checked')==true){
				ids.push($(element).attr('mid'));
			}
		});
		if(!ids||ids.length<=0){
			alert('请选择要上架的项');
			return;
		}
		Tr.post('/supplier/item/online', {
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
	var obj = {
		'vo.pageNo': pageNo,
		'vo.pageSize': CDT.pageSize,
		'vo.title':$('#titleName').val()?$('#titleName').val():'',
		'vo.outNo':$('#goodNum').val()?$('#goodNum').val():'',
		'vo.id':$('#titleId').val()?$('#titleId').val():'',
		'vo.status':CDT.itemStatus,
		'vo.brandId':!$('#titleSel').val()?0:$('#titleSel').val()
	};
	Tr.get('/supplier/item/query', obj, function(data) {
		if (data.code != 200||!data.results) return;
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
		if(data.totalCount<=0){
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
			prev_text:' '
		});
		CDT.currPageNo = pageNo;
	}, {
				loadingMask: false
			});
}
function loadListCallBack(index, jq) {
	loadList(index + 1);
}
$(function() {
	CDT.itemTemp = $('#itemTemp').remove().val();
	Mustache.parse(CDT.itemTemp);
	initBase();
	loadList(1);
});
