CDT = {
	chatTemp: null,
	loadedChatCache: {},
	currPageNo: 1,
	pageSize: 20
};
function initBase(argument) {
	// 关键字查询品牌
	$(document).on('click', '#chatSearchBtn', function() {
		loadChatList(1);
	});

	// 品牌查询enter
	$(document).on('keypress','#chatName',function(e){
		e.preventDefault();
		e.stopPropagation();
		if (e.keyCode == 13) {
			loadChatList(1);
		}
	});

	/*删除行*/
	$(document).on('click', '.delbtn', function() {
		var mid = $(this).parents('tr').attr('mid');
		if (!confirm('确定删除该用户吗？')) {
			return;
		}
		Tr.post('/sys/wechat/user/delete', {
			"id": mid			
		}, function(data) {
			if (data.code != 200) {
				alert(data.msg);
				return;
			}
			alert('删除成功');
			loadChatList(CDT.currPageNo);
		},{loadingMask: false});
	});

}

$(function() {
	CDT.chatTemp = $('#chatTemp').remove().val();
	Mustache.parse(CDT.chatTemp);
	initBase();
	loadChatList(1);
});

// 加载品牌列表
function loadChatList(pageNo) {
	Tr.get('/sys/wechat/user/query', {
		'vo.pageSize': CDT.pageSize,
		'vo.pageNo': pageNo,
		'vo.nick':$('#chatName').val()?$('#chatName').val():''
	}, function(data) {
		if (data.code != 200||!data.results) return;
		// 保存到缓存
		CDT.loadedChatCache = data.results;
		// 输出到html
		var output = Mustache.render(CDT.chatTemp, $.extend(data, {
			formatauthTime: function() {
				if (this.authTime) {
					return new Date(this.authTime).Format('yyyy-MM-dd hh:mm:ss');
				}
				return '----';
			},
			formatlastAuthTime: function() {
				if (this.lastAuthTime) {
					return new Date(this.lastAuthTime).Format('yyyy-MM-dd hh:mm:ss');
				}
				return '----';
			}
		}));
		$('#wechatContainer tbody').html(output);
		if(!data.totalCount||data.totalCount<=0){
			$('#wechatContainer .pagination').hide();
			return;
		}
		$('#wechatContainer .pagination').show().pagination(data.totalCount, {
			items_per_page: CDT.pageSize,
			num_display_entries: 5,
			current_page: pageNo,
			num_edge_entries: 5,
			callback: loadChatCallBack,
			callback_run: false,
			prev_text:' '
		});
		CDT.currPageNo = pageNo;
	}, {
				loadingMask: false
			});
}

function loadChatCallBack(index, jq) {
	loadChatList(index+1);
}
