CDT = {
	currPageNo: 1,
	pageSize: 20,
	totalCount: 0,
	keyWord: '',
	brandId: 0,
	cateId: 0,
	dp_use: false,
	pr_use: false
};

function queryParams(){
	var r = window.location.search.substr(1);
	var kvs = r.split('&');
	var params = {};

	for (var i = 0; i < kvs.length; i++) {
		var kv = kvs[i].split('=');
		if(kv[0]){
			params[kv[0]] = kv[1];			
		}
	}
	return params;
}
function pageNoUrl(pageNo) {
	var params = queryParams();
	params.pageNo = pageNo;
	var resArr = [];
	$.each(params, function(k, v) {
		resArr.push(k + '=' + v);
	});
	return resArr.join('&');
}
// 初始化
$(function() {
	// 图片懒加载
	$("img.lazyImg").lazyload({
	      effect : "fadeIn",
	        threshold : 200
	  });
	// 品牌点击
	$(document).on('click', '#BrandList input[type=checkbox],#CategoryList input[type=checkbox],#Cooperation input[type=checkbox]', function() {
		// 查找当前父元素下所有同类别元素(ul)
		var $me = $(this);
		var $checkboxs = $(this).parent().parent().find('input[type=checkbox]');
		$.each($checkboxs, function(index, obj) {
			if ($(obj).val() != $me.val()) {
				$(obj).prop('checked', false);
			}
		});
		defaultUrlSearch();
	});
	initPaginator();
});
// 初始化分页器
function initPaginator() {

	// 自行实现分页
	if (CDT.totalCount == 0) {
		$('.pagination').hide();
		return
	};
	var pagination = new Paginator(CDT.pageSize).build(CDT.totalCount, CDT.currPageNo);

	var phtml = '';
	if (pagination.has_previous_page) {
		phtml += '<a class="T_prev" href="?' + pageNoUrl(CDT.currPageNo - 1) + '">上一页</a>';
	}

	if (pagination.display_first_page) {
		phtml += '<a href="?' + pageNoUrl(1) + '">' + 1 + '</a>';
		phtml += '<span>...</span>';
	}

	for (var i = pagination.first_page; i <= pagination.last_page; i++) {
		var classstr = i == CDT.currPageNo ? 'class="current"' : '';
		var link = i == CDT.currPageNo ? 'javascript:;' : '?' + pageNoUrl(i);
		phtml += '<a ' + classstr + ' href="' + link + '">' + i + '</a>';
	}

	if (pagination.display_last_page) {
		phtml += '<span>...</span>';
		phtml += '<a href="?' + pageNoUrl(pagination.total_pages) + '">' + pagination.total_pages + '</a>';
	}

	if (pagination.has_next_page) {
		phtml += '<a class="T_next" href="?' + pageNoUrl(CDT.currPageNo + 1) + '">下一页</a>';
	}

	$('.pagination').html(phtml).show();
}

// 默认搜索操作
function defaultUrlSearch() {
	var url = "/search?pageNo=1";
	url = urlParse(url);
	window.location.href = url;
}

function urlParse(url) {
	// 类目条件
	var cateId = $('#CategoryList input[name=CategoryCD]:checked').eq(0).val();
	// 品牌条件
	var brandId = $('#BrandList input[name=BrdCD]:checked').eq(0).val();
	// 价格条件1
	var dp_use = $('#FreeDelivery').prop('checked');
	var pr_use = $('#SalePst').prop('checked');
	// 价格条件2
	if (cateId && cateId > 0) {
		url += "&cateId=" + cateId;
	}
	if (brandId && brandId > 0) {
		url += "&brandId=" + brandId;
	}
	if (dp_use && dp_use == true) {
		url += "&dp_use=true";
	}
	if (pr_use && pr_use == true) {
		url += "&pr_use=true";
	}
	// 关键字条件
	if (CDT.keyWord && CDT.totalCount > 0) {
		url += "&kw=" + CDT.keyWord;
	}
	return url;
}