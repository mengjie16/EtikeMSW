CDT = {
	token: null,
	uptoken: null,
	markTemp: null,
	loadedMarkCache: {},
	currPageNo: 1,
	pageSize: 10
};
function initBase(argument) {
	$('.markContainer').each(function(){
		var length = $(this).find('li').length;
		if(length>10){
			$(this).next().show();
		}
	});
	$(document).on('hover','.markblock',function(){
		$(this).find('.detailBlock').toggle();
	});
	$(document).on('click','.moreBtn',function(){
		loadMarkCallBack();
	});
}
function loadMarkCallBack(index, jq) {
	loadMarkList(index+1);
}
// 加载收藏列表
function loadMarkList(pageNo) {
	Tr.get('', {
		'mvo.pageSize': CDT.pageSize,
		'mvo.pageNo': pageNo
	}, function(data) {
		if (data.code != 200||!data.results||data.results.length <= 0) return;
		// 保存到缓存
		CDT.loadedMarkCache = data.results;
		// 输出到html
		var output = Mustache.render(CDT.markTemp, $.extend(data, {}));
		$('#markContainer').html(output);//append追加是不是要条件判断
		CDT.currPageNo = pageNo;
	});
}

$(function() {
	CDT.markTemp = $('#markTemp').remove().val();
	Mustache.parse(CDT.markTemp);
	initBase();
	loadMarkList(1);
});