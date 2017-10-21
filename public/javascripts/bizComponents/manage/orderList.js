CDT = {
	orderTemp: null,
	loadedListCache: {},
	currPageNo: 1,
	pageSize: 10,
}
//初始化操作
function initBase(){
	var currentTime = new Date().Format("yyyy-MM-dd");
	$('.inputDate').datePicker({
		startDate: '2016-01-01',
		endDate: currentTime,
		clickInput: true,
		verticalOffset: 30,
		horizontalOffset: -26
	});
	//查询
	$(document).on('click','#searchBtn',function () {
		loadList(CDT.currPageNo);
	});
	//取消订单
	$(document).on('click','.cancelbtn',function () {
		var $me = $(this);
		if (!confirm('确定取消该订单吗？')) {
			return;
		}
		var tradeId = $me.parents('.orderItem').find('input[name=tradeId]').val();
		Tr.post('/retailer/trade/cancel', {
			"tradeId":tradeId
		}, function(data) {
			if (data.code != 200) {
				alert('取消失败');
				return;
			}
			loadList(CDT.currPageNo);
		});
	});
	//比较查询开始时间与结束时间大小
	$(document).on('change','.inputDate',function () {
		var startTime=$("#startTime").val();  
	  var start=new Date(startTime.replace("-", "/").replace("-", "/"));  
	  var endTime=$("#endTime").val();  
	  var end=new Date(endTime.replace("-", "/").replace("-", "/"));  
	  if(end<start){  
	      $(this).val('');
	      $('.errorDate').text('结束时间不得小于开始时间');
	  }else{
	  	$('.errorDate').text('');
	  	return true; 
	  } 
	});
	//校验订单号input
	$(document).on('change','#orderNo',function(){
		var reg = /^[1-9]\d*$/;
		var $me = $(this),
				id = $me.val();
		if(!reg.test(id)){
			if(!id){
				$me.next().text('');
				return;
			}
			$me.next().text('格式不正确');
		}else{
			$me.next().text('');
		}	
	});
	//校验用户号码input
	$(document).on('change','#userPhone',function(){
		var reg = /^[1-9]\d*$/;
		var $me = $(this),
				id = $me.val();
		if(!reg.test(id)){
			if(!id){
				$me.next().text('');
				return;
			}
			$me.next().text('格式不正确');
		}else{
			$me.next().text('');
		}	
	});
}


//加载订单列表
function loadList(pageNo) {
	var obj = {
		'vo.id':$('#orderNo').val(),
		'vo.phone':$('#userPhone').val(),
		'vo.pageNo': pageNo,
		'vo.pageSize': CDT.pageSize,
		'vo.createTimeStart': $('#startTime').val(),
		'vo.createTimeEnd': $('#endTime').val(),
		'vo.phone': $('#userPhone').val()
	};
	Tr.get('/sys/trade/query', obj, function(data) {
		if (data.code != 200||!data.results) return;
		// 保存到缓存
		CDT.loadedListCache = data.results;
		var output = Mustache.render(CDT.orderTemp, $.extend(data, {
			'paymentStr':function(){
				if(this.cargoFee){
					return this.cargoFee;
				}
				return 0;
			},
			'shippingFeeStr':function(){
				if(this.shippingFee){
					return this.shippingFee;
				}
				return 0;
			},
			'totalAmountStr':function(){
				if(this.totalFee){
					return this.totalFee;
				}
				return 0;
			},
			'realityPaymentStr':function(){
				if(this.payment){
					return this.payment;
				}
				return 0;
			},
			'discountStr':function(){
				if(this.discountFee){
					return this.discountFee;
				}
				return 0;
			}
		}));
		$('#listContainer').html(output);
		if(data.totalCount<=0){
			$('.searchResult').text('搜索结果无');
			$('.pagination').hide();
			return;
		}
		$('.searchResult').text('');
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
				loadingMask: true
			});
}
function loadListCallBack(index, jq) {
	loadList(index + 1);
}

$(function(){
	CDT.orderTemp = $('#orderTemp').remove().val();
	Mustache.parse(CDT.orderTemp);
	initBase();
	loadList(1);
});