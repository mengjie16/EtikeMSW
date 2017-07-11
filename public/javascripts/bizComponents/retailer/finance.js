CDT = {
	financeTemp: null,
	loadedListCache: {},
	currPageNo: 1,
	pageSize: 10,
}
//初始化操作
function initBase(){
	var currentTime = new Date().Format("yyyy-MM-dd");
	$('.inputDate').datePicker({
		startDate: '2015-01-01',
		endDate: currentTime,
		clickInput: true,
		verticalOffset: 30,
		horizontalOffset: -26
	});
	//查询
	$(document).on('click','#searchBtn',function () {
		loadList(CDT.currPageNo);
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
}

//加载订单列表
function loadList(pageNo) {
	var obj = {
		'vo.id':$('#serialNo').val(),
		'vo.pageNo': pageNo,
		'vo.pageSize': CDT.pageSize,
		'vo.createTimeStart': $('#startTime').val(),
		'vo.createTimeEnd': $('#endTime').val()
	};
	Tr.get('', obj, function(data) {
		if (data.code != 200||!data.results) return;
		// 保存到缓存
		CDT.loadedListCache = data.results;
		var output = Mustache.render(CDT.financeTemp, $.extend(data, {}));
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

$(function(){
	CDT.financeTemp = $('#financeTemp').remove().val();
	Mustache.parse(CDT.financeTemp);
	initBase();
	loadList(1);
});