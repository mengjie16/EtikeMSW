CDT = {
	token: null,
	uptoken: null,
	storeTemp: null,
	loadedStoreCache: {},
	currPageNo: 1,
	pageSize: 20
};
function initBase(argument) {
	$('.storeblock').hover(function(){
		$(this).find('.mark').toggle().next().toggle();
	});
}
// 加载收藏列表
function loadStoreList(pageNo) {
	Tr.get('', {
		'bvo.pageSize': CDT.pageSize,
		'bvo.pageNo': pageNo,
		'bvo.name':$('#titleName').val()?$('#titleName').val():''
	}, function(data) {
		if (data.code != 200||!data.results||data.results.length <= 0) return;
		// 保存到缓存
		CDT.loadedStoreCache = data.results;
		// 输出到html
		var output = Mustache.render(CDT.storeTemp, $.extend(data, {}));
		$('#storeContainer').html(output);
		$('.pagination').pagination(data.totalCount, {
			items_per_page: CDT.pageSize,
			num_display_entries: 5,
			current_page: pageNo,
			num_edge_entries: 5,
			callback: loadStoreCallBack,
			callback_run: false,
			prev_text:' '
		});
		CDT.currPageNo = pageNo;
	}, {
				loadingMask: false
			});
}

function loadStoreCallBack(index, jq) {
	loadStoreList(index+1);
}

$(function() {
	CDT.storeTemp = $('#storeTemp').remove().val();
	Mustache.parse(CDT.storeTemp);
	initBase();
	loadStoreList(1);
});