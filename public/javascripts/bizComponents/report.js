CDT = {
	token: null,
	uptoken: null,
	reportTemp: null,
	loadedReportCache: {},
	currPageNo: 1,
	pageSize: 10
};
function initBase(){
	$('.imgCaptcha').click(function() {
		$(this).attr('src', '/captcha?r=' + Math.random());
	});
	$(document).on('click','.moreBtn',function(){
		loadReportCallBack();
	});
}
function loadReportCallBack(index, jq) {
	loadReportList(index+1);
}
// 加载评论列表
function loadReportList(pageNo) {
	Tr.get('', {
		'mvo.pageSize': CDT.pageSize,
		'mvo.pageNo': pageNo
	}, function(data) {
		if (data.code != 200||!data.results||data.results.length <= 0) return;
		// 保存到缓存
		CDT.loadedReportCache = data.results;
		// 输出到html
		var output = Mustache.render(CDT.reportTemp, $.extend(data, {}));
		$('#list_template').html(output);//append追加是不是要条件判断
		CDT.currPageNo = pageNo;
	});
}
$(function() {
	CDT.reportTemp = $('#reportTemp').remove().val();
	Mustache.parse(CDT.reportTemp);
	initBase();
	loadReportList(1);
});