CDT = {
	token: null,
	uptoken: null,
	articleTemp: null,
	loadedArticleCache: {},
	currPageNo: 1,
	pageSize: 10
};

function initBase(argument) {
	/*删除行*/
	$(document).on('click', '.delbtn', function() {
		var mid = $(this).parents('tr').attr('mid');
		if (!confirm('确定删除该文章吗？')) {
			return;
		}
		Tr.post('/sys/article/delone', {
			"id": mid
		}, function(data) {
			if (data.code != 200) {
				alert('删除失败');
				return;
			}
			alert('删除成功');
			loadArticleList(CDT.currPageNo);
		},{loadingMask: false});
	});

	// 添加文章
	$(document).on('click', '#cateArticleBtn', function() {
		window.location.href = "/sys/article/edit";
	});
}

$(function() {
	CDT.articleTemp = $('#articleTemp').remove().val();
	Mustache.parse(CDT.articleTemp);
	initBase();
	loadArticleList(1);
});

// 加载文章列表
function loadArticleList(pageNo) {
	$('#choolseAll').prop('checked',false);
	Tr.get('/dpl/article/query', {
		'vo.pageSize': CDT.pageSize,
		'vo.pageNo': pageNo,
		'vo.isDelete':false,
		'vo.isPublic':true,
		'vo.isList':true
	}, function(data) {
		if (data.code != 200||!data.results||data.results.length <= 0) return;
		// 保存到缓存
		CDT.loadedArticleCache = data.results;
		// 输出到html
		var output = Mustache.render(CDT.articleTemp, $.extend(data, {}));
		$('#articleContainer tbody').html(output);
		if(!data.totalCount||data.totalCount<=0){
			$('#articleContainer .pagination').hide();
			return;
		}
		$('#articleContainer .pagination').show().pagination(data.totalCount, {
			items_per_page: CDT.pageSize,
			num_display_entries: 5,
			current_page: pageNo,
			num_edge_entries: 5,
			callback: loadArticleCallBack,
			callback_run: false,
			prev_text:' '
		});
		CDT.currPageNo = pageNo;
	}, {
				loadingMask: false
			});
}
function loadArticleCallBack(index, jq) {
	loadArticleList(index+1);
}