CDT = {
	token: null,
	uptoken: null,
	activityTemp: null,
	h5activityTemp: null,
	loadedActivityCache: {},
	loadedH5ActivityCache: {},
	currPageNo: 1,
	pageSize: 10
};
function initBase(argument) {
	//创建活动
	$(document).on('click','#activityCreateBtn',function(){
		var title = prompt("请输入活动标题");
	  if (title && title!=""){
	    Tr.post('/sys/activity/create', {
	    	'authenticityToken':$('input[name=authenticityToken]').val(),
				'title': title
			}, function(data) {
				if (data.code != 200) {
					alert('创建失败');
					return;
				}
				window.location.href = '/sys/activity/edit/'+data.results;
			});
	   }
	});

	//创建H5活动
	$(document).on('click','#h5activityCreateBtn',function(){
	  var title = prompt("请输入活动标题");
	  if (title && title!=""){
	    Tr.post('/sys/h5/activity/create ', {
	    	'authenticityToken':$('#h5activityManageContainer input[name=authenticityToken]').val(),
				'title': title
			}, function(data) {
				if (data.code != 200) {
					alert('创建失败');
					return;
				}
				window.location.href = '/sys/h5/activity/edit/'+data.results;
			});
	   }
	});

	/* 删除活动 */
	$(document).on('click', '#activityManageContainer .delbtn', function() {
		var mid = $(this).parents('tr').attr('mid');
		var $me = $(this);
		var id = $me.parents('tr').eq(0).attr('mid');
		if (!confirm('确定删除该活动吗？')) {
			return;
		}
		$.post('/sys/activity/delete', {
			'authenticityToken': $('input[name=authenticityToken]').val(),
			'id': id
		}, function(data) {
			if (data.code != 200) {
				alert('删除失败!');
				return;
			}
			loadActivityList(CDT.currPageNo);
		});
	});

	/* 删除H5活动 */
	$(document).on('click', '#h5activityManageContainer .delbtn', function() {
		var mid = $(this).parents('tr').attr('mid');
		var $me = $(this);
		var id = $me.parents('tr').eq(0).attr('mid');
		if (!confirm('确定删除该活动吗？')) {
			return;
		}
		$.post('/sys/h5/activity/delete', {
			'authenticityToken': $('input[name=authenticityToken]').val(),
			'id': id
		}, function(data) {
			if (data.code != 200) {
				alert('删除失败!');
				return;
			}
			loadH5ActivityList(CDT.currPageNo);
		});
	});

	// 应用和取消活动页面
	$(document).on('click', '#activityManageContainer .changeStatus', function(){
		var use = $(this).data('use');
		var $me = $(this);
		var id = $me.parents('tr').eq(0).attr('mid');
		if(typeof(use)=='undefined'||!id){
			return;
		}
		var title = '确定取消应用该活动设置?';
		if(use==false){
			title = '确定应用该活动设置?';
		}
		if (!confirm(title)) {
			return;
		}
		$.post('/sys/activity/use', {
			'authenticityToken': $('input[name=authenticityToken]').val(),
			'id': id,
			'use':!use
		}, function(data) {
			if (data.code != 200) {
				alert('更新失败!');
				return;
			}
			$me.data('use',!use);
			if(use==false){

				$me.html('&#xe620;');
				$me.css('color','#FECF10');
			}else{
				$me.html('&#xe61f;');
				$me.css('color','#888');	
			}
		});

	});

	// 应用和取消h5活动页面
	$(document).on('click', '#h5activityManageContainer .changeStatus', function(){
		var use = $(this).data('use');
		var $me = $(this);
		var id = $me.parents('tr').eq(0).attr('mid');
		if(typeof(use)=='undefined'||!id){
			return;
		}
		var title = '确定取消应用该活动设置?';
		if(use==false){
			title = '确定应用该活动设置?';
		}
		if (!confirm(title)) {
			return;
		}
		$.post('/sys/h5/activity/use', {
			'authenticityToken': $('#h5activityManageContainer input[name=authenticityToken]').val(),
			'id': id,
			'use':!use
		}, function(data) {
			if (data.code != 200) {
				alert('更新失败!');
				return;
			}
			$me.data('use',!use);
			if(use==false){

				$me.html('&#xe620;');
				$me.css('color','#FECF10');
			}else{
				$me.html('&#xe61f;');
				$me.css('color','#888');	
			}
		});

	});
}

// 初始化数据
$(function() {
	// 活动render模板
	CDT.activityTemp = $('#activityTemp').remove().val();
	Mustache.parse(CDT.activityTemp);
	// h5活动render模板
	CDT.h5activityTemp = $('#h5activityTemp').remove().val();
	Mustache.parse(CDT.h5activityTemp);
	initBase();
	loadActivityList(1);
	loadH5ActivityList(1);
});


// 加载活动列表
function loadActivityList(pageNo) {
	Tr.get('/sys/activity/list', {
		'bvo.pageSize': CDT.pageSize,
		'bvo.pageNo': pageNo
	}, function(data) {
		if (data.code != 200||!data.results) return;
		// 保存到缓存
		CDT.loadedActivityCache = data.results;
		// 输出到html
		var output = Mustache.render(CDT.activityTemp, $.extend(data, {
			itemCount:function(){
				var count = 0;
				if(this.activityItems){
					count += this.activityItems.length;
				}
				if(this.bottomItemIds){
					count += this.bottomItemIds.length;
				}
				return count;
			}

		}));
		$('#activityManageContainer tbody').html(output);
		if(!data.totalCount||data.totalCount<=0){
			$('#activityManageContainer .pagination').hide();
			return;
		}
		$('#activityManageContainer .pagination').show().pagination(data.totalCount, {
			items_per_page: CDT.pageSize,
			num_display_entries: 5,
			current_page: pageNo,
			num_edge_entries: 5,
			callback: loadActivityCallBack,
			callback_run: false,
			prev_text:' '
		});
		CDT.currPageNo = pageNo;
	}, {
				loadingMask: false
			});
}

// 加载活动列表
function loadH5ActivityList(pageNo) {
	Tr.get('/sys/h5/activity/list', {
		'bvo.pageSize': CDT.pageSize,
		'bvo.pageNo': pageNo
	}, function(data) {
		if (data.code != 200||!data.results) return;
		// 保存到缓存
		CDT.loadedH5ActivityCache = data.results;
		// 输出到html
		var output = Mustache.render(CDT.h5activityTemp, $.extend(data, {
			itemCount:function(){
				var count = 0;
				if(this.groupItems){
					$.each(this.groupItems,function(index,obj){
						if(obj){
							count+=obj.length;
						}
					});
				}
				return count;
			}
		}));
		$('#h5activityManageContainer tbody').html(output);
		if(!data.totalCount||data.totalCount<=0){
			$('#h5activityManageContainer .pagination').hide();
			return;
		}
		$('#h5activityManageContainer .pagination').show().pagination(data.totalCount, {
			items_per_page: CDT.pageSize,
			num_display_entries: 5,
			current_page: pageNo,
			num_edge_entries: 5,
			callback: loadH5ActivityCallBack,
			callback_run: false,
			prev_text:' '
		});
		CDT.currPageNo = pageNo;
	}, {
				loadingMask: false
			});
}

function loadActivityCallBack(index, jq) {
	loadActivityList(index+1);
}

function loadH5ActivityCallBack(index, jq) {
	loadH5ActivityList(index+1);
}
